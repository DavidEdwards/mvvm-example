package dae.rounder.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import dae.rounder.database.entity.Game
import dae.rounder.database.entity.Player
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.events.OnPlayerStatusClicked
import dae.rounder.repositories.GameRepository
import dae.rounder.repositories.PlayerRepository
import dae.rounder.utils.LogUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class GameViewModel(
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
): ViewModel() {

    fun game() = gameRepository.game()
    fun players() = gameRepository.players()

    private val uiPickPlayerLiveData = LiveEvent<PlayerPickPair>()
    fun uiPickPlayer(): LiveEvent<PlayerPickPair> = uiPickPlayerLiveData

    fun onAddPlayerClicked() {
        GlobalScope.launch(Dispatchers.Main) {
            val game = game().value!!
            val list = playerRepository.playersNow().await().toMutableList()
            val inGame = arrayListOf<Boolean>()

            val it = list.iterator()
            while(it.hasNext()) {
                val player = it.next()
                if(gameRepository.playerInGame(game, player).await()) {
                    inGame.add(true)
                } else {
                    inGame.add(false)
                }
            }

            uiPickPlayerLiveData.postValue(PlayerPickPair(list, inGame) { player, enabled ->
                GlobalScope.launch(Dispatchers.IO) {
                    if(enabled) {
                        gameRepository.addPlayer(game, player).join()
                    } else {
                        gameRepository.removePlayer(game, player)
                    }
                }
            })
        }
    }

    fun onPlayerClicked(view: View, playerStatus: PlayerStatus) {
        LogUtils.log("GAME", "V::Player clicked: $playerStatus")
        EventBus.getDefault().post(OnPlayerStatusClicked(playerStatus))
    }

    suspend fun new(displayName: String): Deferred<Game> {
        return gameRepository.new(displayName)
    }

    suspend fun delete(game: Game) {
        gameRepository.delete(game)
    }

    suspend fun delete(gameId: Long) {
        gameRepository.delete(gameId)
    }

    fun watch(gameId: Long) {
        gameRepository.watchGame(gameId)
    }

    fun advanceGameState(gameId: Long) {
        // Use lowest if you want to go instantly to the next player.
        //val lowest = players().value?.firstOrNull()?.status?.counter ?: 0
        gameRepository.advanceGameBy(gameId, 1)
    }

}

data class PlayerPickPair(val list: List<Player>, val inGame: List<Boolean>, val callback: ((player: Player, enabled: Boolean) -> Unit))