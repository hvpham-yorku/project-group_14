# Rateflix

## Motivation
##### Welcome to Rateflix. A serverless window application.
##### The purpose of this web app is to play the role as a social media rating app for movies, shows, serials, etc. Users are able to create posts of recent movies or work they've seen and write a quick review and rate it. Other users are able to see these posts to help them decide whether they should watch it or not. We are using Java's Swing library for UI components and MySQL as the database to store user info, reviews, movie details, etc.
##### Some of the problems this application solves is helping users save time deciding what movie to watch by showing them a quick rundown of each title all in one place.
##### Some general purposes and objectives of the project are outlined in the ``` summary.md ``` file.

## Installation
##### cd into the Rateflix directory and create a 'config.properties' file under Database package. Populate the file with the following:
    db.url=jdbc:mysql://localhost:3306/rateflix
    db.username=[yourUsername (typically 'root')]
    db.password=[yourPassword]
##### This should set you up with the database.
##### further installations of tools and libraries will be added onto this ``` README.md ``` file once they are implemented.

## Contribution
##### When creating a new branch, name it using your name using the format "firstName_LastName" to make it easier to identify.
##### We will be using GitHub issues for task distribution and issue handling. This will be easier given the code organization software used for this is on GitHub.
##### Pull requests will be enabled to main contributors ONLY initially, with future expansions later in the future.

## Key Features
##### This app contains many key features that are followed closely to the listed user stories.
##### Some of the features include an authentication system, user-unique watchlists with movies of their choices, fetching contents from a third-party API to display movie thumbnails, reviewing and rating features for movies, using the average review per movie to represent the score for the given movie, etc.
##### To add a sense of personalization, users are also allowed to change their usernames, add personal biographys/descriptions as they would on other social media platforms, and also upload a profile picture of themselves.
