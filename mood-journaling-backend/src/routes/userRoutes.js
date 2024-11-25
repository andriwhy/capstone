const userHandler = require('../handlers/userHandler');

module.exports = [
    {
        method: 'POST',
        path: '/users',
        handler: userHandler.createUser,
    },
    {
        method: 'GET',
        path: '/users/{id}',
        handler: userHandler.getUser,
    },
    {
        method: 'PUT',
        path: '/users/{id}',
        handler: userHandler.updateUser,
    },
    {
        method: 'DELETE',
        path: '/users/{id}',
        handler: userHandler.deleteUser,
    },
];
