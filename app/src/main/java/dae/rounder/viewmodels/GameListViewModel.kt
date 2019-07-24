package dae.rounder.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dae.rounder.database.entity.Game
import dae.rounder.events.OnGameClickedEvent
import dae.rounder.repositories.GameRepository
import dae.rounder.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.KoinComponent

class GameListViewModel(private val gameRepository: GameRepository): ViewModel(), KoinComponent {

    fun games() = gameRepository.games()

    private val uiNameGameLiveData = LiveEvent<((name: String) -> Unit)>()
    fun uiNameGame(): LiveEvent<((name: String) -> Unit)> = uiNameGameLiveData

    fun onAddGameClicked() {
        viewModelScope.launch {
            uiNameGameLiveData.postValue { name ->
                launch(Dispatchers.IO) {
                    new(name)
                }
            }
        }
    }

    fun onGameClicked(view: View, game: Game) {
        LogUtils.log("GAMES", "V::Game clicked: $game")
        EventBus.getDefault().post(OnGameClickedEvent(game))
    }

    suspend fun new(displayName: String): Game {
        return gameRepository.new(displayName)
    }

    fun delete(game: Game) {
        gameRepository.delete(game)
    }

    fun delete(gameId: Long) {
        gameRepository.delete(gameId)
    }

}