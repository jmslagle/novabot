# novabot

novabot is a [Discord](https://discordapp.com) bot that uses data from [RocketMap](https://github.com/RocketMap/RocketMap) to provide a complete, configurable package for Pokemon Go communities.
novabot's features include:

- Personalised pokemon and raid notifications, delivered by DM.
- Filter settings based on geofence, suburb, or IV.
- Automated posting of pokemon/raids to Discord channels.
- Unique raid coordination system.
- Optional tiered donator/supporter system using Discord roles to reward members of your community.

You can download the latest release [here](https://github.com/novskey/novabot/releases) (Just download the .jar file) 

1. [Installation Guides](#install-guides)
    1. [Windows Installation](#windows-install)
        - [Install prerequisites](#win-prereqs)
            - [Java JRE](#win-java)
            - [MariaDB](#win-mariadb)
        - [Create a Discord bot](#win-create-bot)
        - [Create a new database](#win-create-db)
        - [Download and configure novabot](#win-configure-novabot)
        - [Running novabot](#win-running-novabot)
    2. [Linux Installation](#linux-install)
        - [Install prerequisites](#linux-prereqs)
            - [Java JRE](#linux-java)
            - [MariaDB](#linux-mariadb)
        - [Create a Discord bot](#linux-create-bot)
        - [Create a new database](#linux-create-db)
        - [Download and configure novabot](#linux-configure-novabot)
        - [Running novabot](#linux-running-novabot)
    3. [Mac Installation](#mac-install)
        - [Install prerequisites](#mac-prereqs)
            - [Java JRE](#mac-java)
            - [MariaDB](#mac-mariadb)
        - [Create a Discord bot](#mac-create-bot)
        - [Create a new database](#mac-create-db)
        - [Download and configure novabot](#mac-configure-novabot)
        - [Running novabot](#mac-running-novabot)
2. [Configuration](#configuration)
    1. [Configuration Files](#configuration-files)
        - [config.ini](#config.ini)
        - [geofences.txt](#geofences.txt)
        - [suburbs.txt](#suburbs.txt)
        - [formatting.ini](#formatting.ini)
        - [gkeys.txt](#gkeys.txt)
        - [supporterlevels.txt](#supporterlevels.txt)
        - [raidchannels.ini](#raidchannels.ini)
        - [pokechannels.ini](#pokechannels.ini)
    2. [config.ini [config] Parameters](#config-parameters)
        - [token](#token)
        - [blacklist](#blacklist)
        - [raidBosses](#raidBosses)
        - [useRmDb](#useRmDb)
        - [standardRaidTable](#standardRaidTable)
        - [googleSuburbField](#googleSuburbField)
        - [pokemon](#pokemon)
        - [raids](#raids)
        - [raidOrganisation](#raidOrganisation)
        - [pokePollingDelay](#pokePollingDelay)
        - [pokemonThreads](#pokemonThreads)
        - [raidPollingDelay](#raidPollingDelay)
        - [raidThreads](#raidThreads)
        - [startupMessage](#startupMessage)
        - [footerText](#footerText)
        - [stats](#cfg-stats)
        - [timezone](#timezone)
        - [nonSupporterLimit](#nonSupporterLimit)
        - [countLocationsInLimits](#countLocationsInLimits)
        - [adminRole](#adminRole)
        - [novabotRole](#novabotRole)
        - [commandChannel](#commandChannel)
        - [logging](#cfg-logging)
        - [roleLogChannel](#roleLogChannel)
        - [userUpdatesChannel](#userUpdatesChannel)
    3. [config.ini [novabot db] Parameters](#novabotdb-parameters)
        - [user](#nb-user)
        - [password](#nb-password)
        - [ip](#nb-ip)
        - [port](#nb-port)
        - [dbName](#nb-dbName)
    4. [config.ini [rocketmap db] Parameters](#rocketmapdb-parameters)
        - [user](#rm-user)
        - [password](#rm-password)
        - [ip](#rm-ip)
        - [port](#rm-port)
        - [dbName](#rm-dbName)
3. [Filters](#filters)
    1. [Pokemon Filters](#pokemon-filters)
    2. [Raid Filters](#raid-filters)
4. [Text Substitution](#text-substitution)
    1. [Generic Substitutions](#generic-substitutions)
        - [lat](#lat)
        - [lng](#lng)
        - [geofence](#geofence)
        - [gmaps](#gmaps)
        - [applemaps](#applemaps)
        - [street_num](#street_num)
        - [street](#street)
        - [city](#city)
        - [state](#state)
        - [postal](#postal)
        - [neighbourhood](#neighbourhood)
        - [sublocality](#sublocality)
        - [country](#country)
        - [cp](#cp)
        - [pkmn](#pkmn)
        - [quick_move](#quick_move)
        - [charge_move](#charge_move)
        - [time_left](#time_left)
    2. [Pokemon Substitutions](#pokemon-substitutions)
        - [24h_time](#24h_time)
        - [12h_time](#12h_time)
        - [pkmn_id](#pkmn_id)
        - [atk](#atk)
        - [def](#def)
        - [sta](#sta)
        - [iv](#iv)
        - [weight](#weight)
        - [height](#height)
        - [size](#size)
        - [gender](#gender)
        - [form](#form)
        - [lvl30cp](#lvl30cp)
        - [lvl35cp](#lvl35cp)
        - [pokemon-level](#pokemon-level)
    3. [Raid Substitutions](#raid-substitutions)
        - [gym_name](#gym_name)
        - [team_name](#team_name)
        - [24h_end](#24h_end)
        - [12h_end](#12h_end)
        - [24h_start](#24h_start)
        - [12h_start](#12h_start)
        - [time_left_start](#time_left_start)
        - [lvl20cp](#lvl20cp)
        - [lvl25cp](#lvl25cp)
        - [raid-level](#raid-level)
        - [lobbycode](#lobbycode)
5. [Logging](#logging)
6. [Stats](#stats)
7. [Notification Limits](#notification-limits)
8. [Raid Organisation](#raid-organisation-info)
9. [Usage](#usage)
10. [FAQ](#faq)
11. [Discord Support Server](https://discord.gg/P3qrvkp)

### Prerequisites
- [Java JRE 64-bit](https://java.com/en/download/manual.jsp)
- [MySQL/MariaDB](https://mariadb.org/download/)

That's it!
## <a name="install-guides"></a> Installation Guides

### <a name="windows-install"></a>Windows

#### <a name="win-prereqs"></a>Install prerequisites

##### <a name="win-java"></a> Java JRE 64-bit
Download and run the offline 64-bit installer from https://java.com/en/download/manual.jsp. 
Make sure you select the 64-bit installer or novabot will not work correctly.

##### <a name="win-mariadb"></a> MariaDB
Follow the instructions [here](https://mariadb.org/download/) to install MariaDB.

#### <a name="win-create-bot"></a> Create a discord bot, add it to your server, and get the bot token
Follow [this guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to create a discord bot, add it to your server, and get its bot token.

#### <a name="win-create-db"></a>Create a new database

In your MariaDB/MySQL terminal, use the below command to create a new database, where name is the desired name of the new database.

`create database name;`

Once you've created the database, select the database:

`use name;`

Next, run the DB creation script that can be found [here](create-db.sql), either by copy and posting the contents of the file into the terminal, or with the `source` command like so:

`source /path/to/file/create-db.sql;`

#### <a name="win-configure-novabot"></a>Download and configure novabot

You can find the latest novabot release [here](https://github.com/novskey/novabot/releases).
Each release includes all the necessary configuration and data files that novabot needs.
Once you've downloaded a release, see [configuration](#configuration) on how to configure novabot.

#### <a name="win-running-novabot"></a>Running novabot

Run novabot from cmd.exe (or alternative shell) with:

`java -jar novabot.jar`

###<a name="linux-install"></a> Linux

Tested on Ubuntu 16.04

#### <a name="linux-prereqs"></a>Install prerequisites

##### <a name="linux-java"></a>Java JRE

Install Java8 through terminal with

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

##### <a name="linux-mariadb"></a>MariaDB

Follow the instructions [here](https://downloads.mariadb.org/mariadb/repositories/) to install MariaDB on your server.

#### <a name="linux-create-bot"></a>Create a discord bot, add it to your server, and get the bot token

Follow [this guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to create a discord bot, add it to your server, and get its bot token.

#### <a name="linux-create-db"></a>Create a new database

In your MariaDB/MySQL terminal, use the below command to create a new database, where name is the desired name of the new database.

`create database name;`

Once you've created the database, select the database:

`use name;`

Next, run the DB creation script that can be found [here](create-db.sql), either by copy and posting the contents of the file into the terminal, or with the `source` command like so:

`source /path/to/file/create-db.sql;`

#### <a name="linux-configure-novabot"></a>Download and configure novabot

You can find the latest novabot release [here](https://github.com/novskey/novabot/releases).
Each release includes all the necessary configuration and data files that novabot needs.
Once you've downloaded a release, see [configuration](#configuration) on how to configure novabot.

#### <a name="linux-running-novabot"></a>Running novabot

Run novabot from a terminal with:

`java -jar novabot.jar`
### <a name="mac-install"></a> Mac

#### <a name="mac-prereqs"></a> Install prerequisites

##### <a name="mac-java"></a>Java JRE

Follow the instructions [here](https://java.com/en/download/help/mac_install.xml) to install Java.

##### <a name="mac-mariadb"></a>MariaDB
Follow the instructions [here](https://mariadb.org/download/) to install MariaDB.

#### <a name="mac-create-bot"></a>Create a discord bot, add it to your server, and get the bot token

Follow [this guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to create a discord bot, add it to your server, and get its bot token.

#### <a name="mac-create-db"></a> Create a new database

In your MariaDB/MySQL terminal, use the below command to create a new database, where name is the desired name of the new database.

`create database name;`

Once you've created the database, select the database:

`use name;`

Next, run the DB creation script that can be found [here](create-db.sql), either by copy and posting the contents of the file into the terminal, or with the `source` command like so:

`source /path/to/file/create-db.sql;`

#### <a name="mac-configure-novabot"></a>Download and configure novabot

You can find the latest novabot release [here](https://github.com/novskey/novabot/releases).
Each release includes all the necessary configuration and data files that novabot needs.
Once you've downloaded a release, see [configuration](#configuration) on how to configure novabot.

#### <a name="mac-running-novabot"></a>Running novabot

Run novabot from a terminal with:

`java -jar novabot.jar`

##<a name="configuration"></a> Configuration

###<a name="configuration-files"></a> Configuration Files

####<a name="config.ini"></a> config.ini

`config.ini` is novabot's core configuration file. See [config.ini paramaters](#config-parameters) for more info on the different parameters.

[example file](config.example.ini)

####<a name="geofences.txt"></a> geofences.txt

The `geofences.txt` file is formatted in blocks like this:

```
[name,alias1,alias2,etc]
lat,lng
lat,lng
lat,lng
```

These blocks can then be stacked so all your geofences are in the same file like so: 

```
[fence1]
lat,lng
lat,lng
lat,lng
[fence2]
lat,lng
lat,lng
lat,lng
[fence3]
lat,lng
lat,lng
lat,lng
```

Users will be able to filter pokemon and raids against any geofence found in this file, for example:

```
!addpokemon larvitar <fence1, fence3>
!addpokemon chikorita fence2
```

[example file](geofences.txt)

#### <a name="suburbs.txt"></a>suburbs.txt

The `suburbs.txt` file is formatted with one suburb per line, for example: 

```
suburb1
suburb2
suburb3
suburb4
```

Users will be able to filter pokemon and raids against any suburb found in this file. 
The spawns are filtered by comparing the suburb as defined in suburbs.txt with the Google reverse geocoding response. 
Which field of the Google reverse geocoding response is used to compare suburbs can be defined in the config. See [googleSuburbField](#googleSuburbField) for more info.

[example file](suburbs.txt)

####<a name="formatting.ini"></a>formatting.ini

[example file](formatting.txt)

####<a name="gkeys.txt"></a> gkeys.txt

`gkeys.txt` is one Google API key per line.

The Google API keys will be rotated when accessing Google's APIs.

[example file](gkeys.txt)

####<a name="supporterlevels.txt"></a> supporterlevels.txt
***Note:*** not sure how to get the a Discord role's ID? See the [faq](#faq-roleid)

`supporterlevels.txt` is formatted like this:

When a user adds something to their settings, novabot first checks to see if what they're trying to add would exceed their [notification limit](#notification-limits).
The default notification limit is defined in `config.ini` using [nonSupporterLimit](#nonSupporterLimit), however more limits can be defined here.
If a user has a role specified in this file, they will be granted the limit that corresponds to their role.

Limits are formatted like this:

```
roleId = [max pokemon limit, max raid limit, max preset limit]
```

Where n can be used in place of a limit to denote unlimited settings of that type, for example:

```
roleId = [n,n,n]
```

Would grant unlimited notifications of all types.


[example file](supporterlevels.txt)

#### <a name="raidchannels.ini"></a>raidchannels.ini

`raidchannels.ini` is formatted in blocks like this:

```
[channelId]
geofences = all OR a single geofence name (references geofences.txt) OR a list like [geofence1, geofence2]
filter = filename of filter eg: raidfilter.json
```

All raid spawns that pass the specified filter, and fall within the specified geofences will be posted to the specified Discord channel.

Two optional parameters exist, chat and formatting.
The formatting parameter allows different channels to use different formatting files.
The chat parameter is used to configure a raid chat. See [raid organisation](#raid-organisation) for more info on raid chats.

```
chat = channelId of raid chat channel
formatting = filename of formatting file eg: specialformat.ini
```

[example file](raidchannels.ini)

#### <a name="pokechannels.ini"></a>pokechannels.ini

`pokechannels.ini` is formatted in blocks like this:

```
[channelId]
geofences = all OR a single geofence name (references geofences.txt) OR a list like [geofence1, geofence2]
filter = filename of filter eg: pokefilter.json
```

All pokemon spawns that pass the specified filter, and fall within the specified geofences will be posted to the specified Discord channel.

Optionally, a formatting parameter can be added to allow different channels to use different formatting files.

```
formatting = filename of formatting file eg: specialformat.ini
```

[example file](pokechannels.ini)

### <a name="config-parameters"></a>config.ini [config] parameters

#### <a name="token"></a>token :: String *default:* null
The bot token for your Discord bot. You can find your bot's token like this:
 
1. Login to the [Discord developer dashboard](https://discordapp.com/developers/applications/me).
2. Select your bot.
3. Scroll down to the Bot section, and click the `click to reveal` next to Token.
4. Copy the full token into your config.ini

If you don't have a Discord bot set up yet, see the [faq](#faq) on how to make one.

#### <a name="blacklist"></a>blacklist :: Integer List *default:* `[]`
A list of pokemon IDs that users will not be able to add to their pokemon settings.
This parameter can be used to prevent users adding pokemon that will cause spam or excessive load on the bot, such as common spawns like Pidgey.

#### <a name="raidBosses"></a>raidBosses :: Integer List *default:* `[2,5,8,11,28,31,34,38,62,65,68,71,73,76,82,91,94,105,123,129,131,137,139,143,144,145,146,150,243,244,245,248,249,302,303,359]`
A list of pokemon IDs that users ***will*** be able to add to their settings. 
The primary use of this parameter is to enable users to add new raid bosses to their settings. 
This parameter can also be used to only allow users to add certain raid bosses to their settings.

#### <a name="useRmDb"></a>useRmDb :: Boolean *default:* `True`
Whether or not the bot should poll the RocketMap database for new pokemon/raids. 
This parameter can be disabled to allow testing of just the bot's commands and user interactions, without actually notifying anyone.

#### <a name="standardRaidTable"></a>standardRaidTable :: Boolean *default:* `True`
Whether or not your specified RocketMap db uses the default raid table. 
Leave this true, its mostly a legacy option for some people running a small fork.

#### <a name="googleSuburbField"></a>googleSuburbField :: String *default:* `city`
Which field of the Google reverse geocoding response to use as the suburb. 
This lets users filter by Google's reverse geocoded address as well as geofences.
Used in conjunction with [suburbs.txt](#suburbs.txt)

Possible fields are: 
```
street_num
street
city
state
postal
neighbourhood
sublocality
country
```

#### <a name="pokemon"></a>pokemon :: Boolean *default:* `True`
Whether or not to allow users to add pokemon to their settings, and scan the RocketMap database for new pokemon.

#### <a name="raids"></a>raids :: Boolean *default:* `True`
Whether or not to allow users to add raid to their settings, and scan the RocketMap database for new raids.

#### <a name="raidOrganisation"></a>raidOrganisation :: Boolean *default:* `True`
Whether or not to use novabot's raid organisation features. See [here](#raid-organisation) for more info.

#### <a name="pokePollingDelay"></a>pokePollingDelay :: Integer *default:* `2`
The delay in seconds to be used between querying the RocketMap database for new pokemon.

#### <a name="pokemonThreads"></a>pokemonThreads :: Integer *default:* `2`
How many threads to use to manage delivering of pokemon messages (both PMs and to channels).

#### <a name="raidPollingDelay"></a>raidPollingDelay :: Integer *default:* `15`
The delay in seconds to be used between querying the RocketMap database for new raids.

#### <a name="raidThreads"></a>raidThreads :: Integer *default:* `2`
How many threads to use to manage delivering of raid messages (both PMs and to channels).

#### <a name="startupMessage"></a>startupMessage :: Boolean *default:* `False`
Whether or not to post a message to the command channel when novabot first starts up.

#### <a name="footerText"></a>footerText :: String *default:* `null`
The text to display at the bottom of every message. Do not specify in config.ini for no footer text. 

#### <a name="cfg-stats"></a>stats :: Boolean *default:* `True`
Whether or not to enable the !stats command. See [here](#stats) for more info on the stats functionality.

#### <a name="timezone"></a>timezone :: String *default:* `System Default`
What time zone to use for <24h_time> and <12h_time> text substitutions. For accepted time zones, see the [list of tz time zones](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones) and look in the TZ column.

#### <a name="nonSupporterLimit"></a>nonSupporterLimit :: [Notification Limit](#notification-limits) *default:* `[n,n,n]`
The notification limit for non supporters (as determined by [supporterlevels.txt](#supporterlevels.txt)).
See [notification limits](#notification-limits) for more info and how to format the parameter.

#### <a name="countLocationsInLimits"></a>countLocationsInLimits :: Boolean *default:* `True`
Whether or not to count different locations for a single setting towards a user's limit.
For example, if a user has a maximum of 3 pokemon settings:

If this parameter is true, `!addpokemon chikorita <north, south, west, east>` would go over their limit.
If this parameter is false, the previous command would work, and `!addpokemon <bulbasaur, squirtle, charmander, chikorita>` would not work.

#### <a name="adminRole"></a>adminRole :: Integer *default:* `null`
The Discord role ID used by novabot to identify admins. Admin users can use the !reload command to reload novabot's config while its running.

#### <a name="novabotRole"></a>novabotRole :: Integer *default:* `null`
The Discord role ID of the role assigned to novabot so novabot can grant itself permission to view the new channels created if raid organisation is enabled.
See the [faq](#faq-roleid) for how to get a role's ID.

#### <a name="commandChannel"></a>commandChannel :: Integer *default:* `null`
The Discord channel ID of the channel that novabot will accept commands in. novabot also accepts commands via PM.
See the [faq](#faq-channelid) for how to get a channel's ID.

#### <a name="cfg-logging"></a>logging :: Boolean *default:* `False`
Whether or not to enable the logging features of novabot. See [logging](#logging) for more info on novabot's logging capabality.

#### <a name="roleLogChannel"></a>roleLogChannel :: Integer *default:* `null`
**This parameter is only required if logging is enabled.**
The Discord channel ID of the channel that novabot will accept commands in. novabot also accepts commands via PM.
See the [faq](#faq-channelid) for how to get a channel's ID.

#### <a name="userUpdatesChannel"></a>userUpdatesChannel :: Integer *default:* `null`
**This parameter is only required if logging is enabled.**
The Discord channel ID of the channel that novabot will post user updates in. See [logging](#logging) for more info on novabot's logging.
See the [faq](#faq-channelid) for how to get a channel's ID.

### <a name="novabotdb-parameters"></a>config.ini [novabot db] parameters

#### <a name="nb-user"></a>user :: String *default:* `null`
The username to use when connecting to the novabot settings database.

#### <a name="nb-password"></a>password :: String *default:* `null`
The password to use when connecting to the novabot settings database.

#### <a name="nb-ip"></a>ip :: String *default:* `null`
The ip of the novabot settings database.

#### <a name="nb-port"></a>port :: Integer *default:* `3306`
The port of the novabot settings database.

#### <a name="nb-dbName"></a>dbName :: String *default:* `null`
The databaase name of the novabot settings database.

### <a name="rocketmapdb-parameters"></a>config.ini [rocketmap db] parameters

#### <a name="rm-user"></a>user :: String *default:* `null`
The username to use when connecting to the rocketmap database.

#### <a name="rm-password"></a>password :: String *default:* `null`
The password to use when connecting to the rocketmap database.

#### <a name="rm-ip"></a>ip :: String *default:* `null`
The ip of the rocketmap database.

#### <a name="rm-port"></a>port :: Integer *default:* `3306`
The port of the rocketmap database.

#### <a name="rm-dbName"></a>dbName :: String *default:* `null`
The databaase name of the rocketmap database.

### <a name="filters"></a>Filters

#### <a name="pokemon-filters"></a>Pokemon Filters

Examples:

##### All 100iv pokemon

```
{
  "Default": {
    "min_iv": "100"
  }
}
```

##### All pokemon above 90 IV; dragonair above 80iv; and all unown
```
{
  "Default": {
    "min_iv": "90"
  },
  "Dragonair": {
    "min_iv": "80"
  },
  "Unown": "True"
}
```
#### <a name="raid-filters"></a>Raid Filters

Examples:

##### All eggs and nothing else

```
{
  "Egg1": "True",
  "Egg2": "True",
  "Egg3": "True",
  "Egg4": "True",
  "Egg5": "True"
}
```

##### All raids and eggs

```
{
  "Level1": "True",
  "Level2": "True",
  "Level3": "True",
  "Level4": "True",
  "Level5": "True"
}
```

##### Specific raid bosses only

```
{
  "Tyranitar": "True",
  "Lugia": "True",
  "Magikarp": "True"
}
```

##### Level 3 raid bosses, and level 4+ eggs and bosses

```
{
  "Machamp": "True",
  "Alakazam": "True",
  "Gengar": "True",
  "Ninetales": "True",
  "Scyther": "True",
  "Omastar": "True",
  "Porygon": "True",
  "Level4": "True",
  "Level5": "True"
}
```

## <a name="text-substitution"></a>Text Substitution 

### <a name="generic-substitutions"></a>Generic Substitutions
Generic substitutions for spawns:

#### <a name="lat"></a>lat
Latitude of the spawn.
#### <a name="lng"></a>lng
Longitude of the spawn.
#### <a name="geofence"></a>geofence
The geofence or list of geofences that the spawn falls in.
#### <a name="gmaps"></a>gmaps
Google Maps link to the spawn.
#### <a name="applemaps"></a>applemaps
Apple Maps link to the spawn.
#### <a name="street_num"></a>street_num
Google reverse geocoding street number of the spawn.
#### <a name="street"></a>street
Google reverse geocoding street name of the spawn.
#### <a name="city"></a>city
Google reverse geocoding city of the spawn.
#### <a name="state"></a>state
Google reverse geocoding state of the spawn.
#### <a name="postal"></a>postal
Google reverse geocoding postal area of the spawn.
#### <a name="neighbourhood"></a>neighbourhood
Google reverse geocoding neighourhood of the spawn.
#### <a name="sublocality"></a>sublocality
Google reverse geocoding sublocality of the spawn.
#### <a name="country"></a>country
Google reverse geocoding country of the spawn.
#### <a name="cp"></a>cp
CP of the spawn.
#### <a name="pkmn"></a>pkmn
Pokemon name of the spawn.
#### <a name="quick_move"></a>quick_move
Quick move of the spawn.
#### <a name="charge_move"></a>charge_move
Charge move of the spawn.
#### <a name="time_left"></a>time_left
Time left of the spawn in format MMm SSs if less than one hour and HHh MMm SSs if greater than one hour.

### <a name="pokemon-substitutions"></a>Pokemon Substitutions

Current substitutions for pokemon:

#### <a name="24h_time"></a>24h_time
Despawn time of the pokemon in 24 hour HH:MM:SS format.
#### <a name="12h_time"></a>12h_time
Despawn time of the pokemon in 12 hour HH:MM:SS AM/PM format.
#### <a name="pkmn_id"></a>pkmn_id
Pokemon id of the spawn.
#### <a name="atk"></a>atk
Attack IV of the pokemon, `0` if unknown.
#### <a name="def"></a>def
Defense IV of the pokemon, `0` if unknown.
#### <a name="sta"></a>sta
Stamina IV of the pokemon, `0` if unknown.
#### <a name="iv"></a>iv
IV percentage of the pokemon rounded to 2 decimal places, `0` if unknown.
#### <a name="weight"></a>weight
Weight of the pokemon to 2 decimal places, `0.0` if unknown.
#### <a name="height"></a>height
Height of the pokemon to 2 decimal places, `0.0` if unknown.
#### <a name="size"></a>size
Size of the pokemon (tiny, normal, big etc), `?` if unknown.
#### <a name="gender"></a>gender
Gender of the pokemon as a symbol, `?` if unknown.
#### <a name="form"></a>form
Form letter of the pokemon. Empty if unknown.
#### <a name="lvl30cp"></a>lvl30cp
Max level 30 cp of the pokemon.
#### <a name="lvl35cp"></a>lvl35cp
Max level 35 cp of the pokemon.
#### <a name="pokemon-level"></a>level
Level of the pokemon. `0` if unknown.

### <a name="raid-substitutions"></a>Raid Substitutions

Current substitutions for raids:

#### <a name="gym_name"></a>gym_name
Name of the gym.
#### <a name="team_name"></a>team_name
Name of the team that controls the gym.
#### <a name="24h_end"></a>24h_end
End time of the raid in 24 hour HH:MM:SS format.
#### <a name="12h_end"></a>12h_end
End time of the raid in 12 hour HH:MM:SS AM/PM format.
#### <a name="24h_start"></a>24h_start
Start time of the raid in 24 hour HH:MM:SS format.
#### <a name="12h_start"></a>12h_start
Start time of the raid in 12 hour HH:MM:SS AM/PM format.
#### <a name="time_left_start"></a>time_left_start
Time left until raid starts in format MMm SSs if less than one hour and HHh MMm SSs if greater than one hour.
#### <a name="lvl20cp"></a>lvl20cp
Max catchable level 20 cp of the raid boss. Only valid if it's a raid boss and not an egg.
#### <a name="lvl25cp"></a>lvl25cp
Max catchable level 25 cp of the raid boss. Only valid if it's a raid boss and not an egg.
#### <a name="raid-level"></a>level
Level of the raid.
#### <a name="lobbycode"></a>lobbycode
Raid organisation lobby code of the raid. Only valid if [raidOrganisation](#raidOrganisation) is enabled. See [Raid Organisation](#raid-organisation-info) for more info on raid organisation.

## <a name="logging"></a>Logging
TBD

## <a name="stats"></a>Stats
TBD

## <a name="notification-limits"></a> Notification Limits
Notification limits are formatted as a list like this:
`[pokemon limit, raid limit, preset limit]`

n can be used to denote unlimited notifications of that type, for example:

[n,20,5] would give unlimited pokemon, 20 raids, and 5 preset notifications

## <a name="raid-organisation-info"></a> Raid Organisation

## <a name="usage"></a> Usage

## <a name="faq"></a> FAQ

### <a name="faq-channelid"></a> How do I get the ID of a channel?

1. Discord settings -> appearance -> enable developer mode
2. Right click the desired channel and click `Copy ID`

### <a name="faq-roleid"></a> How do I get the ID of a role?

1. Make sure the role is mentionable (check role settings)
2. Type `\@role` somewhere in your server.
3. It will look like `<@&12312351541>`. Copy the numbers only