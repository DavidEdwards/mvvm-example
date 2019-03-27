package dae.rounder.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dae.rounder.R
import dae.rounder.databinding.FragmentGameBinding
import dae.rounder.events.OnTitleChangedEvent
import dae.rounder.ui.presentation.PlayerStatusListAdapter
import dae.rounder.utils.Constants
import dae.rounder.utils.LogUtils
import dae.rounder.viewmodels.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel

class GameFragment: Fragment() {

    private lateinit var binding: FragmentGameBinding
    private val gameViewModel by viewModel<GameViewModel>()
    private lateinit var adapter: PlayerStatusListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.vm = gameViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        layoutManager = LinearLayoutManager(requireContext())

        gameViewModel.watch(arguments?.getLong(Constants.INTENT_KEY_GAME_ID) ?: 0L)

        gameViewModel.game().observe(viewLifecycleOwner, Observer { game ->
            EventBus.getDefault().post(OnTitleChangedEvent(getString(R.string.game_x, game?.displayName ?: "")))
            LogUtils.log("GAME", "V::Game: $game")
        })

        gameViewModel.players().observe(viewLifecycleOwner, Observer { list ->
            LogUtils.log("GAME", "V::Players: $list")
            adapter.submitList(list)

            if(list.isNotEmpty() && list.first { it.status.health > 0 }.status.counter != 0L) {
                GlobalScope.launch(Dispatchers.Main) {
                    delay(100)
                    gameViewModel.advanceGameState(gameViewModel.game().value?.id ?: -1)
                }
            }
        })

        gameViewModel.uiPickPlayer().observe(viewLifecycleOwner, Observer { data ->
            val names = data.list.map { it.displayName }
            LogUtils.log("GAME", "V::Picking player from: $names")

            AlertDialog.Builder(context)
                .setTitle(R.string.players)
                .setMultiChoiceItems(names.toTypedArray(), data.inGame.toBooleanArray()) { _, which, isChecked ->
                    data.callback.invoke(data.list[which], isChecked)
                }
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        })

        adapter = PlayerStatusListAdapter(viewLifecycleOwner, gameViewModel)
        binding.gameList.adapter = adapter
        binding.gameList.layoutManager = layoutManager
        binding.gameList.setHasFixedSize(true)

        adapter.submitList(listOf())

        binding.executePendingBindings()

        return binding.root
    }

}