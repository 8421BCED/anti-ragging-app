from flask import Flask, request, jsonify
from flask_cors import CORS
import os

app = Flask(__name__)
CORS(app)

# Hardcoded credentials as requested
ADMIN_PASS = "joel@admin"

@app.route('/api/login', methods=['POST'])
def login():
    data = request.json
    password = data.get('password')
    
    if password == ADMIN_PASS:
        return jsonify({"success": True, "token": "fake-jwt-token"}), 200
    else:
        return jsonify({"success": False, "message": "Invalid password"}), 401

if __name__ == '__main__':
    app.run(debug=True, port=5000)
