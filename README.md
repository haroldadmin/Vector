# Vector

Vector is an Android library to help implement the MVI architecture pattern. 

It is inspired from [MvRx](https://www.github.com/airbnb/mvrx) and [Roxie](https://github.com/ww-tech/roxie), but unlike them it is **built completely using Kotlin Coroutines** instead of RxJava. As such, it internally only uses Coroutine primitives, and has extensive support for Suspending functions.

Vector is small, fast, and works well with Android Architecture Components. It is 100% Kotlin, and is inteded for use with Kotlin only.


### Building Blocks

Vector is based primarily around three classes: `VectorViewModel`, `VectorState`, and `VectorFragment`.

* **VectorViewModel**

The Vector ViewModel class is the heart of any screen built with Vector. It is an abstract class extending the Android Architecture Components ViewModel class, and therefore survives configuration changes. It is generic on a class implementing the `VectorState` interface. It is also the only class which can mutate state.

It exposes the current state through a `LiveData` object.

* **VectorState**

VectorState is an interface denoting a model class representing the view's state. We recommend using Kotlin data classes to represent view state in the interest of keeping state immutable. Use the generated `copy()` method to create new state objects.

* **VectorFragment**

Vector provides an abstract `VectorFragment` class extending from AndroidX's Fragment class. A `VectorFragment` has a convenient `fragmentScope` coroutine scope, which can be used to easily launch Coroutines from a Fragment. 

*It is not necessary to use Fragments as Views in your projects. Subclassing VectorFragment is completely optional. While the provided sample app is built with Fragments, Vector does not assume the usage of Fragments as views.*


### Example

Here's a contrived example to show how an app written in Vector looks like.

> VectorState
```kotlin
data class MyState(val message: String): VectorState
```

> VectorFragment
```kotlin
class MyFragment: VectorFragment() {

    private val myViewModel by viewModels<MyViewModel>() 
    // 'by viewModels' delegate is a part of Fragment KTX

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        myViewModel.state.observe(viewLifecycleOwner, Observer { renderState() })
    }

    override fun renderState = withState(myViewModel) { state -> 
        messageTextView.text = state.message
    }
}
```

> VectorViewModel
```kotlin
class MyViewModel<MyState>(initState: MyState): VectorViewModel(initState) {

    suspend fun getMessage() {
        val newMessage = MessageProvider.getMessage()
        setState { copy(message = newMessage) }
    }

}
```

When the `setState()` function is given a state reducer, it internally enqueues it to a Kotlin `Channel`. The reducers passed to this channel are internally processed on a single background thread to avoid race conditions.


You can find a sample app along with the library in this repository.
If you like this project, or are using it in your app, consider starring the repository to show your support.


### Installation Instructions

Add the Jitpack repository to your top level `build.gradle` file.
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

And then add the following dependency in your module's `build.gradle` file:

```groovy
dependencies {
  implementation "com.github.haroldadmin:Vector:0.0.1"
}
```

Constributions from the community are very welcome.