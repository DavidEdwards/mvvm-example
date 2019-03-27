package dae.rounder.ui.presentation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dae.rounder.database.entity.Player
import dae.rounder.databinding.HolderPlayerBinding
import dae.rounder.viewmodels.PlayerListViewModel
import org.koin.standalone.KoinComponent

class PlayerListHolder(private val vm: PlayerListViewModel, private val binding: HolderPlayerBinding) :
    RecyclerView.ViewHolder(binding.root), KoinComponent {

    fun bind(player: Player) {
        binding.player = player
        binding.vm = vm
        binding.executePendingBindings()

        player.avatar?.let { avatar ->
            Glide.with(binding.avatar)
                .load(avatar)
                .apply(RequestOptions().optionalCircleCrop())
                .into(binding.avatar)
        }
    }

    fun clear() {
    }
}