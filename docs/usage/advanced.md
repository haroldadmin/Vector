# Advanced Usage

Let us continue with the example described in the [Basics](basics.md) page.

## Dependency Injection support

Our ViewModel needs access to a Repository or a Use-Case class in order to be able to fetch the user's notes. In the [Usage](basics.md) section, we setup the repository as a singleton Kotlin `object`, so that we could access it without creating an instance of it.

This is rarely the case in real applications, and it is much more common to split your business logic into different Use-Case classes. Let us create a `GetNotesUseCase` class for our notes app:

```kotlin
class GetNotesUseCase (private val repository: NotesRepository) {
  fun getAllNotes() { ... }
  fun getPinnedNotes() { ... }
  fun getArchivedNotes() { ... }
}
```

Our ViewModel now looks like this:

```kotlin
class NotesListViewModel(
  initialState: NotesListState,
  private val notesUseCase: GetNotesUseCase
): VectorViewModel<NotesListState>(initialState) {
  ...

  suspend fun getNotes() { ... }

  suspend fun getAllNotes() {
    val allNotes = notesUseCase.getAllNotes()
    setState { copy(notes = allNotes) }
  }
  suspend fun getPinnedNotes() { ... }
  suspend fun getArchivedNotes() { ... }
}
```

Due to the addition of an additional dependency in the constructor which Vector can not satisfy *on its own*, we can not instantiate this ViewModel in our Fragment or Activity in the way we did before.

We need to tell Vector how to satisfy this ViewModel's dependencies using a `VectorViewModelFactory`, implemented in this ViewModel's `companion object`.

The `VectorViewModelFactory` interface has a method named `create()`, which is used to create and return an instance of this ViewModel. The `create()` method is supplied with a `ViewModelOwner` parameter, which is a wrapper around the Fragment/Activity owning this ViewModel. It can be used to get access to your dependency injection library's object graph. **You must not store a reference to this owner in your ViewModel, or you will create a memory leak for your Activity or Fragment.**

```kotlin
class NotesListViewModel(
  initialState: NotesListState,
  private val notesUseCase: GetNotesUseCase
): VectorViewModel<NotesListState>(initialState) {
  ...

  companion object: VectorViewModelFactory<NotesListViewModel, NotesListState> {
    fun create(initialState: NotesListState, owner: ViewModelOwner, handle: SavedStateHandle): NotesListViewModel? { 
      val usecase = // use the ViewModelOwner parameter to access DI graph and get GetNotesUseCase
      return NotesListViewmodel(initialState, usecase)
    }
  }
}
```

With this factory now implemented, we can get access to our ViewModel again using view model delegates such as `by fragmentViewModel()`. Vector will lookup the factory automatically, and use it to instantiate your ViewModel.

### Koin

If you are using [Koin](https://insert-koin.io), the `create()` method in a `VectorViewModelFactory` looks like this:

```kotlin
fun create(initialState: NotesListState, owner: ViewModelOwner, handle: SavedStateHandle): NotesListViewModel? {
  val usecase = when (owner) {
    is FragmentViewModelOwner -> owner.fragment.get<GetNotesUseCase>() // `get` extension method from Koin
    is ActivityViewModelOwner -> owner.activity.get<GetNotesUseCase>()
  }
  return NotesListViewmodel(initialState, usecase)
}
```

### Dagger and AssistedInject

[Dagger](https://github.com/google/dagger) can generate your DI graph at compile time, but it can not handle constructor paramters only available at run-time such as the `initialState` parameter in our ViewModel. The [AssistedInject](https://github.com/square/AssistedInject) library helps with this, as it automatically generates factories for classes which depend on such runtime parameters.

Usage of AssistedInject and Dagger in Vector looks like this:

```kotlin
class NotesListViewModel @AssistedInject constructor(
  @Assisted initialState: NotesListState,
  private val usecase: GetNotesUseCase
) {
  
  ...

  @AssistedInject.Factory
  interface Factory {
    fun create(initialState: NotesListState): NotesListViewModel
  }
}

class NotesListFragment: VectorFragment() {
  @Inject
  lateinit var viewModelFactory: NotesListViewModel.Factory

  private val viewModel: NotesListViewModel by fragmentViewModel { initialState, savedStateHandle ->
    viewModelFactory.create(initialState)
  }

  override fun onCreate(...) {
    inject()
    super.onCreate(...)
  }
}
```

We leverage another ViewModel delegate supplied by Vector which accepts a ViewModel producing lambda as an input. When this lambda is supplied, you don't need to implement the `VectorViewModelFactory` interface just to create your ViewModel with custom dependencies.

## Handling process death

### What's process death?

When Android kills your application process while it is in the background, we call it process death. Before your application is killed, `onSaveInstanceState` is called for your Activities/Fragments, which can persist their state in a Bundle. When your application is recreated after a Process Death, you receive this bundle back as an argument as `savedInstanceState` in the `onCreate()` method.

### Doesn't a ViewModel handle this automatically?

No, a ViewModel is built to handle configuration changes, such as rotations or locale changes. It does not survive process death. Any state held by the ViewModel is lost after a process death, and is not recoverable.

The ViewModel is meant to handle state while in-memory, and the owning Fragment/Activity is supposed to save relevant parts of this state when being killed. **You need to use both of these together to correctly handle state restoration**.

### Problems with onSaveInstanceState

This setup works okay-ish when your application is small, or when the state is not complex. The biggest problem with this setup is that it also makes the Activity or Fragment responsible for managing state. Besides, it is difficult to implement this method correctly, and the user must also remember to extract the saved state from the `savedInstanceState` bundle.

### Vector's solution

The [AndroidX ViewModel-SavedState](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) library helps with this problem. Vector builds on this library and provides a `SavedStateVectorViewModel`, which uses the `SavedStateHandle` object to help with state persistence.

```kotlin
class NotesListViewModel @AssistedInject constructor(
  @Assisted initialState: NotesListState,
  @Assisted handle: SavedStateHandle,
  private val notesUse: GetNotesUseCase
): SavedStateVectorViewModel(intitialState, savedStatehandle = handle)
```

The `SavedStateVectorVieWModel` has additional methods to help with state persistence: `setStateAndPersist` and `persistState`.
To use them, we need to make sure that our state class implements the `Parcelable` interface. We can use the `@Parcelize` annotation from [Kotlin-Android-Extensions](https://kotlinlang.org/docs/tutorials/android-plugin.html) to automatically generate the implementation for us.

```kotlin
@Parcelize
data class NotesListState(...): Parcelable
```

#### Persisting state

To persist state, we can simply replace the usage of `setState` in our ViewModel with `setStateAndPersist` to make sure that we save the latest state whenever it is modified. This way, whenever the application process is killed we would have already persisted the latest state.

The `setStateAndPersist` method first invokes the state reducer, and then calls `persistState()`. The default implementation of `persistState()` simply takes the state object, and saves it to the `SavedStateHandle` using the key `KEY_SAVED_SAVED` defined in the companion object of `SavedStateVectorViewModel`.

To customize this behaviour, you can override the `persistState()` method and provide your own implementation.

#### Restoring state

To restore the state, the `initialState` method of `VectorViewModelFactory` must be implemented in the ViewModel's companion object:

```kotlin
fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): NotesListState? {
  val persistedState = handle[KEY_SAVED_STATE]
  if (persistedState != null) {
    return persistedState
  } else {
    ...
  }
}
```
