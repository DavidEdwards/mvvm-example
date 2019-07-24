package dae.rounder.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import dae.rounder.databinding.FragmentGlossaryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class GlossaryFragment: Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: FragmentGlossaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGlossaryBinding.inflate(inflater, container, false)

        binding.webviewGlossary.webViewClient = WebViewClient()
        binding.webviewGlossary.loadUrl("https://en.wikipedia.org/wiki/Glossary")

        binding.executePendingBindings()

        return binding.root
    }

}