# Vector

![logo](docs/images/logo-full-coloured.svg)

[![Build Status](https://github.com/haroldadmin/vector/workflows/Android%20CI/badge.svg)](https://github.com/haroldadmin/Vector/actions)

Vector is an Android library to help implement the MVI architecture pattern. 

It is inspired from [MvRx](https://www.github.com/airbnb/mvrx) and [Roxie](https://github.com/ww-tech/roxie), but unlike them it is **built completely using Kotlin Coroutines** instead of RxJava. As such, it internally only uses Coroutine primitives, and has extensive support for Suspending functions.

Vector works well with Android Architecture Components. It is 100% Kotlin, and is intended for use with Kotlin only.

## Building Blocks

Vector is based primarily around three classes: `VectorViewModel`, `VectorState`, and `VectorFragment`.

* **VectorViewModel**

The Vector ViewModel class is the heart of any screen built with Vector. It is an abstract class extending the Android Architecture Components ViewModel class, and therefore survives configuration changes. It is generic on a class implementing the `VectorState` interface. It is also the only class which can mutate state.

It exposes the current state through a `Kotlin Flow`.

* **VectorState**

VectorState is an interface denoting a model class representing the view's state. We recommend using Kotlin data classes to represent view state in the interest of keeping state immutable. Use the generated `copy()` method to create new state objects.

* **VectorFragment**

Vector provides an abstract `VectorFragment` class extending from AndroidX's Fragment class. A `VectorFragment` has a convenient coroutine scope, which can be used to easily launch Coroutines from a Fragment.

## Example

Here's a contrived example to show how an app written in Vector looks like.

> VectorState

```kotlin
data class MyState(val message: String): VectorState
```

> VectorFragment

```kotlin
class MyFragment: VectorFragment() {

    private val myViewModel: MyViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        renderState(viewModel) { state ->
            toast(state.message)
        }
    }
}
```

> VectorViewModel

```kotlin
class MyViewModel(initState: MyState): VectorViewModel<MyState>(initState) {

    init {
        getMessage()
    }

    fun getMessage() = setState { 
        copy(message = "Hello, world!") 
    }
}
```

When the `setState()` function is given a state reducer, it internally enqueues it to a Kotlin `Actor`. The reducers passed to this actor are processed sequentially to avoid race conditions.

## Documentation

The docs can be found at the project's [documentation website](https://haroldadmin.github.io/Vector).

## Projects using Vector

* You can find a [sample app](https://github.com/haroldadmin/Vector/tree/master/sampleapp) along with the library in this repository.
* [MoonShot](https://www.github.com/haroldadmin/MoonShot) is another project of mine. It's an app to help you keep up with SpaceX launches, and is built with Vector.

If you would like your project using Vector to be featured here, please open an Issue on the repository. I shall take a look at it and add your project to the list.

## Installation Instructions

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
  implementation "com.github.haroldadmin:Vector:(latest-version)"
}
```

[![Release](https://jitpack.io/v/haroldadmin/Vector.svg)](https://jitpack.io/#haroldadmin/Vector)

## Contributing

If you like this project, or are using it in your app, consider starring the repository to show your support.
Contributions from the community are very welcome.
