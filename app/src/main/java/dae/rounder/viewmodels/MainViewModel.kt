package dae.rounder.viewmodels

import androidx.lifecycle.ViewModel
import dae.rounder.database.entity.Player
import dae.rounder.repositories.PlayerRepository

class MainViewModel(private val playerRepository: PlayerRepository): ViewModel() {

    fun players() = playerRepository.players()

    suspend fun playersNow(): List<Player> {
        return playerRepository.playersNow()
    }

}