# Coroutine Context for the State Store

Every `VectorViewModel` has a backing `StateHolder` and a `StateStore`. The `StateHolder` is responsible for holding the current state, and the `StateStore` is responsible for processing state access/mutation blocks.

All state related actions are processed off the main thread, in a sequential manner using a Coroutine Actor. The coroutine context for this actor is by default defined as: `Dispatchers.Default + Job()`.

If you wish to customize the state store coroutine context, you may do so by supplying your custom context in the ViewModel.

```kotlin
abstract class VectorViewModel<S : VectorState>(
    initialState: S?,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(), // <- Change this parameter in your own implementations
    protected val logger: Logger = androidLogger()
)
```