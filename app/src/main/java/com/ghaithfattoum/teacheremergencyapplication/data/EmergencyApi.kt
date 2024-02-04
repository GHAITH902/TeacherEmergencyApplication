package com.ghaithfattoum.teacheremergencyapplication.data
import com.ghaithfattoum.teacheremergencyapplication.Constants.CONTENT_TYPE
import com.ghaithfattoum.teacheremergencyapplication.domain.model.FcmNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface EmergencyApi {

    // https://stackoverflow.com/questions/76318682/request-had-invalid-authentication-credentials-when-using-curl-to-call-firebas
    @Headers("Content-Type:$CONTENT_TYPE")
    @POST("projects/emergency-application--teacher/messages:send")
    suspend fun postNotification(
        @Header("Authorization") accessToken: String,
        @Body notification: FcmNotification
    ): Response<ResponseBody>
}
