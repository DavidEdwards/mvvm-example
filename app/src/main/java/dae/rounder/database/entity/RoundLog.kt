package dae.rounder.database.entity

import androidx.room.*
import org.threeten.bp.Instant

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
data class RoundLog(
    var playerId: Long = 0L,
    var gameId: Long = 0L,
    var attribute: Attribute = Attribute.ROUND,
    var modifier: Long = 0L,
    var createdAt: Instant = Instant.now()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    enum class Attribute {
        ROUND, HEALTH
    }
}
