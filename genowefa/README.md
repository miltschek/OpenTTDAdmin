# Genowefa
A fully functional admin app ready to use out of the box:
- Connecting to a specified OpenTTD server.
- Looking up the geo location of client IP addresses to create customized welcome messages.
- Translating international messages, e.g. to make it easier for people coming from all around the world.
- Forwarding chat messages to a Slack channel and back to the game, e.g. to be 'always online' as an admin.

Building
--------

Either take a [pre-generated JAR file](https://github.com/miltschek/OpenTTDAdmin/releases/latest) or compile it by yourself. The project is Maven-enabled and this library depends on two other artifacts: the [OpenTTD Client](../openttdclient/) and the [Integrations](../integrations/). It means, the dependencies have to be built and installed in your Maven repository first to be able to build this app. To compile, make sure you use JDK 11 or newer and request the following build targets:
```
mvn clean compile package assembly:single
```

- `package` will create a genowefa-VERSION.jar file
- `assembly:single` will add dependencies to a genowefa-VERSION-jar-with-dependencies.jar file
- an optional `install` would register the library in your Maven repository, but this is not needed here

How-To
------

You may use an official release of the app (as a JAR file):
- Make sure to install a JRE 11 or newer.
- Make sure you have the required JAR files in place:
    - openttdclient-VERSION-jar-with-dependencies.jar
    - integrations-VERSION-jar-with-dependencies.jar
    - genowefa-VERSION-jar-with-dependencies.jar
    - optionally, an [Slf4j](http://www.slf4j.org/manual.html) library for logging purposes
- The GeoIP functions will work automatically as no special account is needed for (ip-api.com)[https://ip-api.com]
- The Google Translate requires a service account or the function will be deactivated
    - Instructions to be found in the [Integrations How-To Google Translator](../integrations)
    - Set the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to contain the path to the JSON key file:
      - Linux-like: `export GOOGLE_APPLICATION_CREDENTIALS=/home/you/mykey.json`
      - Windows-like: `set GOOGLE_APPLICATION_CREDENTIALS=C:\Folder\mykey.json`
- The Slack integration requires registration of the app as a bot or the function will be deactivated
    - Instructions to be found in the [Integrations How-To Slack (two-way)](../integrations)
    - Set the environment variable `SLACK_APP_TOKEN` to the application's token
      - Linux-like: `export SLACK_APP_TOKEN=xapp-...`
      - Windows-like: `set SLACK_APP_TOKEN=xapp-...`
    - Set the environment variable `SLACK_BOT_TOKEN` to the bot's token
      - Linux-like: `export SLACK_BOT_TOKEN=xoxb-...`
      - Windows-like: `set SLACK_BOT_TOKEN=xoxb-...`
    - Set the environment variable `SLACK_CHANNEL` to either a channel name #channel or to a channel ID Cxxxxxxxxxx
      - Linux-like: `export SLACK_CHANNEL=#your_channel`
      - Windows-like: `set SLACK_CHANNEL=#your_channel`
- Start the app with the following command line:
    - Windows, without logging
```
C:\> java -jar genowefa-VERSION-jar-with-dependencies.jar 127.0.0.1 3977 OPENTTD_ADMIN_PASSWORD
```
    - Windows, with console logging
```
C:\> java -cp slf4j-simple-1.7.30.jar -Dorg.slf4j.simpleLogger.log.de.miltschek=debug -jar genowefa-VERSION-jar-with-dependencies.jar 127.0.0.1 3977 OPENTTD_ADMIN_PASSWORD
```
    - Linux, without logging
```
~$ java -cp -jar ./genowefa-VERSION-jar-with-dependencies.jar 127.0.0.1 3977 OPENTTD_ADMIN_PASSWORD
```
    - Linux, with logging
```
~$ java -cp ./slf4j-simple-1.7.30.jar -Dorg.slf4j.simpleLogger.log.de.miltschek=debug -jar ./genowefa-VERSION-jar-with-dependencies.jar 127.0.0.1 3977 OPENTTD_ADMIN_PASSWORD
```
    - `127.0.0.1` should be the address of the OpenTTD server
    - `3977` should be the port number of the Admin Port of the OpenTTD server as configured in the openttd.cfg under `[network]`: `server_admin_port`
    - `OPENTTD_ADMIN_PASSWORD` should be the password as configured in the openttd.cfg file under `[network]`: `admin_password`
    
User's Manual
-------------

### Command line arguments

```
JAVA -jar genowefa-VERSION-jar-with-dependencies.jar OPENTTD_ADDRESS OPENTTD_ADMIN_PORT OPENTTD_ADMIN_PASSWORD
```

- `OPENTTD_ADDRESS` is the address of the OpenTTD serve (host name, FQDN, IPv4 or IPv6)
- `OPENTTD_PORT` is the port number of the Admin Port of the OpenTTD server as configured in the openttd.cfg under `[network]`: `server_admin_port`
- `OPENTTD_ADMIN_PASSWORD` is be the password as configured in the openttd.cfg file under `[network]`: `admin_password`
    
### Environment variables

- `GOOGLE_APPLICATION_CREDENTIALS` contains a path to the JSON key file of the Google service account
- `SLACK_APP_TOKEN` contains an ap-level token of the Slack app (starting with xapp-...)
- `SLACK_BOT_TOKEN` contains a bot user OAuth access token of the Slack app (starting with xoxb-...)
- `SLACK_CHANNEL` contains either a channel name (starting with a #) or a channel ID (Cxxxxxxxxxx)
    
### Interaction with Players

The app sends a welcome message to any new player in the game (including spectators):
1. A hardcoded 'hello' message in many languages depending on the player's country.
2. An additional, configurable welcome message, if available in the file `on_new_client.txt` in the current working directory.
   Just save your custom message encoded in UTF-8 in this file and it will be sent as a private chat to any new player.

### In-Game Commands

The players may request one of the following actions within the game:
- `!help` to get a list of supported commands
- `!admin <message>` to address a message to the game administrator (increased visibility with the :boom: emoji)
- `!reset` to remove the company the requesting user is currently playing
    The official implementation of the game requires all players to leave the company before it can be closed. That's why the players are kicked out of the game before the company gets closed. Unfortunately, there is no official command to change the role of the players to spectators, which would allow a nicer solution.
- `!dict <text>` to translate a message from any language into English

### Slack interaction

If the `SLACK_CHANNEL` environment variable is set, the application tries to establish a connection to the Slack server.

The application forwards to the specified channel the following data:
- Admin contact requests, prefixed with :boom:
- Non-empty chat messages from the players, including an optional translation (see Google Translate integration)
- Information on company reset requests
- Information on user name change requests
- Translation requests results
- Information on new players (id, name, network address, country, city and proxy-flag)
- Information on disconnected players (if available, with the error reason)
- Information on companies (new, changed, removed)
- Information whether a new game has been started

Any message written to the Slack channel by another user will be forwarded to the game as a public message coming from the server.

### Google Translate

If the `GOOGLE_APPLICATION_CREDENTIALS` environment variable is set, the application:
1. Forwards all public chat messages to the the translating service and - if recognized as a non-English text - is appended to the Slack notification.
2. Offers a translation service to the players, reacting on the `!dict` request (see In-Game Commands).

**Caution**
The Google Translation Service **does cost real money**. Please refer to the current price list.

### Geolocation

For the geographical location of a network address, the service of [ip-api.com](https://ip-api.com/). There is no configuration needed.
For license details, see the project [Integrations](../integrations).

Further Development
-------------------

Feel free to report your ideas for additional functions of the app. On the ideas list there are points like:
- Requesting a list of current players and companies, current game date etc. out of Slack
- Integration with other messengers

Known Issues
------------

If using more than one instance of the app with the same Slack workspace, the messages will be randomly delivered to one of the running instances. It looks like Slack is treating it as load-balancing. Still investigating, whether it can be solved somehow, so that every instance does receive each message. It would be useful for setups with multiple games and one Slack workspace with multiple channels, each for a single game.
