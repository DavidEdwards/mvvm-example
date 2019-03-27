package dae.rounder.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.PlayerStatus

@Dao
interface PlayerDao {

    @Query("SELECT count(id) FROM player")
    fun count(): Int

    @Query("SELECT count(id) FROM player")
    fun has(): Boolean

    @Query("SELECT * FROM player WHERE id = :id")
    fun playerByIdNow(id: Long): Player?

    @Query("SELECT * FROM player WHERE id = :id")
    fun playerById(id: Long): LiveData<Player?>

    @Query("SELECT player.id as player_id, player.displayName as player_displayName, player.avatar as player_avatar, player.defaultHealth as player_defaultHealth, status.playerId as status_playerId, status.id as status_id, status.gameId as status_gameId, status.counter as status_counter, status.health as status_health FROM game, player, status WHERE game.id = status.gameId AND player.id = status.playerId AND game.id = :gameId AND status.playerId = :playerId ORDER BY status.counter ASC, player.displayName ASC")
    fun playerByIds(gameId: Long, playerId: Long): LiveData<PlayerStatus>

    @Query("SELECT * FROM player ORDER BY displayName ASC")
    fun list(): LiveData<List<Player>>

    @Query("SELECT * FROM player ORDER BY displayName ASC")
    fun listNow(): List<Player>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg player: Player): Array<Long>

    @Query("DELETE FROM player WHERE id = :id")
    fun deleteById(id: Long)

    @Delete
    fun delete(player: Player)

    @Query("DELETE FROM player")
    fun deleteAll()
}