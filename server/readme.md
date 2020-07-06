# ZAct Server

This is a NodeJS Express server that manages the acts created from the app by the creator. The information published on the server is public and doesn't reveal by any means the final data of the act.

## Requirements

The libraries we require for this server application are:

* [express](https://expressjs.com/), a minimal and flexible Node.js web application framework that provides a robust set of features for web and mobile applications.
* [mongoose](https://mongoosejs.com/), a straight-forward, schema-based solution to model your application data.
* [dotenv](https://github.com/motdotla/dotenv), a zero-dependency module that loads environment variables.

## Database

For this project, we use MongoDB as a database. If you don't have a database on your server, please sign up and create one on the [MongoDB platform](https://www.mongodb.com/).

Once you have MongoDB database, create a .env file using .env.example, and put your connection string in the DB_CONNECTION variable.

## Publish

For this project we use [Heroku](https://www.heroku.com/) to host our NodeJS server. Once you have it [installed](https://devcenter.heroku.com/articles/heroku-cli) on your local computer, login with your credentials:

```
$ heroku login -i
heroku: Enter your login credentials
Email: me@example.com
Password: ***************
Two-factor code: ********
Logged in as me@heroku.com
```

Then create an app, and deploy the code on it:
```
heroku create
git add .
git commit -m "first commit"
git push heroku master
```

And ready; you will now have an URL for your new ZAct API!

If you want a full explanation of the project, please visit:

* [Documentation](https://nestorbonilla.gitbook.io/zact/)