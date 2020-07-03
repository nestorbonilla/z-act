const express = require('express');
const router = express.Router();
const Act = require('../models/Act');

router.get('/', async (req, res) => {
    try {
        const acts = await Act.find();
        res.json(acts);
    } catch (err) {
        res.json(err);
    }
    res.send('yeap in acts');
});

router.post('/', async (req, res) => {
    const act = new Act({
        title: req.body.title,
        fromAddress: req.body.fromAddress,
        publicInfo: req.body.publicInfo,
        preSeed: req.body.preSeed
    });
    try {
        const savedAct = await act.save();
        res.json(savedAct);    
    } catch (err) {
        res.json(err);
    }
});

router.get('/:actId', async (req, res) => {
    try {
        const act = await Act.findById(req.params.actId);
        res.json(act);   
    } catch (err) {
        res.json(err);
    }
});

router.delete('/:actId', async (req, res) => {
    try {
        const act = await Act.remove({ _id: req.params.actId });
        res.json(act);   
    } catch (err) {
        res.json(err);
    }
});

router.patch('/:actId', async (req, res) => {
    try {
        const act = await Act.updateOne(
            { 
                _id: req.params.actId
            },
            {
                $set: {
                    title: req.body.title,
                    fromAddress: req.body.fromAddress,
                    publicInfo: req.body.publicInfo,
                    preSeed: req.body.preSeed,
                    meetingPointRadius: req.body.meetingPointRadius,
                    meetingPoint: req.body.meetingPoint
                }
            });
        res.json(act);   
    } catch (err) {
        res.json(err);
    }
});

module.exports = router;