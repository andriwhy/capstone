const userService = require('../services/userService');

exports.createUser = async (request, h) => {
    const { name, email, password } = request.payload;

    try {
        const userId = await userService.createUser({ name, email, password });
        return h.response({ userId }).code(201);
    } catch (error) {
        console.error(error);
        return h.response({ error: 'Failed to create user' }).code(500);
    }
};

exports.getUser = async (request, h) => {
    const { id } = request.params;

    try {
        const user = await userService.getUserById(id);
        if (!user) {
            return h.response({ error: 'User not found' }).code(404);
        }
        return h.response(user).code(200);
    } catch (error) {
        console.error(error);
        return h.response({ error: 'Failed to fetch user' }).code(500);
    }
};

exports.updateUser = async (request, h) => {
    const { id } = request.params;
    const { name, email, password } = request.payload;

    try {
        const updated = await userService.updateUser(id, { name, email, password });
        if (!updated) {
            return h.response({ error: 'User not found or update failed' }).code(404);
        }
        return h.response({ message: 'User updated successfully' }).code(200);
    } catch (error) {
        console.error(error);
        return h.response({ error: 'Failed to update user' }).code(500);
    }
};

exports.deleteUser = async (request, h) => {
    const { id } = request.params;

    try {
        const deleted = await userService.deleteUser(id);
        if (!deleted) {
            return h.response({ error: 'User not found or delete failed' }).code(404);
        }
        return h.response({ message: 'User deleted successfully' }).code(200);
    } catch (error) {
        console.error(error);
        return h.response({ error: 'Failed to delete user' }).code(500);
    }
};
