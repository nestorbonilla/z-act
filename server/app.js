const express = require('express');
const app = express();
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
require('dotenv/config');

//MIDDLEWARES
app.use(cors());
app.use(bodyParser.json());

//IMPORT ROUTES
const actsRoute = require('./routes/acts');
app.use('/acts', actsRoute);

//ROUTES
app.get('/', (req, res) => {
    res.send('Welcome to ZAct App');
});


//CONNECT TO DB
mongoose.connect(
    process.env.DB_CONNECTION,
    {
        useNewUrlParser: true,
        useUnifiedTopology: true
    },
    () => console.log('connected to zact database...'));

app.listen(3000);