# Vector Fragment

Vector Fragment is an abstract class that extends the AndroidX `Fragment` class.

`VectorFragment` class also has an abstract `renderState(state: S, renderer: (S) -> Unit)` method. 
It is supposed to be the place where Views update themselves according to the state update provided by the ViewModel. 

You are free to choose your own implementation and not extend from `VectorFragment` at all.

An example of how the `renderState` function should look like:

```kotlin
class UsersListFragment: VectorFragment() {

    private val viewModel: UserViewModel by viewModel()

    override fun onViewCreated(...) {
        renderState(viewModel) { state ->
            // Update your views here
        }
    }
}
```

The `renderState` method internally launches a coroutine in a Coroutine Scope scoped to fragment's view-lifecycle.

Vector is not opinionated about what should be used as Views in an app, so please feel free to use whatever you like. However, the `VectorViewModel` class exposes the current state observable as a `Kotlin Flow` object, so it helps if your View object is a `CoroutineScope`.
