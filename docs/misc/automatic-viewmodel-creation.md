# Automatic ViewModel creation

Vector ships with some lazy delegates for instantiating ViewModels automatically.

```kotlin
val viewModel by fragmentViewModel()
              by activityViewModel()
              by viewModel()

val viewModel by fragmentViewModel { initialState, savedStateHandle -> ... }
              by activityViewModel { initialState, savedStateHandle -> ... }
              by viewModel { initialState, savedStateHandle -> ... }
```

These delegates use Reflection to instantiate your ViewModels. The process goes as follows:

* First, we try to create the initial state for your ViewModel using either the ViewModel factory or using the constructor.
    * If the ViewModel implements `VectorViewModelFactory` in its companion object, we attempt to create initial state using it
    * If the ViewModel does not implement that interface, or it does not override the `initialState()` method, then we attempt to create initial state using the state class constructor. For this to succeed, all properties in your state class must have default values.
    * If both the strategies fail, we throw an exception and crash.

* Then, we try to create the ViewModel.
    * If the delegate has been supplied a trailing lambda which tells us how to produce the ViewModel, we invoke it, register the ViewModel with the `ViewModelProvider` for the calling Activity/Fragment and return it.
    * Otherwise, we check if the ViewModel implements `VectorViewModelFactory` in its companion object. If so, we attempt to create the ViewModel using its `create` method.
    * If the ViewModel does not implement that interface or if the returned ViewModel is null, we try to create the ViewModel using its constructor. For this to succeed, the ViewModel must have one of the following constructors:
        1. ViewModel()
        2. ViewModel(initialState)
        3. ViewModel(initialState, savedStateHandle)
        4. ViewModel(initialState, stateStoreContext, savedStateHandle)
  
  * If these conditions can not be met, we throw an exception and crash.

Therefore, the ViewModel's `VectorViewModelFactory` is given priority for both tasks if it is implemented. Otherwise, we resort to constructor invocations.
