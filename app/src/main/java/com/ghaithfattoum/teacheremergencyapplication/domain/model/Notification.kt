package com.ghaithfattoum.teacheremergencyapplication.domain.model

data class FcmNotification(
    val message: FcmMessage
)

data class FcmMessage(
    val topic: String,
    val notification: FcmNotificationPayload
)

data class FcmNotificationPayload(
    val title: String,
    val body: String,
    val image: String? = null
)
