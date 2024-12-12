from google.cloud import firestore

# Path to your credentials.json file
CREDENTIALS_PATH = "credentials.json"

# Initialize Firestore using the credentials.json file
db = firestore.Client.from_service_account_json(CREDENTIALS_PATH)
