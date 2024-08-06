# Notification Service
### _Easily Send and Monitor Messages to your Customers_

## Features

- Send and manage messages
- Enable to automatically prevent sending to blacklisted numbers
- Easily search phrase across all messages and retreive messages in a range
- Ablity to track message status as well as automatic retrial
- Track all the messages, users and their details

This is the basic overview of service :-

![General Flow](main.png "General FLow" width="600" height="300")

## Tech

The following services uses the following technologies :- 

- SpringBoot - Creating Backend over Java
- Redis - Enabling Caching
- MySQL - Database
- Elastic Search - Greatly redusing searching time especially for range queries and text search.
- Apache kafka - Unable queueing and handling of events

## Installation
In order to use the application download elastic-search and kafka and unzip the folder and save as `elasticsearch` and `kafka` respectively.

You also need to install following for redis:- 
```sh
brew install redis
```

```sh
bash start_service.sh
```

Followed by this simply run the main Java application and send in the request as needed.
