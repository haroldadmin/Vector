package com.haroldadmin.vector.viewModel

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

/**
 * The Base ViewModel class your ViewModel should inherit from
 *
 * @param S The state class for this ViewModel implementing [VectorState]
 * @param initialState The initial state for this ViewModel
 * @param enableLogging Flag to enable/disable debug logs on state updates
 *
 * Initial State can be used in conjunction with fragment provided state to
 * recover from process deaths.
 *
 * Example:
 *
 * class MyViewModel(initState: MyState?) : VectorViewModel<MyState, MyAction>(initState ?: MyState()) {
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
abstract class VectorViewModel<S : VectorState>(private val initialState: S, private val enableLogging: Boolean = false) : ViewModel() {

    /**
     * The state store associated with this view model.
     * The state store manages synchronized accesses and mutations to state
     *
     * Initialized lazily because the initialState needs to be initialized by the subclass
     */
    protected val stateStore: StateStore<S> by lazy { StateStoreImpl(initialState) }

    /**
     * Internal backing field for the [LiveData] based state observable exposed to View objects
     */
    private val _state = MutableLiveData<S>()

    /**
     * The observable live data class to provide current state to views.
     * Activities and Fragments may subscribe to it to get notified of state updates.
     */
    val state: LiveData<S> = MediatorLiveData<S>().apply {
        addSource(_state, this::setValue)
    }

    /**
     * A convenience property to access the current state without having to observe it
     *
     * This state is not guaranteed to be the latest, because there might be other state mutation
     * blocks in queue in the state store.
     */
    val currentState: S
        get() = stateStore.state

    /**
     * The only method through which state mutation is allowed in subclasses.
     *
     * Dispatches an action to the actions channel. The channel reduces the action
     * and current state to a new state and sets the new value on [_state]
     *
     * @param reducer The state reducer to create a new state from the current state
     */

    protected fun setState(reducer: suspend S.() -> S) {
        stateStore.set(reducer)
    }

    protected fun withState(block: suspend (S) -> Unit) {
        stateStore.get(block)
    }

    init {
        viewModelScope.launch {
            Vector.log("Connecting StateChannel to LiveData")
            stateStore.stateChannel.consumeEach { state -> _state.value = state }
        }
    }

    @CallSuper
    override fun onCleared() {
        Vector.log("Clearing ViewModel")
        super.onCleared()
        stateStore.cleanup()
    }
}