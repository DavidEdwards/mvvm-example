package dae.rounder.database.entity

import androidx.room.*

@Entity
data class PlayerStatus(
    @Embedded(prefix = "player_")
    var player: Player,
    @Embedded(prefix = "status_")
    var status: Status
)
