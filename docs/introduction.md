# Introduction

Vector helps you build an app based on the Model-View-Intent architecture pattern.

Hannes Dorfmann has written an excellent series of blog posts about this pattern, the links to which you can find at the bottom of this page. I won't try to explain in detail what MVI is, much more qualified people have already done so in great detail. This page contains only a small summary.

!!! note
    Vector uses Experimental Coroutine API's under the hood, so the design of this library could change drastically as and when the design of Coroutines in the language evolves.

## MVI

An app is essentially a collection of screens with which the user interacts. Each screen is called a **View**.

Almost all screens in an app need some data to generate views to display on the screen. This data is called the **Model**. Views render UI elements to the screen based on the information contained in the Model.

The user interacts with these UI elements, possibly triggering some 'actions' that mutate the UI state. These actions are called **Intents**.

Intents are dispatched to a 'controller' of the screen, which mutates the Model based on the triggered action and produces a new one. The UI reacts to the updated model and re-renders itself based on the new data.

## Why use a library for this?

In the version of MVI that I just described, we can see a clear pattern followed by each screen of the app. Setting up MVI this way requires you to create a lot of boilerplate code to get things up and running. Even then, things are difficult to get right in the first try. You have to make sure that mutations to the data model are processed sequentially even though the sources of actions may be asynchronous. Vector helps you with all these things. It takes care of most of the boilerplate, state mutations are always processed sequentially even though the sources of mutations be asynchronous, and it encourages clean architectural practices.

## Why use Vector for this?

Granted, there are quite a lot of excellent MVI architecture libraries available for Android. Here are some of them, I encourage you to check them out:

* [MvRx](https://www.github.com/airbnb/MvRx)
  
    MvRx by AirBnb is an excellent library, and Vector draws a lot of its design from it. However, MvRx is heavily reliant on RxJava and has less than stellar support for Kotlin's suspending functions. It also tries to do a lot more than just simple state management, and is very close to being a framework rather than a library. Vector, on the other hand, is small and lightweight. It is also built completely using Kotlin Coroutines and therefore offers excellent support for suspending functions. It tries not to do a whole lot more than managing state, and therefore lets you freely choose how you want to design the rest of your app.

* [Roxie](https://www.github.com/ww-tech/roxie)

    Roxie is a small and lightweight library, just like Vector, but it also does not have great support for suspending functions. It also treats actions as a very well defined entity, something which Vector does not. Roxie recommends using a Kotlin Sealed Class to represent all possible actions that can be performed on a screen. It also recommends a very different pattern of reducers than what Vector does. Thus, while Roxie itself is quite small, the boilerplate that comes with it is not.

* [Mobius](https://github.com/spotify/mobius)
  
    Mobius is another popular MVI framework by Spotify, and is also recommended by a lot of developers in the community.

Vector borrows from the design of both MvRx and Roxie, and I would like to sincerely thank the developers of both of them.

## Vector's approach to MVI

* Vector recommends using a Kotlin Data Class to represent the UI Model. Your model class should implement the [VectorState](vector-state.md) interface.
* Intents are regular lambdas in Vector. State reducers are of the type `S.() -> S`, where S is a model class implementing the `VectorState` interface.
* Vector does not have an opinion regarding what `Views` should be in your app. We supply a convenient `VectorFragment` abstract class in the library, but it is not necessary to use it. While the sample app in the repository uses `Fragments`, you are free to choose whatever you like. The current state is exposed to the View through a `Kotlin Flow` object, so it helps if your View class is a `CoroutineScope` with.

### Building Blocks

* [Vector State](vector-state.md)
* [Vector Fragment](vector-fragment.md)
* [Vector ViewModel](vector-viewmodel.md)

## Further Reading

* [Hannes Dorfmann blog series on MVI](http://hannesdorfmann.com/android/mosby3-mvi-1)
* [MvRx: Android on Autopilot](https://medium.com/airbnb-engineering/introducing-mvrx-android-on-autopilot-552bca86bd0a)
