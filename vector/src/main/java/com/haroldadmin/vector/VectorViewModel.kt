package com.haroldadmin.vector

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The Base ViewModel class your ViewModel should inherit from
 *
 * @param consumeDelay Time period between each dispatched action consumption
 * @param S The state class for this ViewModel implementing [VectorState]
 * @param A The class containing all possible actions for this ViewModel. Should implement [VectorAction]
 */
abstract class VectorViewModel<S : VectorState, A : VectorAction>(private val consumeDelay: Long = 0L) : ViewModel() {

    private val actions = Channel<A>(capacity = Channel.UNLIMITED)

    /**
     * The initial state for this ViewModel
     *
     * Override this to provide your own implementation of initial state.
     * This can be used in conjunction with fragment provided state to
     * recover from process deaths.
     *
     * Example:
     *
     * class MyViewModel(initState: MyState?) : VectorViewModel<MyState, MyAction>() {
     *
     *      override val initialState = initState ?: MyState()
     *
     *      ...
     * }
     *
     * class MyFragment {
     *      onActivityCreated(savedInstanceState: Bundle?) {
     *          val initialState: MyState? = null
     *          if (savedInstanceState != null) {
     *              initialState = MyState(bundle.getString("USER_ID")
     *          }
     *          val viewModel = ViewModelProviders.of(this, MyVMFactory(initialState)).get(MyViewModel::class.java)
     *      }
     * }
     */
    protected abstract val initialState: S

    /**
     * The function that takes in a state and an action and outputs a new state
     */
    protected abstract val reducer: Reducer<S, A>

    private val _state = MutableLiveData<S>()

    /**
     * The observable live data class to provide state to views.
     * Activities and Fragments may subscribe to it to get notified of state updates.
     */
    val state: LiveData<S> = _state

    /**
     * A convenience property to access the current state without having to observe it
     */
    val currentState: S
        get() = _state.value ?: initialState

    /**
     * The only method through which state mutation is allowed in subclasses.
     *
     * Dispatches an action to the actions channel. The channel reduces the action
     * and current state to a new state and sets the new value on [_state]
     *
     * @param action The action object to be dispatched
     */
    protected fun dispatch(action: A) = viewModelScope.launch {
        actions.send(action)
    }

    init {
        viewModelScope.launch {
            actions.consumeEach { action ->
                _state.value = reducer(currentState, action)
                if (consumeDelay > 0) delay(consumeDelay)
            }
        }
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        actions.close()
    }
}