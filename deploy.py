from google.cloud import storage

# Path ke file credentials.json Anda
CREDENTIALS_PATH = "credentials.json"

# Nama file lokal yang akan diunggah
local_file_name = "model_lstm_new_3.h5"

# Nama bucket Anda
bucket_name = "aifeel-model"

# Nama tujuan file di bucket
destination_blob_name = "models/model_lstm_new_3.h5"

def upload_to_gcs(credentials_path, local_file_name, bucket_name, destination_blob_name):
    """
    Mengunggah file ke Google Cloud Storage.
    """
    try:
        # Inisialisasi klien Google Cloud Storage
        storage_client = storage.Client.from_service_account_json(credentials_path)
        bucket = storage_client.bucket(bucket_name)
        blob = bucket.blob(destination_blob_name)

        # Unggah file
        blob.upload_from_filename(local_file_name)
        print(f"File {local_file_name} berhasil diunggah ke bucket {bucket_name} dengan nama {destination_blob_name}.")
    except Exception as e:
        print(f"Error saat mengunggah file: {e}")

# Jalankan fungsi upload
upload_to_gcs(CREDENTIALS_PATH, local_file_name, bucket_name, destination_blob_name)
