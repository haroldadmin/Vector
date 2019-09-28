# Vector ViewModel

The `VectorViewModel` class is the place where the UI state is stored. It is also the only place which can perform changes to the UI state. You must extend this class in your own ViewModels. It is generic on a state class implementing the `VectorState` interface.

```kotlin
data class UserState(...): VectorState

class UserViewModel(
    initialState: UsersState
): VectorViewModel<UsersState>(initialState) { ... }
```

## Creating a ViewModel

A `ViewModel` is scoped to the Lifecycle of its owning Fragment or Activity. An Activity can access Activity-scoped ViewModels, whereas Fragments can create both Fragment-scoped and Activity-scoped ViewModels.

The library ships with a few [Kotlin property delegates](https://kotlinlang.org/docs/reference/delegated-properties.html) which make it easy to create a `VectorViewModel` for whichever scope you need.

* From a Fragment:

```kotlin
val userViewModel: UserViewModel by fragmentViewModel() // Scoped to this fragment
// OR
val userViewModel: UserViewModel by activityViewModel() // Scoped to the parent activity
```

* From an Activity:

```kotlin
val userViewModel: UserViewModel by viewModel() // Scoped to this activity
```

These delegates automatically create the ViewModel for you, as long as they do not have any external dependencies.

### ViewModels with additional dependencies

If your ViewModel has external dependencies, then you should use an alternative version of these delegates which accepts a trailing lambda that should contain the code to create your ViewModel.

```kotlin
val userViewModel: UserViewModel by fragmentViewModel { initialState, savedStateHandle ->
    UserViewModel(initialState, UserRepository())
}
```

Alternatively, you can choose to implement the `VectorViewModelFactory` interface in your ViewModel's companion object to provide your own implementation for its `create` and `initialState` methods.

```kotlin
class UserViewModel(initialState: UserState, val repository: UserRepository) {
    ...
    companion object: VectorViewModelFactory<UserViewModel, UserState> {
        override fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): UserState? {
            // Create state object directly or restore it using `SavedStateHandle`
        }

        override fun create(initialState: UserState, owner: ViewModelOwner, handle: SavedStateHandle): UserViewModel? { 
            // Create and return your ViewModel
            // the `owner` parameter can be used to access your DI graph
        }
    }
}
```

### Support for AssistedInject factories

If you use [Dagger](https://github.com/google/dagger) and [AssistedInject](https://github.com/square/AssistedInject) in your project, then you can create ViewModels in this way:

```kotlin
@Inject val usersViewModelFactory: UsersViewModel.Factory // The AssistedInject factory

val userViewModel: UserViewModel by fragmentViewModel { initialState, savedStateHandle ->
    userViewModelFactory.create(initialState, ...)
}
```

## Managing State

### Mutating State

State mutation is done through the `setState` function, which accepts regular lambdas as well as **suspending lambdas**. The supplied lambda is given the current state as [the receiver](https://kotlinlang.org/docs/reference/lambdas.html#function-literals-with-receiver), and it is responsible for creating a new state and returning it.

```kotlin
class UserViewModel(...): VectorViewModel<UserState>(...) {
    fun greetUser() = setState {
        val currentState = this // this = current state
        val newState = UsersState(greeting = "Hello!", user = currentState.user)
        newState
    }
}
```

If the state class is a [Kotlin Data class](https://kotlinlang.org/docs/reference/data-classes.html#data-classes), then this can be expressed succintly as:

```kotlin
fun greetUser() = setState {
    copy(greeting = "Hello!")
}
```

This works because the `this` received in the `setState` block is the current state, which has a `copy` method define for it by virtue of being a data class.

!!! note
    State mutations are processed asynchronously. You should not rely on the state to be updated immediately after you call the `setState` function. Every state mutation is enqueued to a `Channel` on a background thread, which processes them sequentially to avoid race conditions.

### Accessing State

If you need to access the current state and perform some action based on it, you should use the `withState` function. It receives the current state as a parameter, and can then use it to perform decisions based on it.

```kotlin
fun greetUser() = withState { state ->
    if (state.isUserPremium()) {
        setState { copy(greeting = "Hello, premium user!") }
    } else {
        setState { copy(greeting = "Hello!")}
    }
}
```

`withState` blocks, just like `setState` blocks, are processed on a background thread asynchronously.
The state parameter supplied to `withState` is guaranteed to be the latest state at the time of processing the lambda. Any nested `setState` blocks are processed immediately, before any other `withState` blocks can be processed.

!!! warning
    While there are other ways to access the state in your ViewModel, using the `withState` function is the safest way to do so. Since state updates are processed asynchronously, other methods are not guaranteed to have the latest state when you access it. The `withState` block always receives the latest state as a parameter when it is processed.

!!! note
    There's also a `currentState` property in a `VectorViewModel`, but it should not be used in place of a `withState` block. `currentState` only provides a convenient way for external classes to access the current state without subscribing to it.

## Observing state changes

A `VectorViewModel` exposes state to fragments and activities through a [Kotlin Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html).

A Flow is a cold stream of values, which is active only while there is someone subscribing to it.

You can subscribe to state changes like this:

```kotlin
class UserActivity: AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModel()
    private val coroutineScope = MainScope()

    override fun onCreate(...) {
        coroutineScope.launch {
            userViewModel.state.collect { state -> updateState(state) }
        }
    }

    fun updateState(state: UserState) { ... }
    override fun onDestroy(...) { coroutineScope.cancel() }
}
```

### Example

Here's an example of how to use the VectorViewModel:

```kotlin
data class UserState(
    val user: User? = null,
    isError: Boolean = false,
    isLoading: Boolean = false
): VectorState

class UserViewModel(
    initState: UserState,
    private val repository: UserRepository
): VectorViewModel<UsersState>(initState) {

    init {
        viewModelScope.launch { getUserDetails() }
    }

    private suspend fun getUserDetails() {
        setState { copy(isLoading = true) }
        val users = repository.getUser()
        if (user == null) {
            setState { copy(user = null, isError = true, isLoading = false) }
        } else {
            setState { copy(usersList = users, isLoading = false) }
        }
    }
}

class UserFragment: VectorFragment() {
    private val viewModel: UserViewModel by fragmentViewModel { initialState, savedStateHandle ->
        UserViewModel(initialState, savedStateHandle)
    }

    override fun onCreate(...) {
        renderState(viewModel) { state ->
            ...
        }
    }
}
```

!!! warning
    ViewModels only survive configuration changes such as screen rotations. They do NOT survive process death.

## State Persistence

While ViewModels are great for storing UI state because they survive configuration changes, you still need to take care of persisting your UI state in the event of a process death.

To make this process easier, the library ships with a specialized version of `VectorViewModel`, named the [`SavedStateVectorViewModel`](saved-state-vectorviewmodel.md) which leverages the [ViewModel SavedState Module](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) for state persistence.
