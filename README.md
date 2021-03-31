# drapak-reddit

The purpose of this app is to scrape data from Reddit using Reddit API. Obtained data is transformed to model objects and these are stored in the database. The final goal of the app is to create an efficient service with regularly updated dataset that will be offering pojos in JSON representing reddit related objects via HTTP. Different passable parameters will make the queries flexible. The potential clients using this service will be able to use it for analyzing posts and comments related data and detect trends either in financial or any other subreddits. Making it functional for financial subreddits is a goal but it will be easily modifiable. The project is developed using Java 11 + Maven. Keeping it pretty low level using pure servlets and no frameworks for persistence layer/dependency injection is done for training purposes. 

In order to make it work <b>config.properties</b> file needs to be updated with the path to <b>reddit.properties</b>- external file with id and key for the registered reddit app. Necessary values:  
<b>secretKey</b>= your secret key here  
<b>scriptId</b>= ID of your script here  

dbpath property needs correct path to the <b>redditdb.properties</b> file specifying:  
<b/>connectionString</b>= jdbc connection string  
<b>username</b>= username of db user  
<b>password</b>= password of db user  
To create compatible tables use <b>sql scripts</b> located under <b>resources/sql</b>  
  
Another configurable properties:  
<b>userAgent</b> name should be changed to the name of your bot, could be registered using the link at the bottom.   
<b>subreddits</b> list can be freely edited  

  
Register an app here: https://ssl.reddit.com/prefs/apps/

Read API rules before using: https://github.com/reddit-archive/reddit/wiki/API
