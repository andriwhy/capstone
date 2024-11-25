const firestore = require('../config/firestore');

const USERS_COLLECTION = 'users';

exports.createUser = async ({ name, email, password }) => {
    const userRef = firestore.collection(USERS_COLLECTION).doc();
    const userData = {
        name,
        email,
        password,
        createdAt: new Date(),
    };
    await userRef.set(userData);
    return userRef.id;
};

exports.getUserById = async (id) => {
    const userDoc = await firestore.collection(USERS_COLLECTION).doc(id).get();
    return userDoc.exists ? userDoc.data() : null;
};

exports.updateUser = async (id, updatedData) => {
    const userRef = firestore.collection(USERS_COLLECTION).doc(id);
    const userDoc = await userRef.get();
    if (!userDoc.exists) {
        return false;
    }
    await userRef.update(updatedData);
    return true;
};

exports.deleteUser = async (id) => {
    const userRef = firestore.collection(USERS_COLLECTION).doc(id);
    const userDoc = await userRef.get();
    if (!userDoc.exists) {
        return false;
    }
    await userRef.delete();
    return true;
};
