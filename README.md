# drapak-reddit

The purpose of this app is to scrape data from Reddit using Reddit API. Data is transferred to Objects that will be stored and analyzed. The final goal of the app is to help with detecting trends either in financial subreddits or any other reddit. Making it working for financial subreddits will be implemented firstly as it is relatively easier due to logic being based on tickers. The project is developed using Java 11 + Maven. 

In order to make it work config.properties file needs to be updated with the path to reddit.properties- external file with id and key for the registered reddit app. Necessery values:
secretKey=<<your secret key here>>
scriptId=<<ID of your script here>>
  
Both can be obtained by registering an app here: https://ssl.reddit.com/prefs/apps/
Read API rules before using: https://github.com/reddit-archive/reddit/wiki/API
