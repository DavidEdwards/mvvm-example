package dae.rounder.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Player(
    var displayName: String = "",
    var avatar: String? = null,
    var defaultHealth: Long = 100L
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
