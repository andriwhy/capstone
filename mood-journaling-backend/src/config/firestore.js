const { Firestore } = require('@google-cloud/firestore');

const firestore = new Firestore({
    projectId: '[project-id]',
    keyFilename: '[key]', 
});

module.exports = firestore;
