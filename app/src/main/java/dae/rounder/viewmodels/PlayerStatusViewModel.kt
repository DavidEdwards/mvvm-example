package dae.rounder.viewmodels

import android.widget.NumberPicker
import androidx.lifecycle.ViewModel
import dae.rounder.database.entity.Game
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.repositories.GameRepository
import dae.rounder.repositories.PlayerRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayerStatusViewModel(private val gameRepository: GameRepository, private val playerRepository: PlayerRepository): ViewModel() {

    private var amount = 1

    fun player() = playerRepository.playerStatus()
    fun game() = gameRepository.game()

    fun onSpendTurnClicked(game: Game, playerStatus: PlayerStatus) {
        GlobalScope.launch(Dispatchers.Main) {
            gameRepository.spendTurn(game.id, playerStatus.player.id, amount).join()
        }
    }

    fun onRefundTurnClicked(game: Game, playerStatus: PlayerStatus) {
        GlobalScope.launch(Dispatchers.Main) {
            gameRepository.refundTurn(game.id, playerStatus.player.id, amount).join()
        }
    }

    fun onDamageClicked(game: Game, playerStatus: PlayerStatus) {
        GlobalScope.launch(Dispatchers.Main) {
            gameRepository.damagePlayer(game.id, playerStatus.player.id, amount).join()
        }
    }

    fun onHealClicked(game: Game, playerStatus: PlayerStatus) {
        GlobalScope.launch(Dispatchers.Main) {
            gameRepository.healPlayer(game.id, playerStatus.player.id, amount).join()
        }
    }

    fun onNumberPickerChanged(view: NumberPicker, old: Int, new: Int) {
        amount = new
    }

    suspend fun new(displayName: String, avatarPath: String? = null): Deferred<Player> {
        return playerRepository.new(displayName, avatarPath)
    }

    suspend fun delete(player: Player) {
        playerRepository.delete(player)
    }

    suspend fun delete(playerId: Long) {
        playerRepository.delete(playerId)
    }

    fun watch(gameId: Long, playerId: Long) {
        playerRepository.watchGame(gameId)
        playerRepository.watchPlayer(playerId)
        gameRepository.watchGame(gameId)
    }

}