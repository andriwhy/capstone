from flask import request, jsonify
from diary_service import create_diary_with_prediction, update_diary_with_prediction, generate_activity_recommendation, get_diaries_by_user, delete_diary
import tensorflow as tf
import numpy as np
import jwt

# JWT Configuration
JWT_SECRET = "aifell2024*"  # Ganti dengan secret key Anda
JWT_ALGORITHM = "HS256"

# Load the machine learning model
MODEL_PATH = 'model_lstm_new_2.h5'
VOCAB_PATH = 'indonesian_vocab.txt'
MAX_SEQ_LENGTH = 20

print("Loading ML model...")
ml_model = tf.keras.models.load_model(MODEL_PATH)
print("ML model loaded successfully.")

# Load vocabulary
def load_vocabulary(vocab_path):
    with open(vocab_path, 'r', encoding='utf-8') as f:
        vocab = f.read().splitlines()
    return {word: idx for idx, word in enumerate(vocab)}

vocab = load_vocabulary(VOCAB_PATH)

# Tokenize and preprocess text
def preprocess_text(text, max_seq_length, vocab, unknown_token="[UNK]"):
    tokens = [
        vocab.get(word, vocab.get(unknown_token)) for word in text.lower().split()
    ]
    padded_tokens = [0] * (max_seq_length - len(tokens)) + tokens[:max_seq_length]
    return np.array([padded_tokens])

def get_diaries_handler():
    try:
        user_id = request.args.get("userId")  # Ambil userId dari query parameter
        if not user_id:
            return jsonify({"error": "User ID is required"}), 400

        diaries = get_diaries_by_user(user_id)
        if not diaries:
            return jsonify({"message": "No diaries found for the user"}), 404

        return jsonify({"userId": user_id, "diaries": diaries}), 200
    except Exception as e:
        return jsonify({"error": f"Failed to fetch diaries: {str(e)}"}), 500

# Create a diary with ML prediction and activity recommendation
def create_diary_handler():
    try:
        # Ambil token dari header Authorization
        token = request.headers.get("Authorization")
        if not token:
            return jsonify({"error": "Authorization token is required"}), 401

        # Hapus "Bearer " dari token jika ada
        token = token.split(" ")[1] if token.startswith("Bearer ") else token

        # Decode token untuk mendapatkan userId
        decoded = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        user_id = decoded.get("userId")
        if not user_id:
            return jsonify({"error": "User ID not found in token"}), 400

        # Ambil data dari body request
        data = request.json
        content = data.get("content", "")
        date = data.get("date", "")

        if not content or not date:
            return jsonify({"error": "Content and date are required"}), 400

        # Preprocess content for prediction
        preprocessed_input = preprocess_text(content, MAX_SEQ_LENGTH, vocab)

        # Predict the emotion
        predictions = ml_model.predict(preprocessed_input)
        predicted_class = int(np.argmax(predictions))  # Get the predicted class

        # Map predicted class to emotion
        emotion_mapping = {
            0: "sedih",
            1: "senang",
            2: "love",
            3: "marah",
            4: "takut",
            5: "terkejut"
        }
        predicted_emotion = emotion_mapping.get(predicted_class, "Unknown")

        # Generate mood category
        mood_category = "Good Mood" if predicted_emotion in ["senang", "love"] else "Bad Mood"

        # Generate activity recommendation
        activity_recommendation = generate_activity_recommendation(predicted_emotion)

        # Save the diary with the predicted class and recommendation
        diary_id = create_diary_with_prediction({
            "userId": user_id,
            "date": date,
            "content": content
        }, predicted_emotion, mood_category)

        return jsonify({
            "diaryId": diary_id,
            "predictedEmotion": predicted_emotion,
            "moodCategory": mood_category,
            "activityRecommendation": activity_recommendation
        }), 201

    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Token has expired"}), 401
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid token"}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500
# Update a diary with ML prediction and activity recommendation
def update_diary_handler(id):
    try:
        data = request.json
        content = data.get("content", "")

        if not content:
            return jsonify({"error": "Content is required"}), 400

        # Preprocess content for prediction
        preprocessed_input = preprocess_text(content, MAX_SEQ_LENGTH, vocab)

        # Predict the emotion
        predictions = ml_model.predict(preprocessed_input)
        predicted_class = int(np.argmax(predictions))  # Get the predicted class

        # Map predicted class to emotion
        emotion_mapping = {
            0: "sedih",
            1: "senang",
            2: "love",
            3: "marah",
            4: "takut",
            5: "terkejut"
        }
        predicted_emotion = emotion_mapping.get(predicted_class, "Unknown")

        # Generate mood category
        mood_category = "Good Mood" if predicted_emotion in ["senang", "love"] else "Bad Mood"

        # Generate activity recommendation
        activity_recommendation = generate_activity_recommendation(predicted_emotion)

        # Update the diary with the predicted class and recommendation
        updated = update_diary_with_prediction(id, data, predicted_emotion, mood_category)

        if not updated:
            return jsonify({"error": "Diary not found or update failed"}), 404

        return jsonify({
            "message": "Diary updated successfully",
            "predictedEmotion": predicted_emotion,
            "moodCategory": mood_category,
            "activityRecommendation": activity_recommendation
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

def delete_diary_handler(id):
    try:
        return delete_diary(id)
    except Exception as e:
        return jsonify({"error": f"Failed to delete diary: {str(e)}"}), 500