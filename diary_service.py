from google.cloud import firestore
import google.generativeai as genai
from datetime import datetime
from flask import jsonify

# Path to your credentials.json file
CREDENTIALS_PATH = "credentials.json"

# Initialize Firestore using the credentials.json file
db = firestore.Client.from_service_account_json(CREDENTIALS_PATH)

# Firestore Collection Name
DIARIES_COLLECTION = 'diaries'

# Configure Gemini API
def configure_genai_api():
    genai.configure(api_key="AIzaSyAO16cRX-IT0HmaUHcZrm4MXMkF4m1xdeY")  # Replace with your actual API key

# Generate activity recommendation
def get_diaries_by_user(user_id):
    """Fetch all diaries for a specific user from Firestore."""
    diaries_ref = db.collection(DIARIES_COLLECTION)
    query = diaries_ref.where("userId", "==", user_id).stream()

    diaries = []
    for diary in query:
        diary_data = diary.to_dict()
        diary_data["id"] = diary.id  # Tambahkan ID dokumen ke hasil
        diaries.append(diary_data)

    return diaries

def generate_activity_recommendation(predicted_emotion):
    """
    Generate activity recommendations based on the predicted emotion using Gemini API.
    """
    # Map emotion to activity recommendation prompt
    prompts = {
        "sedih": "Saya merasa sedih. Berikan rekomendasi aktivitas yang bisa membantu saya merasa lebih baik.",
        "senang": "Saya merasa senang. Berikan rekomendasi aktivitas yang bisa membuat hari saya lebih bermakna.",
        "love": "Saya merasa penuh cinta. Berikan rekomendasi aktivitas yang bisa mempererat hubungan dengan orang-orang terdekat.",
        "marah": "Saya merasa marah. Berikan rekomendasi aktivitas yang bisa membantu saya menenangkan diri.",
        "takut": "Saya merasa takut. Berikan rekomendasi aktivitas yang bisa membantu saya merasa aman.",
        "terkejut": "Saya merasa terkejut. Berikan rekomendasi aktivitas untuk menenangkan pikiran saya."
    }

    prompt = prompts.get(predicted_emotion, "Berikan rekomendasi aktivitas untuk meningkatkan suasana hati saya.")

    # Call Gemini API
    try:
        # Gunakan objek model secara eksplisit
        model = genai.GenerativeModel("gemini-1.5-flash")  # Gunakan model yang sama seperti di gemini-test.py
        response = model.generate_content(prompt)  # Panggil metode generate_content dari model
        return response.text  # Ambil hasil teks
    except Exception as e:
        print(f"Error generating activity recommendation: {e}")
        return "Tidak ada rekomendasi yang tersedia saat ini."

def create_diary_with_prediction(data, predicted_emotion, mood_category):
    """Creates a diary entry in Firestore with an ML-predicted class."""
    diary_ref = db.collection(DIARIES_COLLECTION).document()
    diary_data = {
        "userId": data.get("userId"),  # Tambahkan userId
        "date": data.get("date"),
        "content": data.get("content"),
        "predictedEmotion": predicted_emotion,
        "moodCategory": mood_category,
        "createdAt": firestore.SERVER_TIMESTAMP,
    }
    diary_ref.set(diary_data)
    return diary_ref.id

def update_diary_with_prediction(id, data, predicted_emotion, mood_category):
    """Updates an existing diary entry in Firestore."""
    diary_ref = db.collection(DIARIES_COLLECTION).document(id)
    diary = diary_ref.get()
    if not diary.exists:
        return False
    updated_data = {
        "date": data.get("date"),
        "content": data.get("content"),
        "predictedEmotion": predicted_emotion,
        "moodCategory": mood_category,
        "updatedAt": firestore.SERVER_TIMESTAMP,
    }
    diary_ref.update(updated_data)
    return True

def delete_diary(id):
    """Deletes a diary entry from Firestore."""
    diary_ref = db.collection(DIARIES_COLLECTION).document(id)
    diary = diary_ref.get()
    if not diary.exists:
        return jsonify({"error": "Diary not found"}), 404
    
    diary_ref.delete()
    return jsonify({"message": "Diary deleted successfully"}), 200