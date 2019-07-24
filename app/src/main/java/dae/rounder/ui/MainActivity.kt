package dae.rounder.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import dae.rounder.R
import dae.rounder.databinding.ActivityMainBinding
import dae.rounder.events.*
import dae.rounder.ui.fragments.*
import dae.rounder.utils.Constants
import dae.rounder.viewmodels.MainViewModel
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by inject<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navView.foregroundTintList = ColorStateList.valueOf(Color.BLACK)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        launch {
            val players = mainViewModel.playersNow()
            if(players.isNotEmpty()) {
                loadGameListFragment()
            } else {
                loadPlayerListFragment()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTitleChanged(event: OnTitleChangedEvent) {
        binding.toolbar.title = event.title
    }

    @Subscribe
    fun loadRulesFragment(event: OnRulesClicked? = null) {
        binding.navView.setCheckedItem(R.id.nav_rules)

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, RulesFragment(), "RulesFragment")
        ft.commit()
    }

    @Subscribe
    fun loadGlossaryFragment(event: OnGlossaryClicked? = null) {
        binding.navView.setCheckedItem(R.id.nav_glossary)

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, GlossaryFragment(), "GlossaryFragment")
        ft.commit()
    }

    @Subscribe
    fun loadGameListFragment(event: OnGameListClicked? = null) {
        binding.navView.setCheckedItem(R.id.nav_games)

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, GameListFragment(), "GameListFragment")
        ft.commit()
    }

    @Subscribe
    fun loadPlayerListFragment(event: OnPlayerListClicked? = null) {
        binding.navView.setCheckedItem(R.id.nav_players)

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, PlayerListFragment(), "PlayerListFragment")
        ft.commit()
    }

    @Subscribe
    fun loadGameFragment(event: OnGameClickedEvent) {
        binding.navView.setCheckedItem(0)

        val fragment = GameFragment()
        fragment.arguments = bundleOf(
            Constants.INTENT_KEY_GAME_ID to event.game.id
        )

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, fragment, "Game${event.game.id}Fragment")
        ft.commit()
    }

    @Subscribe
    fun loadPlayerFragment(event: OnPlayerClicked) {
        binding.navView.setCheckedItem(0)

        val fragment = PlayerStatusFragment()
        fragment.arguments = bundleOf(
            Constants.INTENT_KEY_PLAYER_ID to event.player.id
        )

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, fragment, "Player${event.player.id}Fragment")
        ft.commit()
    }

    @Subscribe
    fun loadPlayerStatusFragment(event: OnPlayerStatusClicked) {
        binding.navView.setCheckedItem(0)

        val fragment = PlayerStatusFragment()
        fragment.arguments = bundleOf(
            Constants.INTENT_KEY_PLAYER_ID to event.playerStatus.player.id,
            Constants.INTENT_KEY_GAME_ID to event.playerStatus.status.gameId
        )

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.fragment_container, fragment, "Player${event.playerStatus.status.gameId}#${event.playerStatus.player.id}Fragment")
        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onBackPressed() {
        val drawerLayout = binding.drawerLayout
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_rules -> {
                loadRulesFragment()
            }
            R.id.nav_glossary -> {
                loadGlossaryFragment()
            }
            R.id.nav_games -> {
                loadGameListFragment()
            }
            R.id.nav_players -> {
                loadPlayerListFragment()
            }
        }
        val drawerLayout = binding.drawerLayout
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
