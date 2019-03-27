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

interface GameRepository {
    suspend fun new(displayName: String = "Untitled"): Deferred<Game>
    suspend fun delete(game: Game)
    suspend fun delete(id: Long)
    fun game(): LiveData<Game?>
    fun games(): LiveData<List<Game>>
    fun players(): LiveData<List<PlayerStatus>>
    fun watchGame(gameId: Long)
    suspend fun addPlayer(game: Game, vararg players: Player): Deferred<List<PlayerStatus>>
    fun removePlayer(game: Game, vararg players: Player)
    suspend fun playerInGame(game: Game, player: Player): Deferred<Boolean>
    fun spendTurn(gameId: Long, playerId: Long, amount: Int = 1): Deferred<PlayerStatus>
    fun refundTurn(gameId: Long, playerId: Long, amount: Int = 1): Deferred<PlayerStatus>
    fun damagePlayer(gameId: Long, playerId: Long, amount: Int = 1): Deferred<PlayerStatus>
    fun healPlayer(gameId: Long, playerId: Long, amount: Int = 1): Deferred<PlayerStatus>
    fun getPlayerFromGame(gameId: Long, playerId: Long): LiveData<PlayerStatus?>
    fun advanceGameBy(gameId: Long, turns: Long)
}

class GameRepositoryImpl: GameRepository, KoinComponent {

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

    override suspend fun new(displayName: String): Deferred<Game> {
        return GlobalScope.async(Dispatchers.IO) {
//            val dao = db.getDatabase().gameDao()
            val ids = dao.insertAll(Game(displayName))
            return@async dao.gameByIdNow(ids.first()) ?: throw RuntimeException("Game cannot be null")
        }
    }

    override suspend fun delete(game: Game) {
        GlobalScope.launch(Dispatchers.IO) {
//            val dao = db.getDatabase().gameDao()
            dao.delete(game)
        }
    }

    override suspend fun delete(id: Long) {
        GlobalScope.launch(Dispatchers.IO) {
//            val dao = db.getDatabase().gameDao()
            dao.deleteById(id)
        }
    }

    override fun game(): LiveData<Game?> = gameLiveData
    override fun games(): LiveData<List<Game>> = gamesLiveData
    override fun players(): LiveData<List<PlayerStatus>> = playersInGameLiveData

    override fun watchGame(gameId: Long) {
        gameIdLiveData.postValue(gameId)
    }

    override suspend fun addPlayer(game: Game, vararg players: Player): Deferred<List<PlayerStatus>> {
        return GlobalScope.async(Dispatchers.IO) {
            val list = ArrayList<PlayerStatus>()

            players.forEach { player ->
                val status = Status(player.id, game.id, 0L, player.defaultHealth)
                dao.updateStatus(status)

                val playerStatus = PlayerStatus(player, status)
                list.add(playerStatus)
            }


            return@async list
        }
    }

    override fun removePlayer(game: Game, vararg players: Player) {
        GlobalScope.launch(Dispatchers.IO) {
            players.forEach { player ->
                dao.removePlayerFromGame(game.id, player.id)
            }
        }
    }

    override suspend fun playerInGame(game: Game, player: Player): Deferred<Boolean> {
        return GlobalScope.async(Dispatchers.IO) {
            dao.playerInGame(game.id, player.id)
        }
    }

    override fun spendTurn(gameId: Long, playerId: Long, amount: Int): Deferred<PlayerStatus> {
        return GlobalScope.async(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            if(playerStatus.status.health > 0) {
                playerStatus.status.counter = Math.max(0, playerStatus.status.counter + amount)

                dao.updateStatus(playerStatus.status)
            }

            return@async playerStatus
        }
    }

    override fun refundTurn(gameId: Long, playerId: Long, amount: Int): Deferred<PlayerStatus> {
        return GlobalScope.async(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            if(playerStatus.status.health > 0) {
                playerStatus.status.counter = Math.min(100, playerStatus.status.counter - amount)

                dao.updateStatus(playerStatus.status)
            }

            return@async playerStatus
        }
    }

    override fun damagePlayer(gameId: Long, playerId: Long, amount: Int): Deferred<PlayerStatus> {
        return GlobalScope.async(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            playerStatus.status.health = Math.max(0, playerStatus.status.health - amount)

            dao.updateStatus(playerStatus.status)

            return@async playerStatus
        }
    }

    override fun healPlayer(gameId: Long, playerId: Long, amount: Int): Deferred<PlayerStatus> {
        return GlobalScope.async(Dispatchers.IO) {
            val playerStatus = dao.getPlayerInGameNow(gameId, playerId)!!

            playerStatus.status.health = Math.min(100, playerStatus.status.health + amount)

            dao.updateStatus(playerStatus.status)

            return@async playerStatus
        }
    }

    override fun getPlayerFromGame(gameId: Long, playerId: Long): LiveData<PlayerStatus?> {
        return dao.getPlayerInGame(gameId, playerId)
    }

    override fun advanceGameBy(gameId: Long, turns: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.advanceGameBy(gameId, Math.max(0, turns))
        }
    }
}