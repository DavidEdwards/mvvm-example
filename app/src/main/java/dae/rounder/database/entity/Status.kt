package dae.rounder.database.entity

import androidx.room.*

@Entity(
    indices = [Index(value = ["playerId"]), Index(value = ["gameId"])],
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Status(
    var playerId: Long = 0L,
    var gameId: Long = 0L,
    var counter: Long = 0L,
    var health: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
