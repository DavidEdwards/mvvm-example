package dae.rounder.ui.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.databinding.HolderPlayerStatusBinding
import dae.rounder.viewmodels.GameViewModel

class PlayerStatusListAdapter(private val viewLifecycleOwner: LifecycleOwner, private val vm: GameViewModel) :
    ListAdapter<PlayerStatus, PlayerStatusListHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerStatusListHolder {
        val binding = HolderPlayerStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return PlayerStatusListHolder(vm, binding)
    }

    override fun onBindViewHolder(holder: PlayerStatusListHolder, position: Int) {
        val playerStatus = getItem(position)

        if (playerStatus != null) {
            holder.bind(playerStatus)
        } else {
            holder.clear()
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<PlayerStatus> = object : DiffUtil.ItemCallback<PlayerStatus>() {
            override fun areItemsTheSame(v1: PlayerStatus, v2: PlayerStatus): Boolean {
                return v1.player.id == v1.player.id
            }

            override fun areContentsTheSame(v1: PlayerStatus, v2: PlayerStatus): Boolean {
                return v1.player == v2.player && v1.status == v2.status
            }
        }
    }
}