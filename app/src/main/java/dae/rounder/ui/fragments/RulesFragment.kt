package dae.rounder.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import dae.rounder.databinding.FragmentRulesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class RulesFragment: Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: FragmentRulesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRulesBinding.inflate(inflater, container, false)

        binding.webviewRules.webViewClient = WebViewClient()
        binding.webviewRules.loadUrl("https://en.wikipedia.org/wiki/Rule")

        binding.executePendingBindings()

        return binding.root
    }

}