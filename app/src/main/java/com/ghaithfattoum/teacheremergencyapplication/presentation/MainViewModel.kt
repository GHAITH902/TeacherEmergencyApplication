package com.ghaithfattoum.teacheremergencyapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghaithfattoum.teacheremergencyapplication.data.RetrofitInstance
import com.ghaithfattoum.teacheremergencyapplication.domain.model.FcmMessage
import com.ghaithfattoum.teacheremergencyapplication.domain.model.FcmNotification
import com.ghaithfattoum.teacheremergencyapplication.domain.model.FcmNotificationPayload
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {

    private val _event = MutableLiveData<UiEvent>()
    internal val event: LiveData<UiEvent> = _event

    internal fun sendNotification(
        accessTokenFileInputStream: InputStream,
        title: String,
        message: String
    ) {
        FcmNotification(
            message = FcmMessage(
                topic = TOPIC,
                notification = FcmNotificationPayload(
                    title = title,
                    body = message
                )
            )
        ).also {
            postNotification(
                accessTokenFileInputStream = accessTokenFileInputStream,
                notification = it
            )
        }
    }

    /**
     * Post notification to the server so that it is sent to the users
     */
    private fun postNotification(
        accessTokenFileInputStream: InputStream,
        notification: FcmNotification
    ) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitInstance.api.postNotification(
                        accessToken = "Bearer ${getAccessToken(accessTokenFileInputStream)}",
                        notification = notification
                    )
                    if (response.isSuccessful) {
                        Log.e(TAG, " $response Notification has been sent successfully")
                        _event.postValue(UiEvent.NotificationSentSuccessfully)
                    } else {
                        _event.postValue(UiEvent.FailedSendingNotification)
                        Log.e(TAG, response.errorBody()?.string()!!)
                    }
                } catch (e: Exception) {
                    _event.postValue(UiEvent.FailedSendingNotification)
                    Log.e(TAG, e.toString())
                }
            }
        }

    /**
     * This method will obtain the token and then refresh it, meaning next time you will get the refreshed one
     */
    private fun getAccessToken(inputStream: InputStream): String? {
        try {
            val googleCredentials: GoogleCredentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            googleCredentials.refresh()
            return googleCredentials.accessToken.tokenValue
        } catch (e: Exception) {
            // Handle exceptions (e.g., IOException, FirebaseAuthException)
            throw IllegalStateException("Failed to refresh access token", e)
        }
    }

    sealed class UiEvent {
        object NotificationSentSuccessfully : UiEvent()

        object FailedSendingNotification : UiEvent()
    }
}
