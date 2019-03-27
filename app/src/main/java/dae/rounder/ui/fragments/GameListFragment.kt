package dae.rounder.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dae.rounder.R
import dae.rounder.databinding.FragmentGameListBinding
import dae.rounder.events.OnTitleChangedEvent
import dae.rounder.ui.presentation.GameListAdapter
import dae.rounder.utils.LogUtils
import dae.rounder.viewmodels.GameListViewModel
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel

class GameListFragment: Fragment() {

    private lateinit var binding: FragmentGameListBinding
    private val gameListViewModel by viewModel<GameListViewModel>()
    private lateinit var adapter: GameListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(OnTitleChangedEvent(getString(R.string.games)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameListBinding.inflate(inflater, container, false)
        binding.vm = gameListViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        layoutManager = LinearLayoutManager(requireContext())

        gameListViewModel.games().observe(viewLifecycleOwner, Observer { list ->
            LogUtils.log("GAMES", "V::Games: $list")
            adapter.submitList(list)
        })

        gameListViewModel.uiNameGame().observe(viewLifecycleOwner, Observer { callback ->
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_text, view as ViewGroup?, false)
            val input = viewInflated.findViewById(R.id.input) as EditText
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.choose_game_name)
                .setView(viewInflated)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    val text = input.text.toString()
                    callback.invoke(text)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create().show()
        })

        adapter = GameListAdapter(viewLifecycleOwner, gameListViewModel)
        binding.gameList.adapter = adapter
        binding.gameList.layoutManager = layoutManager
        binding.gameList.setHasFixedSize(true)

        binding.executePendingBindings()

        return binding.root
    }

}