package dae.rounder.viewmodels

import android.widget.NumberPicker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dae.rounder.database.entity.Game
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.repositories.GameRepository
import dae.rounder.repositories.PlayerRepository
import kotlinx.coroutines.launch

class PlayerStatusViewModel(private val gameRepository: GameRepository, private val playerRepository: PlayerRepository): ViewModel() {

    private var amount = 1

    fun player() = playerRepository.playerStatus()
    fun game() = gameRepository.game()

    fun onSpendTurnClicked(game: Game, playerStatus: PlayerStatus) {
        viewModelScope.launch {
            gameRepository.spendTurn(game.id, playerStatus.player.id, amount)
        }
    }

    fun onRefundTurnClicked(game: Game, playerStatus: PlayerStatus) {
        viewModelScope.launch {
            gameRepository.refundTurn(game.id, playerStatus.player.id, amount)
        }
    }

    fun onDamageClicked(game: Game, playerStatus: PlayerStatus) {
        viewModelScope.launch {
            gameRepository.damagePlayer(game.id, playerStatus.player.id, amount)
        }
    }

    fun onHealClicked(game: Game, playerStatus: PlayerStatus) {
        viewModelScope.launch {
            gameRepository.healPlayer(game.id, playerStatus.player.id, amount)
        }
    }

    fun onNumberPickerChanged(view: NumberPicker, old: Int, new: Int) {
        amount = new
    }

    suspend fun new(displayName: String, avatarPath: String? = null): Player {
        return playerRepository.new(displayName, avatarPath)
    }

    fun delete(player: Player) {
        playerRepository.delete(player)
    }

    fun delete(playerId: Long) {
        playerRepository.delete(playerId)
    }

    fun watch(gameId: Long, playerId: Long) {
        playerRepository.watchGame(gameId)
        playerRepository.watchPlayer(playerId)
        gameRepository.watchGame(gameId)
    }

}