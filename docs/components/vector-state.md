# Vector State

Vector recommends using immutable [Kotlin Data Classes](https://kotlinlang.org/docs/reference/data-classes.html?q=&p=0#data-classes) to represent your UI Model classes. Such classes should implement the `VectorState` interface, because the `VectorViewModel` class is generic on a subtype of this interface.

It is recommended to keep your state classes immutable, otherwise you risk your UI model getting into inconsistent states when there are multiple sources producing state updates concurrently.

## Example

```kotlin
data class ProfilePageState(
  val user: User,
  val isLoading: Boolean,
  val isError: Boolean
): VectorState
```

Using a Data Class provides the benefit of the automatically generated `copy()` method. It allows you to mutate the state very easily, you just need to provide the values that have actually changed, and the others will be kept the same. For example, if the User Profile page in the above example starts in the `Loading` state, the initial state model would look like this:

```kotlin
val initialState = ProfilePageState(
  user = cachedUser,
  isLoading = true,
  isError = false
)
```

When the loading completes, the state can be mutated easily like this:

```kotlin
val newState = initialState.copy(
  user = userRetrievedFromNetwork,
  isLoading = false
)
```

Since the `isError` variable value remains the same (false), we do not need to supply it in the `copy` method.

## Sealed Class based Model Classes

A great way to represent all possible states a screen can be in is using Kotlin's Sealed Classes.

Continuing the profile page example, suppose we want to show a different types of information based on whether the user is a premium user or not. We can do it this way:

```kotlin
sealed class ProfilePageState: VectorState {
  data class PremiumProfilePage(
    val user: User,
    val accountPerks: List<Perks>,
    val isLoading: Boolean,
    val isError: Boolean
  ): ProfilePageState()

  data class StandardProfilePage(
    val user: User,
    val isLoading: Boolean,
    val isError: Boolean
  ): ProfilePageState()
}
```

This allows you to separate similar, but related states of a screen. It increases verbosity though, and you lose direct access to convenient data class methods, unless you type cast the state object into one of its subclasses.

## Persistable state

[Kotlin Extensions for Android](https://kotlinlang.org/docs/tutorials/android-plugin.html) have the ability to automatically generate `Parcelable` implementations of data classes with the `@Parcelize` annotation. This can be leveraged to easily persist state classes when needed.

```kotlin
@Parcelize
data class ProfilePageState(
  ...
): Parcelable
```

When needed, this state can be directly put as a `Serializable` into a `SavedInstanceState` bundle or a `SavedStateHandle` in a ViewModel.

For members in your state class which should not be persisted, `@Transient` annotation can be used.

```kotlin
@Parcelize
data class ProfilePageState(
  val userId: Long,
  @Transient val user: User
): VectorState
```

Doing this ensures that the transient values in the state object are not persisted.

!!! info
    Your state object should only persist the minimum amount of information required to rebuild it fully after a process death.

## Automatic State Restoration

If you use the lazy ViewModel delegates shipped with Vector, you must ensure that either:

* Your state class has default values for every property, or
* Your ViewModel class implements the `VectorViewModelFactory` interface along with its `initialState` method

This is to ensure that we can create an instance of your state class automatically.
