package dae.rounder.viewmodels

import androidx.lifecycle.ViewModel
import dae.rounder.database.entity.Player
import dae.rounder.repositories.PlayerRepository
import kotlinx.coroutines.Deferred

class MainViewModel(private val playerRepository: PlayerRepository): ViewModel() {

    fun players() = playerRepository.players()

    fun playersNow(): Deferred<List<Player>> {
        return playerRepository.playersNow()
    }

}