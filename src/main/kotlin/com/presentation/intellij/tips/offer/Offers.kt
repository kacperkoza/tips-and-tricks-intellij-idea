package com.presentation.intellij.tips.offer

import java.net.URI

data class Offers(
    val offers: List<Offer>,
)

data class Offer(
    val id: Long,
    val description: String,
    val imageUrl: URI,
)