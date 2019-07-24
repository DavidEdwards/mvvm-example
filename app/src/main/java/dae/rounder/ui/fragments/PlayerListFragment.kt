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
import dae.rounder.databinding.FragmentPlayerListBinding
import dae.rounder.events.OnTitleChangedEvent
import dae.rounder.ui.presentation.PlayerListAdapter
import dae.rounder.utils.LogUtils
import dae.rounder.viewmodels.PlayerListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext


class PlayerListFragment: Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: FragmentPlayerListBinding
    private val playerListViewModel by viewModel<PlayerListViewModel>()
    private lateinit var adapter: PlayerListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(OnTitleChangedEvent(getString(R.string.players)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        binding.vm = playerListViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        layoutManager = LinearLayoutManager(requireContext())

        playerListViewModel.players().observe(viewLifecycleOwner, Observer { list ->
            LogUtils.log("PLAYERS", "V::Players: $list")
            adapter.submitList(list)
        })

        playerListViewModel.uiNamePlayer().observe(viewLifecycleOwner, Observer { callback ->
            val viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_text, view as ViewGroup?, false)
            val input = viewInflated.findViewById(R.id.input) as EditText
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.choose_player_name)
                .setView(viewInflated)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    val text = input.text.toString()
                    callback.invoke(text)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create().show()
        })

        adapter = PlayerListAdapter(viewLifecycleOwner, playerListViewModel)
        binding.playerList.adapter = adapter
        binding.playerList.layoutManager = layoutManager
        binding.playerList.setHasFixedSize(true)

        binding.executePendingBindings()

        return binding.root
    }

}