package com.fein.dytec.data.remote

import com.fein.dytec.data.remote.models.PredictionRequest
import com.fein.dytec.data.remote.models.PredictionResponse
import com.fein.dytec.data.remote.models.TokenRequest
import com.fein.dytec.data.remote.models.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DiskalkuliaApi {
    @POST("/api/v1/auth/token")
    suspend fun getToken(@Body request: TokenRequest): Response<TokenResponse>

    @POST("/api/v1/predict")
    suspend fun predict(@Body request: PredictionRequest): Response<PredictionResponse>
}
