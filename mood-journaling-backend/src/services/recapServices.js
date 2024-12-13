const firestore = require('../config/firestore');

// Mengambil data mood mingguan dari koleksi `diaries`
const getWeeklyMoodData = async (userId, weekStartDate, weekEndDate) => {
    try {
        const diariesRef = firestore.collection('diaries');
        console.log(`Fetching mood data for userId: ${userId}, weekStartDate: ${weekStartDate}, weekEndDate: ${weekEndDate}`);

        const snapshot = await diariesRef
            .where('userId', '==', userId)
            .where('date', '>=', weekStartDate)
            .where('date', '<=', weekEndDate)
            .get();

        console.log(`Found ${snapshot.size} entries for the query.`);

        if (snapshot.empty) {
            return [];
        }

        const moodData = [];
        snapshot.forEach(doc => {
            console.log(`Document data: ${JSON.stringify(doc.data())}`);
            moodData.push(doc.data());
        });

        return moodData;
    } catch (error) {
        console.error('Error fetching weekly mood data:', error);
        throw new Error('Failed to fetch weekly mood data.');
    }
};

// Menyimpan hasil recap mingguan ke koleksi `weeklyRecaps`
const saveWeeklyMoodPrediction = async (userId, weekStartDate, weekEndDate, dominantMoodCategory) => {
    try {
        const recapRef = firestore.collection('weeklyRecaps');
        console.log(`Saving weekly mood recap for userId: ${userId}, dominantMoodCategory: ${dominantMoodCategory}`);
        await recapRef.add({
            userId,
            weekStartDate,
            weekEndDate,
            dominantMoodCategory,
            createdAt: new Date().toISOString(),
        });
    } catch (error) {
        console.error('Error saving weekly mood prediction:', error);
        throw new Error('Failed to save weekly mood prediction.');
    }
};

module.exports = {
    getWeeklyMoodData,
    saveWeeklyMoodPrediction,
};
