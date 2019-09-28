# Logging

Vector can log state related actions. It ships with two different loggers:

* AndroidLogger: Writes log statements to Android log (`Log.*`). Can be created using the `androidLogger()` factory function.
* SystemOutLogger: Writes log statements to STDOUT (`println`). Can be created using the `systemOutLogger()` factory function.

By default, the `AndroidLogger` is used, but you can customize which ever one you want based on your needs. The `Logger` interface is very simple, and you can use it to create your custom implementations as well.

## Enable/Disable Logging

You can enable or disable logging by setting `Vector.enableLogging`.
