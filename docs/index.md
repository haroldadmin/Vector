# Overview

![Vector](images/logo-full-coloured.svg)

Vector is a Kotlin Coroutines based MVI Architecture library for Android.

It is inspired from [MvRx](https://www.github.com/airbnb/mvrx) and [Roxie](https://github.com/ww-tech/roxie), but unlike them it is built completely using Kotlin Coroutines instead of RxJava. As such, it internally only uses Coroutine primitives, and has extensive support for Suspending functions.

Vector works well with Android Architecture Components. It is 100% Kotlin, and is intended for use with Kotlin only.

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

## R8/Proguard Config

The library ships with consumer proguard rules, so no additional configuration should be required.

## License

```
Copyright 2019 Vector Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
