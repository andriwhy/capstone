from flask import Flask, jsonify, request
from routes import diary_routes
import jwt  # Install library ini dengan `pip install pyjwt`
from functools import wraps
from diary_service import configure_genai_api

app = Flask(__name__)
configure_genai_api()
# Konfigurasi secret key untuk JWT
JWT_SECRET = "aifell2024*"  # Ganti dengan secret key yang digunakan oleh Node.js
JWT_ALGORITHM = "HS256"  # Algoritma yang digunakan untuk menandatangani JWT

# Middleware untuk validasi token JWT
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get("Authorization")
        if not token:
            return jsonify({"error": "Token tidak ditemukan"}), 401
        
        try:
            # Hapus "Bearer " jika ada
            token = token.split(" ")[1] if token.startswith("Bearer ") else token
            decoded = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
            request.user = decoded  # Lampirkan payload yang telah didecode ke request
        except jwt.ExpiredSignatureError:
            return jsonify({"error": "Token sudah kadaluarsa"}), 401
        except jwt.InvalidTokenError:
            return jsonify({"error": "Token tidak valid"}), 401

        return f(*args, **kwargs)
    return decorated

# Terapkan middleware untuk semua route di `diary_routes`
@app.before_request
def before_request():
    # Pastikan hanya endpoint diary yang memerlukan autentikasi
    if request.path.startswith("/diaries"):
        token_required(lambda: None)()

# Register routes
app.register_blueprint(diary_routes)

# Run the server
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=3000)
