package com.presentation.intellij.tips.util

/**
 * Drops the first [n] elements if [n] is not null, otherwise returns the original collection.
 */
fun <T> List<T>.dropIfNotNull(n: Int?): List<T> {
    return if (n != null) {
        this.drop(n)
    } else {
        this
    }
}

/**
 * Takes the first [n] elements if [n] is not null, otherwise returns the original collection.
 */
fun <T> List<T>.takeIfNotNull(n: Int?): List<T> {
    return if (n != null) {
        this.take(n)
    } else {
        this
    }
}

/**
 * Combines dropIfNotNull and takeIfNotNull for pagination.
 * Drops [offset] elements first, then takes [limit] elements.
 */
fun <T> List<T>.paginate(offset: Int?, limit: Int?): List<T> {
    return this.dropIfNotNull(offset).takeIfNotNull(limit)
}