package dae.rounder.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dae.rounder.R
import dae.rounder.databinding.FragmentPlayerStatusBinding
import dae.rounder.events.OnTitleChangedEvent
import dae.rounder.utils.Constants
import dae.rounder.utils.LogUtils
import dae.rounder.viewmodels.PlayerStatusViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class PlayerStatusFragment: Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: FragmentPlayerStatusBinding
    private val playerStatusViewModel by viewModel<PlayerStatusViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayerStatusBinding.inflate(inflater, container, false)
        binding.vm = playerStatusViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        playerStatusViewModel.watch(arguments?.getLong(Constants.INTENT_KEY_GAME_ID) ?: 0L, arguments?.getLong(Constants.INTENT_KEY_PLAYER_ID) ?: 0L)

        playerStatusViewModel.player().observe(viewLifecycleOwner, Observer { ps ->
            EventBus.getDefault().post(OnTitleChangedEvent(getString(R.string.player_x, ps?.player?.displayName ?: "")))
            LogUtils.log("PLAYER", "V::Player: $ps")
        })

        binding.numberPicker.minValue = 1
        binding.numberPicker.maxValue = 100

        binding.executePendingBindings()

        return binding.root
    }

}