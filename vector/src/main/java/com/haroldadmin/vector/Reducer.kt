package com.haroldadmin.vector

/**
 * A function which takes in the current state and an action,
 * and produces a new state based on the given action.
 *
 * @param S The state class type
 * @param A The action type
 */
typealias Reducer<S, A> = S.(A) -> S