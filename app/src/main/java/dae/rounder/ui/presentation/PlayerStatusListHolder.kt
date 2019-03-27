package dae.rounder.ui.presentation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dae.rounder.database.entity.PlayerStatus
import dae.rounder.databinding.HolderPlayerStatusBinding
import dae.rounder.viewmodels.GameViewModel
import org.koin.standalone.KoinComponent

class PlayerStatusListHolder(private val vm: GameViewModel, private val binding: HolderPlayerStatusBinding) :
    RecyclerView.ViewHolder(binding.root), KoinComponent {

    fun bind(ps: PlayerStatus) {
        binding.ps = ps
        binding.vm = vm
        binding.executePendingBindings()

        ps.player.avatar?.let { avatar ->
            Glide.with(binding.avatar)
                .load(avatar)
                .apply(RequestOptions().optionalCircleCrop())
                .into(binding.avatar)
        }
    }

    fun clear() {
    }
}