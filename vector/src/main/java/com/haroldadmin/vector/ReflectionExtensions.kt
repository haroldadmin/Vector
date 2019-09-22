package com.haroldadmin.vector

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberFunctions

/**
 * Tries to find the companion object of a class that implements [VectorViewModelFactory] and
 * returns it. If no such companion object is found, it throws [DoesNotImplementVectorVMFactoryException]
 */
internal fun Class<*>.factoryCompanion(): Class<*> {
    return companionObject()?.let { clazz ->
        if (clazz doesImplement VectorViewModelFactory::class.java) {
            clazz
        } else {
            null
        }
    } ?: throw DoesNotImplementVectorVMFactoryException()
}

/**
 * Overloaded version of java class factoryCompanion method
 */
internal fun KClass<*>.factoryCompanion(): Class<*> {
    return this.java.factoryCompanion()
}

internal fun KClass<*>.factoryKompanion(): KClass<*> {
    return this.factoryCompanion().kotlin
}

/**
 * Tries to find the companion object of the given class, and returns it. If the class does not
 * have a companion object, returns null
 */
internal fun Class<*>.companionObject(): Class<*>? {
    return try {
        Class.forName("$name\$Companion")
    } catch (ex: ClassNotFoundException) {
        null
    }
}

/**
 * Creates a new instance of the given class using the constructor having one parameter only.
 * If no such constructor exists, returns null.
 */
internal fun Class<*>.instance(initArg: Any? = null): Any? {
    return declaredConstructors.firstOrNull { it.parameterTypes.size == 1 }?.newInstance(initArg)
}

/**
 * Syntactic sugar for [Class.isAssignableFrom] method
 */
internal infix fun Class<*>.doesImplement(other: Class<*>): Boolean {
    return other.isAssignableFrom(this)
}

internal infix fun KClass<*>.doesImplement(other: KClass<*>): Boolean {
    return other.java.isAssignableFrom(this.java)
}

/**
 * Kotlin interfaces with default methods don't create equivalent Java interfaces with default methods.
 * https://youtrack.jetbrains.com/issue/KT-4779
 *
 * Therefore we can't use general java reflection to find out if a class has overridden a default interface method.
 * This extension allows us to do it though, using Kotlin reflection.
 */
internal infix fun KClass<*>.doesOverride(methodName: String): Boolean {
    return this.memberFunctions.first { it.name == methodName } in this.declaredFunctions
}