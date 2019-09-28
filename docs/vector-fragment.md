# Vector Fragment

Vector Fragment is an abstract class that extends the AndroidX `Fragment` class.

You are not required to use it, the only benefit it offers is that it has a convenient `fragmentScope` Couroutine Scope which can be used to easily launch Coroutines.

`VectorFragment` class also has an abstract `renderState(state: S, renderer: (S) -> Unit)` method, which should be overridden by subclasses. It is supposed to be the place where Views update themselves. You are free to choose your own implementation and not extend from `VectorFragment` at all.

An example of how the `renderState` function should look like:

```kotlin
class UsersListFragment: VectorFragment() {
    
    private val viewModel by viewModels<UsersViewModel>

    onActivityCreated(savedInstanceState: Bundle?) {
        fragmentScope.launch {
            viewModel.state.collect { state -> 
                renderState(state, this@UsersListFragment::renderer)
            }
        }
    }

    private fun renderer(state: UsersListState) {        
        usersListAdapter.submitList(state.usersList)
    }
}
```

Vector is not opinionated about what should be used as Views in an app, so please feel free to use whatever you like.
However, the `VectorViewModel` class exposes the current state observable as a `Kotlin Flow` object, so it helps if your View object is a `CoroutineScope`.