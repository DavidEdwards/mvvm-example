package dae.rounder.ui.presentation

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dae.rounder.database.entity.Game
import dae.rounder.databinding.HolderGameBinding
import dae.rounder.utils.md5
import dae.rounder.viewmodels.GameListViewModel
import org.koin.standalone.KoinComponent
import java.util.*

class GameListHolder(private val vm: GameListViewModel, private val binding: HolderGameBinding) :
    RecyclerView.ViewHolder(binding.root), KoinComponent {

    fun bind(game: Game) {
        binding.game = game
        binding.vm = vm
        binding.executePendingBindings()

        val hash = UUID.randomUUID().toString().md5()
        val gravatarUrl = "https://www.gravatar.com/avatar/$hash?f=y&d=retro"
        Glide.with(binding.avatar)
            .load(gravatarUrl)
            .apply(RequestOptions().optionalCircleCrop())
            .into(binding.avatar)
    }

    fun clear() {
    }
}