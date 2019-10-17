# SavedState VectorViewModel

A subclass of [`VectorViewModel`](vector-viewmodel.md) which provides easier state persistence across process deaths, by providing access to a `SavedStateHandle` from [ViewModel SavedState](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) library in Android Jetpack.

## Making state persistable

The easiest way to persist your UI state is to simply save the entire state object using the saved state handle. However, you state class needs to implementing `Parcelable` for this, which is a tedious and error prone process.

Luckily, Kotlin comes with an [Android Extension](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) which helps use make our classes `Parcelable` with just a single annotation.

```kotlin
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserState(
  val userId: Long = -1,
  val user: User? = null,
  val isUserPremium: Boolean = false
): Parcelable
```

Make sure that you have turned on the experimental flag in your `build.gradle` file to be able to access this feature:

```groovy
androidExtensions {
    experimental = true
}
```

## Persisting State

The `SavedStateVectorViewModel` class has a `setStateAndPersist` method which is the same as the regular `setState` method, except that it also persists the new state.

```kotlin
fun greetUser() = setStateAndPersist {
  copy(greeting = "Hello!")
}
```

If you want to exclude some properties in your state object from being persisted, you must annotate them with `@Transient`.

```kotlin
@Parcelize
data class UserState(
  val userId: Long = -1,
  @Transient val user: User? = null,
  val isUserPremium: Boolean = false
): Parcelable
```

This method by default tries to persist your entire state object using `KEY_SAVED_STATE` key defined in this class. If you need to customize this behaviour, you should override the `persistState` method.

```kotlin
override fun persistState() = withState { state ->
  // Your custom implementation
}
```
