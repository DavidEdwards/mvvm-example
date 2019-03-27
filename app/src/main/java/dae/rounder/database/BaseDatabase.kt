package dae.rounder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dae.rounder.database.dao.GameDao
import dae.rounder.database.dao.PlayerDao
import dae.rounder.database.entity.Game
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.RoundLog
import dae.rounder.database.entity.Status

@Database(entities = [(Game::class), (Player::class), (Status::class), (RoundLog::class)], version = 3)
@TypeConverters(Converters::class)
abstract class BaseDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
}