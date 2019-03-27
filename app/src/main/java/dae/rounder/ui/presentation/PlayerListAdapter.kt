package dae.rounder.ui.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dae.rounder.database.entity.Player
import dae.rounder.databinding.HolderPlayerBinding
import dae.rounder.viewmodels.PlayerListViewModel

class PlayerListAdapter(private val viewLifecycleOwner: LifecycleOwner, private val vm: PlayerListViewModel) :
    ListAdapter<Player, PlayerListHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListHolder {
        val binding = HolderPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return PlayerListHolder(vm, binding)
    }

    override fun onBindViewHolder(holder: PlayerListHolder, position: Int) {
        val player = getItem(position)

        if (player != null) {
            holder.bind(player)
        } else {
            holder.clear()
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Player> = object : DiffUtil.ItemCallback<Player>() {
            override fun areItemsTheSame(v1: Player, v2: Player): Boolean {
                return v1.id == v1.id
            }

            override fun areContentsTheSame(v1: Player, v2: Player): Boolean {
                return v1 == v2
            }
        }
    }
}