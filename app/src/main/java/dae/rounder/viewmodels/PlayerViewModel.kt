package dae.rounder.viewmodels

import androidx.lifecycle.ViewModel
import dae.rounder.database.entity.Player
import dae.rounder.repositories.PlayerRepository
import kotlinx.coroutines.Deferred

class PlayerViewModel(private val playerRepository: PlayerRepository): ViewModel() {

    fun player() = playerRepository.player()

    suspend fun new(displayName: String, avatarPath: String? = null): Deferred<Player> {
        return playerRepository.new(displayName, avatarPath)
    }

    suspend fun delete(player: Player) {
        playerRepository.delete(player)
    }

    suspend fun delete(playerId: Long) {
        playerRepository.delete(playerId)
    }

    fun watch(playerId: Long) {
        playerRepository.watchPlayer(playerId)
    }

}