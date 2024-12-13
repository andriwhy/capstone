const recapHandler = require('../handlers/recapHandlers');
const Joi = require('joi');  // or 'joi', make sure you're using the correct version

module.exports = [
    {
        method: 'GET',
        path: '/recap/{userId}',
        handler: recapHandler.getUserWeeklyRecap, 
        options: {
            auth: false,  // Adjust as necessary
            validate: {
                params: Joi.object({
                    userId: Joi.string().required() // Validate userId as a required string
                }),
                headers: Joi.object({
                    authorization: Joi.string().required(), // Validate the authorization header
                }).unknown(true)  // Allow unknown headers if necessary
            }
        },
    },
];
