from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from pydantic import BaseModel
import jwt
from datetime import datetime, timedelta, timezone
import joblib
import pandas as pd
from typing import Dict

# Configuration
SECRET_KEY = "super-secret-dytec-key"  # In production, use env variable
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60

app = FastAPI(title="Dytec Dyscalculia Model API")
security = HTTPBearer()

# Load model on startup
model_data = None

@app.on_event("startup")
def load_model():
    global model_data
    try:
        model_data = joblib.load("model.joblib")
        print("Model loaded successfully.")
    except Exception as e:
        print(f"Error loading model: {e}")
        # We don't crash here so tests that don't need the model can still run,
        # but in a real app you might want to raise an exception.

# --- Auth ---
class TokenRequest(BaseModel):
    client_id: str
    client_secret: str

def create_access_token(data: dict, expires_delta: timedelta = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def verify_token(credentials: HTTPAuthorizationCredentials = Depends(security)):
    token = credentials.credentials
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token has expired",
            headers={"WWW-Authenticate": "Bearer"},
        )
    except jwt.InvalidTokenError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token",
            headers={"WWW-Authenticate": "Bearer"},
        )

@app.post("/api/v1/auth/token")
async def get_token(request: TokenRequest):
    # Dummy authentication logic for demonstration
    if request.client_id == "dytec_android" and request.client_secret == "dummy_secret":
        access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
        access_token = create_access_token(
            data={"sub": request.client_id}, expires_delta=access_token_expires
        )
        return {"access_token": access_token, "token_type": "bearer", "expires_in": ACCESS_TOKEN_EXPIRE_MINUTES * 60}
    raise HTTPException(status_code=401, detail="Invalid client credentials")


# --- Predict ---
class PredictionRequest(BaseModel):
    age: float
    std_dot_enum: float
    std_stroop: float
    std_addition: float
    std_multiplication: float
    has_mult: int

@app.post("/api/v1/predict")
async def predict(request: PredictionRequest, token_payload: dict = Depends(verify_token)):
    if model_data is None:
        raise HTTPException(status_code=500, detail="Model not loaded on server.")
    
    try:
        model = model_data["model"]
        features = model_data["features"]
        labels = model_data["labels"]
        
        # Prepare input data as a DataFrame to ensure column order matches training
        input_data = pd.DataFrame([{
            "age": request.age,
            "std_dot_enum": request.std_dot_enum,
            "std_stroop": request.std_stroop,
            "std_addition": request.std_addition,
            "std_multiplication": request.std_multiplication,
            "has_mult": request.has_mult
        }])[features] # Enforce order
        
        # Predict
        prediction_idx = model.predict(input_data)[0]
        prediction_label = prediction_idx # The labels in train_model.py are strings directly
        
        probabilities_array = model.predict_proba(input_data)[0]
        
        # Map probabilities to classes
        prob_dict = {}
        for i, class_name in enumerate(model.classes_):
            prob_dict[class_name] = float(probabilities_array[i])
            
        return {
            "prediction_label": prediction_label,
            "probabilities": prob_dict,
            "timestamp": datetime.now(timezone.utc).isoformat()
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction error: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
