import pytest
from fastapi.testclient import TestClient
from main import app, create_access_token
from typing import Dict
import time

client = TestClient(app)

def test_auth_token_success():
    response = client.post(
        "/api/v1/auth/token",
        json={"client_id": "dytec_android", "client_secret": "dummy_secret"}
    )
    assert response.status_code == 200
    data = response.json()
    assert "access_token" in data
    assert data["token_type"] == "bearer"

def test_auth_token_failure():
    response = client.post(
        "/api/v1/auth/token",
        json={"client_id": "wrong", "client_secret": "wrong"}
    )
    assert response.status_code == 401

def test_predict_unauthorized():
    payload = {
        "age": 8.5,
        "std_dot_enum": 85.0,
        "std_stroop": 90.0,
        "std_addition": 70.0,
        "std_multiplication": 100.0,
        "has_mult": 1
    }
    # No header provided
    response = client.post("/api/v1/predict", json=payload)
    assert response.status_code == 401
    
    # Invalid token provided
    response = client.post("/api/v1/predict", json=payload, headers={"Authorization": "Bearer INVALID"})
    assert response.status_code == 401

def test_predict_invalid_payload():
    token = create_access_token({"sub": "dytec_android"})
    headers = {"Authorization": f"Bearer {token}"}
    
    # Missing fields
    response = client.post("/api/v1/predict", json={"age": 8.5}, headers=headers)
    assert response.status_code == 422
    
    # Wrong data type
    payload = {
        "age": "delapan", # Should be float
        "std_dot_enum": 85.0,
        "std_stroop": 90.0,
        "std_addition": 70.0,
        "std_multiplication": 100.0,
        "has_mult": 1
    }
    response = client.post("/api/v1/predict", json=payload, headers=headers)
    assert response.status_code == 422

def test_predict_success():
    token = create_access_token({"sub": "dytec_android"})
    headers = {"Authorization": f"Bearer {token}"}
    
    payload = {
        "age": 8.5,
        "std_dot_enum": 85.0,
        "std_stroop": 90.0,
        "std_addition": 70.0,
        "std_multiplication": 100.0,
        "has_mult": 1
    }
    with TestClient(app) as client:
        response = client.post("/api/v1/predict", json=payload, headers=headers)
        assert response.status_code == 200
        data = response.json()
        assert "prediction_label" in data
        assert "probabilities" in data
        assert "typical" in data["probabilities"]
        assert "low_achievement" in data["probabilities"]
        assert "dyscalculia" in data["probabilities"]
        assert "timestamp" in data
