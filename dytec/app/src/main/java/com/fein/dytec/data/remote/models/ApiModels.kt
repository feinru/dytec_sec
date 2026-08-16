package com.fein.dytec.data.remote.models

data class TokenRequest(
    val client_id: String,
    val client_secret: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class PredictionRequest(
    val age: Double,
    val std_dot_enum: Double,
    val std_stroop: Double,
    val std_addition: Double,
    val std_multiplication: Double,
    val has_mult: Int
)

data class PredictionResponse(
    val prediction_label: String,
    val probabilities: Map<String, Double>,
    val timestamp: String
)
