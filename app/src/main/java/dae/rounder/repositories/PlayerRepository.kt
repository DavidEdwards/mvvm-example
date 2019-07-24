package dae.rounder.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import dae.rounder.database.AppDatabase
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.PlayerStatus
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.coroutines.CoroutineContext

interface PlayerRepository {
    suspend fun new(displayName: String, avatarPath: String? = null): Player
    fun delete(player: Player)
    fun delete(id: Long)
    fun players(): LiveData<List<Player>>
    suspend fun playersNow(): List<Player>
    fun watchPlayer(playerId: Long)
    fun player(): LiveData<Player?>
    fun watchGame(gameId: Long)
    fun playerStatus(): LiveData<PlayerStatus?>
}

class PlayerRepositoryImpl: PlayerRepository, KoinComponent, CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val db by inject<AppDatabase>()
    private val dao = db.getDatabase().playerDao()

    private val players = dao.list()

    private val playerIdLiveData = MutableLiveData<Long>()
    private val gameIdLiveData = MutableLiveData<Long>()
    private val playerLiveData = Transformations.switchMap(db.isDatabaseCreated()) { created ->
        if(created) {
            Transformations.switchMap(playerIdLiveData) { id ->
                dao.playerById(id)
            }
        } else {
            MutableLiveData()
        }
    }

    private val playerStatusLiveData = Transformations.switchMap(db.isDatabaseCreated()) { created ->
        if(created) {
            Transformations.switchMap(playerIdLiveData) { playerId ->
                Transformations.switchMap(gameIdLiveData) { gameId ->
                    dao.playerByIds(gameId, playerId)
                }
            }
        } else {
            MutableLiveData()
        }
    }

    override suspend fun new(displayName: String, avatarPath: String?): Player {
        return withContext(Dispatchers.IO) {
            val ids = dao.insertAll(Player(displayName, avatarPath))
            return@withContext dao.playerByIdNow(ids.first()) ?: throw RuntimeException("Player cannot be null")
        }
    }

    override fun delete(player: Player) {
        launch(Dispatchers.IO) {
            dao.delete(player)
        }
    }

    override fun delete(id: Long) {
        launch(Dispatchers.IO) {
            dao.deleteById(id)
        }
    }

    override fun players(): LiveData<List<Player>> = players

    override suspend fun playersNow(): List<Player> {
        return withContext(Dispatchers.IO) {
            dao.listNow()
        }
    }

    override fun watchPlayer(playerId: Long) {
        playerIdLiveData.postValue(playerId)
    }

    override fun player(): LiveData<Player?> = playerLiveData

    override fun watchGame(gameId: Long) {
        gameIdLiveData.postValue(gameId)
    }

    override fun playerStatus(): LiveData<PlayerStatus?> = playerStatusLiveData
}