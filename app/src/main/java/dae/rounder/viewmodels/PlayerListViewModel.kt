package dae.rounder.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import dae.rounder.database.entity.Player
import dae.rounder.events.OnPlayerClicked
import dae.rounder.repositories.PlayerRepository
import dae.rounder.utils.LogUtils
import dae.rounder.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.*

class PlayerListViewModel(private val playerRepository: PlayerRepository): ViewModel() {

    fun players() = playerRepository.players()

    private val uiNamePlayerLiveData = LiveEvent<((name: String) -> Unit)>()
    fun uiNamePlayer(): LiveEvent<((name: String) -> Unit)> = uiNamePlayerLiveData

    fun onAddPlayerClicked() {
        viewModelScope.launch {
            uiNamePlayerLiveData.postValue { name ->
                launch(Dispatchers.IO) {
                    val hash = UUID.randomUUID().toString().md5()
                    val gravatarUrl = "https://www.gravatar.com/avatar/$hash?f=y&d=robohash"
                    new(name, gravatarUrl)
                }
            }
        }
    }

    fun onPlayerClicked(view: View, player: Player) {
        LogUtils.log("PLAYERS", "V::Player clicked: $player")
        EventBus.getDefault().post(OnPlayerClicked(player))
    }

    fun onPlayerDeleteClicked(view: View, player: Player) {
        LogUtils.log("PLAYERS", "V::Player clicked: $player")
        viewModelScope.launch(Dispatchers.IO) {
            delete(player)
        }
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

}