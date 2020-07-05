const mongoose = require('mongoose');

const ActSchema = mongoose.Schema({
    fromAddress: {
        type: String,
        required: true
    },
    actAddress: {
        type: String,
        default: ""
    },
    seed: {
        type: String,
        required: true
    },
    title: {
        type: String,
        required: true
    },
    publicInformation: {
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