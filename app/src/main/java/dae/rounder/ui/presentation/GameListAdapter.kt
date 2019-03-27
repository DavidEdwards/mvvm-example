package dae.rounder.ui.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dae.rounder.database.entity.Game
import dae.rounder.databinding.HolderGameBinding
import dae.rounder.viewmodels.GameListViewModel

class GameListAdapter(private val viewLifecycleOwner: LifecycleOwner, private val vm: GameListViewModel) :
    ListAdapter<Game, GameListHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListHolder {
        val binding = HolderGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return GameListHolder(vm, binding)
    }

    override fun onBindViewHolder(holder: GameListHolder, position: Int) {
        val game = getItem(position)

        if (game != null) {
            holder.bind(game)
        } else {
            holder.clear()
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Game> = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(v1: Game, v2: Game): Boolean {
                return v1.id == v1.id
            }

            override fun areContentsTheSame(v1: Game, v2: Game): Boolean {
                return v1 == v2
            }
        }
    }
}