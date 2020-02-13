# Basics

To demonstrate the usage of Vector, let us build an imaginary note taking app. We are going to build the screen where we show all the notes saved by our user.

A screen written using Vector typically has three components: A view (such as a fragment), a state class, and a ViewModel.

## The State class

The state class is a model for the UI state. It should have the following characteristics:

* Immutable, as the data in it could be accessed by multiple threads
* Contain all necessary data to render the UI
* Have default values for their properties.

Our Notes screen at the very least needs a list of all the notes of our user. Additionally, let us also add the ability to filter the notes. Here's what the state class might look like:

```kotlin
data class NotesListState(
  val notes: List<Note> = listOf(),
  val filter: Filter = Filter.ALL
): VectorState

enum class Filter {
  ALL, PINNED, ARCHIVED
}
```

Vector requires that you implement the `VectorState` interface in your state classes. It is an empty interface, and serves only as a marker for these classes.

For an in-depth look at defining state, please read [Vector State](../components/vector-state.md).

## The ViewModel class

The ViewModel class serves as a holder for UI state, and also manages mutations/access to it. It survives configuration changes, and is tied to the logical lifecycle of a Fragment or Activity. Vector provides a [`VectorViewModel`](../components/vector-viewmodel.md) component for this. You should extend this class in your own ViewModels. Let's use it to build our `NotesListViewModel`.

### Initial State

```kotlin
class NotesListViewModel(
  initialState: NotesListState
): VectorViewModel<NotesListState>(initialState)
```

Our ViewModel has a dependency on an `initialState` object. Therefore, this ViewModel can **not** be instantiated automatically with the `ViewModelProviders` class in the AndroidX Lifecycle library. We shall see how to get a hold of this ViewModel in the section on the [presenter class](#getting-hold-of-the-viewmodel).

The `initialState` parameter represents the default state of UI. Our ViewModel needs this in order to be able to tell the presenter what to show the user when it first loads.

### Managing State

A `VectorViewModel` exposes a [Kotlin Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html) of UI State to its owning Fragment/Activity. The owner can subscribe to this state flow, and render the UI according to whatever value is contained in it.

From our ViewModel, whenever we produce a new state it should be pushed to this Flow, so that the UI can be informed that a new state has been created, and that it should re-render itself. When the ViewModel is first created, it pushes the `initialState` object to the state flow.

Our ViewModel is ready to get all the notes from our Repository, so let us fetch them as soon as the ViewModel is created.

```kotlin
class NotesListViewModel(
  initialState: NotesListState
): VectorViewModel<NotesListState>(initialState) {

  init {
    viewModelScope.launch { getAllNotes() }
  }

  suspend fun getAllNotes() = withContext(Dispatchers.IO) {
    val notes = Repository.allNotes()
    ...
  }
}
```

Now that we have the notes, we are ready to show them to the user. How do we do this? By modifying the current state!

#### Modifying/Mutating state

We can modify the current state using the `setState` method, like this:

```kotlin
suspend fun getAllNotes() = withContext(Dispatchers.IO) {
  val allNotes = Repository.allNotes()
  setState {
    // `this` = the current state
    copy(notes = allNotes)
  }
}
```

The `setState` method accepts a lambda which has the current state as the receiver. This lambda is often called as a *"Reducer"*. Since we used a data class to model our state, we have a `copy()` method on it generated automatically for us. We can use it to produce a new state from our current state.

When the `setState` method is called, it enqueues its reducer to an internal queue. A coroutine processes the reducers in this queue sequentially on a background thread. Hence, **state updates are performed asynchronously**. When this reducer is processed, the new state object produced by it is pushed to the state flow to notify the UI that it should re-render itself. All of this happens automatically, and you do not need to worry about it.

So when our `setState` block has finished processing, our UI will be notified that it should show this list of notes to our user! 🎉🎉🎉

!!! warning
    Since state updates are processed asynchronously, you should not assume that you shall get the updated state immediately after the `setState` block.
    To get access to the latest state within a ViewModel, *always* use `withState` method.

#### Accessing State

Let us also add the ability to filter notes. We need to fetch notes based on what filter is set. To know what filter is currently set, we need access to the current state. The correct way to do this is to use the `withState` method.

```kotlin
suspend fun getNotes() = withState { state ->
  when (state.filter) {
    ALL -> getAllNotes()
    PINNED -> getPinnedNotes()
    ARCHIVED -> getArchivedNotes()
  }
}
```

The `withState` block receives the current state as a parameter. We can use to take decisions which depend on the current state.

`withState`, just like `setState`, is processed asynchronously.

Our ViewModel now looks like this:

```kotlin
class NotesListViewModel(
  initialState: NotesListState
): VectorViewModel<NotesListState>(initialState) {

  init {
    viewModelScope.launch { getNotes() }
  }
  
  suspend fun getNotes() = withState { state ->
    when (state.filter) {
      ALL -> getAllNotes()
      PINNED -> getPinnedNotes()
      ARCHIVED -> getArchivedNotes()
    }
  }

  suspend fun getAllNotes() { ... }
  suspend fun getPinnedNotes() { ... }
  suspend fun getArchivedNotes() { ... }
}
```

## The View class

The view class serves as the UI for your application. Vector provides a simple [`VectorFragment`](../components/vector-fragment.md) component for this. It is a subclass of the AndroidX Fragment, and has a convenient `renderState` to collect state updates and render the UI. 
Let's use it to build our `NotesListFragment` class.

```kotlin
class NotesListFragment: VectorFragment()
```

### Getting hold of the ViewModel

Our Fragment needs access to its ViewModel. We usually use the `ViewModelProviders` class for this, **but this will not work here**, unless we create our own `ViewModelProvider.Factory` which knows how to create our ViewModel. Even then, the factory might not know how to create the initial state in more complex use cases.

Vector provides some convenient lazy delegates to take care of this for you. From inside a fragment, we can request a ViewModel scoped to a fragment like this:

```kotlin
class NotesListFragment: VectorFragment() {
  private val viewModel: NotesListViewModel by fragmentViewModel()
}
```

This will lazily instantiate the ViewModel for you! 🎉🎉🎉

!!! note
    ViewModel delegates supplied by Vector can only create ViewModels with the same dependencies as the `VectorViewModel` class. If you have other dependencies in your ViewModel's constructor, please take a look at the "ViewModels with additional dependencies" section in the [documentation for VectorViewModel](../components/vector-viewmodel.md).

### Observing state

Now that we have our ViewModel, we can start observing state changes. To do this, we need to subscribe to the state flow exposed by the ViewModel. Subscribing to a Flow requires a Coroutine Scope, and luckily `VectorFragment` provides us with one.

```kotlin
class NotesListFragment: VectorFragment() {

  private val viewModel: NotesListViewModel by fragmentViewModel()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_message, container, false)
    
    renderState(viewModel) { state ->
      recyclerViewAdapter.submitList(state.notes)
    }
    
    return root
  }
}
```

The `renderState` block is lifecycle aware. State updates are rendered only while the View lifecycle is active.

Now whenever we get a new state object from our ViewModel, we automatically update the user interface! 🎉🎉🎉

### Sending Actions to the ViewModel

We have established how a ViewModel communicates with the presenter, but not how a presenter communicates with the ViewModel. In MVI-speak, presenter communicates with the ViewModel through `Intents`, or `Actions`. We are **not** referring to the `android.content.Intent` class here.

Vector does not need you to define any classes which specify your `Actions`. A simpler way to communicate actions to the ViewModel is to simply call methods on it.

Whenever the user changes their filter setting, we must notify our ViewModel. Let us add a method on our ViewModel to set a filter.

```class NotesListViewModel(...): VectorViewModel<NotesListState>(...) {
  fun changeFilter(newFilter: Filter) = setState {
    copy(filter = newFilter)
    getNotes()
  }
}
```

Now whenever the user sets a new filter, we fetch notes for it and update our state. The UI then re-renders according to the updated state.

We have a working Notes list screen now! 🎉🎉🎉

## Advanced usage

For more advanced use cases, such as dependency injection in a ViewModel, automatic state persistence across process deaths, customization of state store Coroutine Context, logging and more, please explore the relevant sections under the Component section in the navigation bar.
