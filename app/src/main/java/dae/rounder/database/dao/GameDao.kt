package dae.rounder.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dae.rounder.database.entity.Game
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.database.entity.Status

@Dao
interface GameDao {

    @Query("SELECT count(id) FROM game")
    fun count(): Int

    @Query("SELECT count(id) FROM game")
    fun has(): Boolean

    @Query("SELECT * FROM game WHERE id = :id")
    fun gameByIdNow(id: Long): Game?

    @Query("SELECT * FROM game WHERE id = :id")
    fun gameById(id: Long): LiveData<Game?>

    @Query("SELECT * FROM game ORDER BY createdAt DESC")
    fun games(): LiveData<List<Game>>

    @Query("SELECT player.id as player_id, player.displayName as player_displayName, player.avatar as player_avatar, player.defaultHealth as player_defaultHealth, status.playerId as status_playerId, status.id as status_id, status.gameId as status_gameId, status.counter as status_counter, status.health as status_health FROM game, player, status WHERE game.id = status.gameId AND player.id = status.playerId AND game.id = :id ORDER BY status.counter ASC, player.displayName ASC")
    fun playersInGame(id: Long): LiveData<List<PlayerStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg game: Game): Array<Long>

    @Query("DELETE FROM game WHERE id = :id")
    fun deleteById(id: Long)

    @Delete
    fun delete(game: Game)

    @Query("DELETE FROM game")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateStatus(status: Status)

    @Query("DELETE FROM status WHERE gameId = :gameId AND playerId = :playerId")
    fun removePlayerFromGame(gameId: Long, playerId: Long)

    @Query("SELECT count(playerId) FROM status WHERE gameId = :gameId AND playerId = :playerId")
    fun playerInGame(gameId: Long, playerId: Long): Boolean

    @Query("SELECT player.id as player_id, player.displayName as player_displayName, player.avatar as player_avatar, player.defaultHealth as player_defaultHealth, status.playerId as status_playerId, status.id as status_id, status.gameId as status_gameId, status.counter as status_counter, status.health as status_health FROM game, player, status WHERE game.id = status.gameId AND player.id = status.playerId AND game.id = :gameId AND playerId = :playerId")
    fun getPlayerInGame(gameId: Long, playerId: Long): LiveData<PlayerStatus?>

    @Query("SELECT player.id as player_id, player.displayName as player_displayName, player.avatar as player_avatar, player.defaultHealth as player_defaultHealth, status.playerId as status_playerId, status.id as status_id, status.gameId as status_gameId, status.counter as status_counter, status.health as status_health FROM game, player, status WHERE game.id = status.gameId AND player.id = status.playerId AND game.id = :gameId AND playerId = :playerId")
    fun getPlayerInGameNow(gameId: Long, playerId: Long): PlayerStatus?

    @Query("UPDATE status SET counter = counter - :turns WHERE gameId = :gameId AND health > 0")
    fun advanceGameBy(gameId: Long, turns: Long)
}