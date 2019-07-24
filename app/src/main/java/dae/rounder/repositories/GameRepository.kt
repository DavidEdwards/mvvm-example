package dae.rounder.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import dae.rounder.database.AppDatabase
import dae.rounder.database.entity.Game
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.database.entity.Status
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.coroutines.CoroutineContext

interface GameRepository {
    suspend fun new(displayName: String = "Untitled"): Game
    fun delete(game: Game)
    fun delete(id: Long)
    fun game(): LiveData<Game?>
    fun games(): LiveData<List<Game>>
    fun players(): LiveData<List<PlayerStatus>>
    fun watchGame(gameId: Long)
    suspend fun addPlayer(game: Game, vararg players: Player): List<PlayerStatus>
    fun removePlayer(game: Game, vararg players: Player)
    suspend fun playerInGame(game: Game, player: Player): Boolean
    suspend fun spendTurn(gameId: Long, playerId: Long, amount: Int = 1): PlayerStatus
    suspend fun refundTurn(gameId: Long, playerId: Long, amount: Int = 1): PlayerStatus
    suspend fun damagePlayer(gameId: Long, playerId: Long, amount: Int = 1): PlayerStatus
    suspend fun healPlayer(gameId: Long, playerId: Long, amount: Int = 1): PlayerStatus
    fun getPlayerFromGame(gameId: Long, playerId: Long): LiveData<PlayerStatus?>
    fun advanceGameBy(gameId: Long, turns: Long)
}

class GameRepositoryImpl: GameRepository, KoinComponent, CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val db by inject<AppDatabase>()
    private val dao = db.getDatabase().gameDao()

    private val gamesLiveData = Transformations.switchMap(db.isDatabaseCreated()) { created ->
        if(created) {
            dao.games()
        } else {
            MutableLiveData()
        }
    }

    private val gameIdLiveData = MutableLiveData<Long>()
    private val playersInGameLiveData = Transformations.switchMap(db.isDatabaseCreated()) { created ->
        if(created) {
            Transformations.switchMap(gameIdLiveData) { id ->
                dao.playersInGame(id)
            }
        } else {
            MutableLiveData()
        }
    }

    private val gameLiveData = Transformations.switchMap(db.isDatabaseCreated()) { created ->
        if(created) {
            Transformations.switchMap(gameIdLiveData) { id ->
                dao.gameById(id)
            }
        } else {
            MutableLiveData()
        }
    }

    override suspend fun new(displayName: String): Game {
        return withContext(Dispatchers.IO) {
            val ids = dao.insertAll(Game(displayName))
            return@withContext dao.gameByIdNow(ids.first()) ?: throw RuntimeException("Game cannot be null")
        }
    }

    override fun delete(game: Game) {
        launch(Dispatchers.IO) {
            dao.delete(game)
        }
    }

    override fun delete(id: Long) {
        launch(Dispatchers.IO) {
            dao.deleteById(id)
        }
    }

    override fun game(): LiveData<Game?> = gameLiveData
    override fun games(): LiveData<List<Game>> = gamesLiveData
    override fun players(): LiveData<List<PlayerStatus>> = playersInGameLiveData

    override fun watchGame(gameId: Long) {
        gameIdLiveData.postValue(gameId)
    }

    override suspend fun addPlayer(game: Game, vararg players: Player): List<PlayerStatus> {
        return withContext(Dispatchers.IO) {
            val list = ArrayList<PlayerStatus>()

            players.forEach { player ->
                val status = Status(player.id, game.id, 0L, player.defaultHealth)
                dao.updateStatus(status)

                val playerStatus = PlayerStatus(player, status)
                list.add(playerStatus)
            }


            return@withContext list
        }
    }

    override fun removePlayer(game: Game, vararg players: Player) {
        launch(Dispatchers.IO) {
            players.forEach { player ->
                dao.removePlayerFromGame(game.id, player.id)
            }
        }
    }

    override suspend fun playerInGame(game: Game, player: Player): Boolean {
        return withContext(Dispatchers.IO) {
            dao.playerInGame(game.id, player.id)
        }
    }

    override suspend fun spendTurn(gameId: Long, playerId: Long, amount: Int): PlayerStatus {
        return withContext(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            if(playerStatus.status.health > 0) {
                playerStatus.status.counter = Math.max(0, playerStatus.status.counter + amount)

                dao.updateStatus(playerStatus.status)
            }

            return@withContext playerStatus
        }
    }

    override suspend fun refundTurn(gameId: Long, playerId: Long, amount: Int): PlayerStatus {
        return withContext(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            if(playerStatus.status.health > 0) {
                playerStatus.status.counter = Math.min(100, playerStatus.status.counter - amount)

                dao.updateStatus(playerStatus.status)
            }

            return@withContext playerStatus
        }
    }

    override suspend fun damagePlayer(gameId: Long, playerId: Long, amount: Int): PlayerStatus {
        return withContext(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            playerStatus.status.health = Math.max(0, playerStatus.status.health - amount)

            dao.updateStatus(playerStatus.status)

            return@withContext playerStatus
        }
    }

    override suspend fun healPlayer(gameId: Long, playerId: Long, amount: Int): PlayerStatus {
        return withContext(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            playerStatus.status.health = Math.min(100, playerStatus.status.health + amount)

            dao.updateStatus(playerStatus.status)

            return@withContext playerStatus
        }
    }

    override fun getPlayerFromGame(gameId: Long, playerId: Long): LiveData<PlayerStatus?> {
        return dao.getPlayerInGame(gameId, playerId)
    }

    override fun advanceGameBy(gameId: Long, turns: Long) {
        launch(Dispatchers.IO) {
            dao.advanceGameBy(gameId, Math.max(0, turns))
        }
    }
}