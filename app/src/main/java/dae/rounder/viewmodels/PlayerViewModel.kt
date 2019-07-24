package dae.rounder.viewmodels

import androidx.lifecycle.ViewModel
import dae.rounder.database.entity.Player
import dae.rounder.repositories.PlayerRepository

class PlayerViewModel(private val playerRepository: PlayerRepository): ViewModel() {

    fun player() = playerRepository.player()

    suspend fun new(displayName: String, avatarPath: String? = null): Player {
        return playerRepository.new(displayName, avatarPath)
    }

    fun delete(player: Player) {
        playerRepository.delete(player)
    }

    fun delete(playerId: Long) {
        playerRepository.delete(playerId)
    }

    fun watch(playerId: Long) {
        playerRepository.watchPlayer(playerId)
    }

}