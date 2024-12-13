const jwt = require('@hapi/jwt'); 
const { getWeeklyMoodData, saveWeeklyMoodPrediction } = require('../services/recapServices');

exports.getUserWeeklyRecap = async (request, h) => {
    const token = request.headers.authorization?.split(' ')[1];
    const userId = request.params.userId;

    if (!token) {
        return h.response({ error: 'Authentication token is required' }).code(401);
    }

    try {
        const decodedToken = jwt.token.decode(token);
        const payload = decodedToken.decoded.payload;

        console.log('Decoded Token Payload:', payload);
        console.log('UserId from Params:', userId);

        if (payload.userId !== userId) {
            return h.response({ error: 'Unauthorized access' }).code(403);
        }

        const today = new Date();
        const weekEndDate = today.toISOString().split('T')[0]; // Format: YYYY-MM-DD
        const weekStartDate = new Date(today.setDate(today.getDate() - 6)).toISOString().split('T')[0];

        console.log('Calculated Week Range:', { weekStartDate, weekEndDate });

        const moodData = await getWeeklyMoodData(userId, weekStartDate, weekEndDate);

        if (moodData.length === 0) {
            console.log('No mood data found for this week.');
            return h.response({ message: 'No mood data found for this week.' }).code(404);
        }

        const moodCategoryCounts = {
            "Good Mood": 0,
            "Bad Mood": 0,
        };

        moodData.forEach((entry) => {
            console.log(`Processing entry: ${JSON.stringify(entry)}`);
            if (entry.moodCategory === "Good Mood") {
                moodCategoryCounts["Good Mood"]++;
            } else if (entry.moodCategory === "Bad Mood") {
                moodCategoryCounts["Bad Mood"]++;
            }
        });

        const dominantMoodCategory = moodCategoryCounts["Good Mood"] > moodCategoryCounts["Bad Mood"]
            ? "Good Mood"
            : "Bad Mood";

        console.log('Calculated Mood Counts:', moodCategoryCounts);
        console.log('Dominant Mood Category:', dominantMoodCategory);

        await saveWeeklyMoodPrediction(userId, weekStartDate, weekEndDate, dominantMoodCategory);

        return h.response({
            message: 'Weekly recap calculated successfully',
            recap: {
                weekStartDate,
                weekEndDate,
                dominantMoodCategory,
                moodCounts: moodCategoryCounts,
            },
        }).code(200);
    } catch (error) {
        console.error('Error in getUserWeeklyRecap handler:', error);
        return h.response({ error: 'Failed to calculate weekly recap' }).code(500);
    }
};
