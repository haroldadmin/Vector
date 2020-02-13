# Coroutine Context for the State Store

Every `VectorViewModel` has a backing `StateHolder` and a `StateStore`. The `StateHolder` is responsible for holding the current state, and the `StateStore` is responsible for processing state access/mutation blocks.

All state related actions are processed off the main thread, in a sequential manner. The coroutine context for processing these actions can be customized using the ViewModel.
Just pass in the desired context to the ViewModel's constructor.

```kotlin
abstract class VectorViewModel<S : VectorState>(
    initialState: S?,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(), // <- Change this parameter in your own implementations
    protected val logger: Logger = androidLogger()
)
```