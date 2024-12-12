from flask import Blueprint
from diary_handlers import create_diary_handler, update_diary_handler, get_diaries_handler, delete_diary_handler

# Create a Flask Blueprint for diary routes
diary_routes = Blueprint('diary_routes', __name__)

# Define routes
@diary_routes.route('/diaries', methods=['POST'])
def create_diary():
    return create_diary_handler()

@diary_routes.route('/diaries/<id>', methods=['PUT'])
def update_diary(id):
    return update_diary_handler(id)

@diary_routes.route('/diaries', methods=['GET'])
def get_diaries():
    return get_diaries_handler()

@diary_routes.route('/diaries/<id>', methods=['DELETE'])
def delete_diary(id):
    return delete_diary_handler(id)
