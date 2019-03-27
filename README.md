[![CircleCI](https://circleci.com/gh/DavidEdwards/mvvm-example.svg?style=svg&circle-token=2a38dc620a9a31666fadb66ee2cf9f38274962c8)](https://circleci.com/gh/DavidEdwards/mvvm-example)

# Android App architected with MVVM

This project was created as an example of how to use MVVM in Android.

## Important frameworks

* Dependency injection using [Koin](https://github.com/InsertKoinIO/koin).
* ViewModel provided through [Android Architecture Component libraries](https://developer.android.com/topic/libraries/architecture/viewmodel).
* Database provided by [Room](https://developer.android.com/topic/libraries/architecture/room).
* Databindings provided by [Android Databinding Library](https://developer.android.com/topic/libraries/data-binding).

## Future

* The new [Navigation component](https://developer.android.com/guide/navigation) should replace the current usage of [EventBus](https://github.com/greenrobot/EventBus) to set fragment state. 