# Integrations

This subproject contains a basic integration of external tools:
- Slack integration (one-way) in [SlackClient](src/main/java/de/miltschek/openttdadmin/integration/SlackClient.java)
- Slack integration (two-way) in [SlackRTMClient](src/main/java/de/miltschek/openttdadmin/integration/SlackRTMClient.java)
- ip-api.com integration in [GeoIp](src/main/java/de/miltschek/openttdadmin/integration/GeoIp.java)
- Google Cloud Translator in [GoogleTranslate](src/main/java/de/miltschek/openttdadmin/integration/GoogleTranslate.java)

Building
--------

Either take a [pre-generated JAR file](https://github.com/miltschek/OpenTTDAdmin/releases/latest) or compile it by yourself. The project is Maven-enabled and this library is independent of other modules offered within OpenTTDAdmin workspace. To compile, make sure you use JDK 11 or newer and request the following build targets:
```
mvn clean compile package assembly:single install
```

- `package` will create an integrations-VERSION.jar file
- `assembly:single` will add dependencies to an integrations-VERSION-jar-with-dependencies.jar file
- `install` will register the library in your Maven repository so that other projects (including [Genowefa](../genowefa)) may make use of it

How-To Slack (One-Way)
------------------------
The one-way variant is supported by the simple [SlackClient](src/main/java/de/miltschek/openttdadmin/integration/SlackClient.java) class. You will be able to get chat messages out of the game to your Slack channel only. If you are looking for a two-way communication, jump to the next section.

1. Go to your [Slack Apps](https://api.slack.com/apps/)
2. Click 'Create New App' button, decide on the name and merge it with one of your workspaces.
3. Go to 'OAuth and Permissions' page in the 'Features' group.
4. Add an OAuth scope under 'Scopes' / 'Bot Token Scopes'
    - chat:write for chats to which the App will be invited
    - or chat:write.public for all public chats of your workspace
5. Scroll up and hit the button 'Install in Workspace', followed by allowing the access for the purpose.
6. Copy the value 'Bot User OAuth Access Token', usually staring with 'xoxb-...' - this is your **Slack Token**.
7. Go to your workspace (mobile app is more user-friendly than the web).
8. Create a new channel, you want your messages to be posted to (or take an existing one).
9. If you granted the App only the **chat:write** scope, you have to add the App to the created/chosen channel:
    - mobile: enter the channel, click the info icon (i), hit 'Apps', hit plus symbol (+), select the newly created App
    - web: really, no solution found if the App did not write to any of the existing channels already! if so, click on the App's name in any chat, hit 'Add this app to channel...', choose the channel, hit 'Add'
10. The [BasicTool](src/main/java/de/miltschek/openttdadmin/BasicTool.java) up to the version 1.1.2 inclusive does use this Slack Client. Just start it providing the channel's name or ID as **Slack Channel** and the newly created token as **Slack Token**. That's it!
11. Starting with the version 1.2.x, the [BasicTool](src/main/java/de/miltschek/openttdadmin/BasicTool.java) makes use of the RTM client described in the next section.

How-To Slack (Two-Way)
----------------------
The two-way variant is implemented as a new [SlackRTMClient](src/main/java/de/miltschek/openttdadmin/integration/SlackRTMClient.java) class. It provides the same push function from the game to the Slack channel plus a possibility to write back from Slack to the game. There are still a few issues explained below.

1. Go to your [Slack Apps](https://api.slack.com/apps/)
2. Click 'Create New App' button, decide on the name and merge it with one of your workspaces.
3. Go to the 'Socket Mode' page in the 'Settings' group.
    - enable the 'Socket Mode'.
    - it will automatically create an app-level token (connections:write scope)
    - name the token however you like to, it does not matter at all
    - copy the token (xapp-...) - this is your `SLACK_APP_TOKEN`.
4. Go to the 'OAuth & Permissions' page in the 'Features' group. In the 'Bot Token Scopes' section add the scopes:
    - `chat:write` to allow the bot to post messages to the channels it will be invited to
    - `reactions:write` to allow the bot to mark your messages as processed/failed
    - `channels:read` to allow the bot to get a list of channels and match the required ID
5. Go to the 'Event Subscriptions' page in the 'Features' group.
    - enable the 'Events'
    - in the 'Subscribe to bot events section' add:
      - `message.channels` to make the bot receive messages from the channels
6. Scroll up and hit the button 'Install to Workspace', followed by allowing the access for the purpose.
7. Copy the value 'Bot User OAuth Access Token' (xoxb-...) from the 'OAuth & Permissions' page - this is your `SLACK_BOT_TOKEN`.
8. Set the following environment variables:
    - `SLACK_APP_TOKEN` to the respective value noted above
    - `SLACK_BOT_TOKEN` to the respective value noted above
    - `SLACK_CHANNEL` to either a channel name #channel or to a channel ID Cxxxxxxxxxx.
9. Start the [BasicTool](src/main/java/de/miltschek/openttdadmin/BasicTool.java). That's it!

#### Known Issues
If using more than one instance of the app with the same workspace, the messages will be randomly delivered to one of the running instances. Still investigating, whether it can be solved somehow.

How-To GeoIp
------------
The service is ready to use and requires no configuration, for as long as you stick to the [Terms of Service](https://ip-api.com/docs/legal), especially:
- The use of the API is strictly limited for a non-commercial purpose and in a non-commercial environment.
- If you exceed the usage limit of 45 requests per minute your access to the API will be temporarily blocked. Repeatedly exceeding the limit will result in your IP address being banned for up to 1 hour.

How-To Google Translator
------------------------
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project if needed.
3. Scroll down the menue to 'Artificial Intelligence' / 'Translation'
4. Enable the API.
5. You may be required to set up your payment account/method.
6. Go to 'APIs & Services' / 'Credentials' menue.
7. Create a new service account:
    - 'Create credentials' button
    - 'Service account' type
    - Choose some name and id
    - Grant this service account access to project: select the role 'Cloud Translation API User'.
8. If not directed automatically, click on the newly created account and select 'Add Key'.
9. Choose the 'JSON' format and download the key in a **secure place**.
10. Set the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to contain the path to the JSON key file:
    - Linux-like: `export GOOGLE_APPLICATION_CREDENTIALS=/home/you/mykey.json`
    - Windows-like: `set GOOGLE_APPLICATION_CREDENTIALS=C:\Folder\mykey.json`

**Caution**
The Google Translation Service **does cost real money**. Please refer to the current price list.
