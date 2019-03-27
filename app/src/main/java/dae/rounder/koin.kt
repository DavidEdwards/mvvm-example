package dae.rounder

import dae.rounder.database.AppDatabase
import dae.rounder.database.AppDatabaseImpl
import dae.rounder.repositories.GameRepository
import dae.rounder.repositories.GameRepositoryImpl
import dae.rounder.repositories.PlayerRepository
import dae.rounder.repositories.PlayerRepositoryImpl
import dae.rounder.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    single<AppDatabase> {
        AppDatabaseImpl(androidContext())
    }

    single<GameRepository> {
        GameRepositoryImpl()
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    viewModel {
        GameListViewModel(get())
    }

    viewModel {
        GameViewModel(get(), get())
    }

    viewModel {
        PlayerListViewModel(get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        PlayerStatusViewModel(get(), get())
    }

    viewModel {
        MainViewModel(get())
    }
}

//val appTestModule = module {
//    single<AppDatabase>(override = true) {
//        AppDatabaseMockImpl(androidContext())
//    }
//}