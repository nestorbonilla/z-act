const mongoose = require('mongoose');

const ActSchema = mongoose.Schema({
    title: {
        type: String,
        required: true
    },
    fromAddress: {
        type: String,
        required: true
    },
    publicInfo: {
        type: String,
        required: true
    },
    preSeed: {
        type: String,
        required: true
    },
    meetingPointRadius: {
        type: Number,
        default: 50
    },
    meetingPoint: String,
    date: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Acts', ActSchema);