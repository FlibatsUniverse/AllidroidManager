package com.flibatsuniverse.allidroid.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contributor(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val contributions: Int
)
