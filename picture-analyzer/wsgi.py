import json
from flask import Flask, jsonify, request
from prediction import predict
import os

workers = int(os.environ.get('GUNICORN_PROCESSES', '1'))
threads = int(os.environ.get('GUNICORN_THREADS', '2'))
timeout = int(os.environ.get('GUNICORN_TIMEOUT', '120'))
bind = os.environ.get('GUNICORN_BIND', '0.0.0.0:8080')
forwarded_allow_ips = '*'
secure_scheme_headers = { 'X-Forwarded-Proto': 'https' }

print("starting...")

application = Flask(__name__)


@application.route('/')
@application.route('/status')
def status():
    return jsonify({'status': 'ok'})


@application.route('/predictions', methods=['POST'])
def create_prediction():
    data = request.data or '{}'
    print(data)
    body = json.loads(data)
    return jsonify(predict(body))
