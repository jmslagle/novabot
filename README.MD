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
    2. [Linux Installation](#linux-install)
    3. [Mac Installation](#mac-install)
2. [Configuration](#configuration)
3. [Usage](#usage)
    1. [Basic Examples](#basic-examples)
4. [FAQ](#faq)
5. [Discord Support Server](https://discord.gg/RgWSqyU)
6. [Wiki - WIP](http://pallettown.readthedocs.io/)

### Prerequisites

- [Java JRE 64-bit](https://java.com/en/download/manual.jsp)
- [MySQL/MariaDB](https://mariadb.org/download/)

That's it!
## <a name="install-guides"></a> Installation Guides

### <a name="windows-install"></a>Windows

#### Install prerequisites

##### Java JRE 64-bit

Download and run the offline 64-bit installer from https://java.com/en/download/manual.jsp. 
Make sure you select the 64-bit installer or novabot will not work correctly.

##### MariaDB

https://mariadb.org/download/

#### Create a discord bot, add it to your server, and get the bot token

Follow [this guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to create a discord bot, add it to your server, and get its bot token.

#### Create a new database

In your MariaDB/MySQL terminal, use the below command to create a new database, where name is the desired name of the new database.

`create database name;`

Once you've created the database, select the database:

`use name;`

Next, run the DB creation script that can be found [here](create-db.sql), either by copy and posting the contents of the file into the terminal, or with the `source` command like so:

`source /path/to/file/create-db.sql;`

#### Running novabot

There a two ways of running novabot. 

Starting it from cmd.exe (or alternative shell) with:

`java -jar novabot.jar`

Or simply double clicking the .jar file in Windows Explorer.

You can find the .jar file [here](https://github.com/novskey/novabot/releases).

###<a name="linux-install"></a> Linux

Tested on Ubuntu 16.04

#### Install prerequisites

##### Java JRE

Install Java8 through terminal with

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

#### Create a discord bot, add it to your server, and get the bot token

Follow [this guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to create a discord bot, add it to your server, and get its bot token.

#### Create a new database

In your MariaDB/MySQL terminal, use the below command to create a new database, where name is the desired name of the new database.

`create database name;`

Once you've created the database, select the database:

`use name;`

Next, run the DB creation script that can be found [here](create-db.sql), either by copy and posting the contents of the file into the terminal, or with the `source` command like so:

`source /path/to/file/create-db.sql;`

#### Running novabot

There a two ways of running novabot. 

Starting it from cmd.exe (or alternative shell) with:

`java -jar novabot.jar`

Or simply double clicking the .jar file in Windows Explorer.

You can find the .jar file [here](https://github.com/novskey/novabot/releases).
### <a name="mac-install"></a> Mac

Detailed instructions not written yet, however, if you install the following dependencies, it should work:

- [Java JRE](https://java.com/en/download/manual.jsp)

#### Create a discord bot, add it to your server, and get the bot token

Follow [this guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to create a discord bot, add it to your server, and get its bot token.

#### Create a new database

In your MariaDB/MySQL terminal, use the below command to create a new database, where name is the desired name of the new database.

`create database name;`

Once you've created the database, select the database:

`use name;`

Next, run the DB creation script that can be found [here](create-db.sql), either by copy and posting the contents of the file into the terminal, or with the `source` command like so:

`source /path/to/file/create-db.sql;`

#### Running novabot

There a two ways of running novabot. 

Starting it from cmd.exe (or alternative shell) with:

`java -jar novabot.jar`

Or simply double clicking the .jar file in Windows Explorer.

You can find the .jar file [here](https://github.com/novskey/novabot/releases).

## <a name="configuration"></a> Configuration

### Main files

#### config.ini

[example file](config.example.ini)

#### geofences.txt

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

[example file](geofences.txt)

#### suburbs.txt

`suburbs.txt` is one suburb per line, for example: 

```
suburb1
suburb2
suburb3
suburb4
```

[example file](suburbs.txt)

#### formatting.ini

[example file](formatting.txt)

#### gkeys.txt

`gkeys.txt` is one Google API key per line

[example file](gkeys.txt)

#### supporterlevels.txt

`supporterlevels.txt` is formatted like this:

```
roleId = [max pokemon limit, max raid limit]
```

[example file](supporterlevels.txt)

#### raidchannels.ini

`raidchannels.ini` is formatted in blocks like this:

```
[channelId]
geofences = all OR a single geofence name (references geofences.txt) OR a list like [geofence1, geofence2]
filter = filename of filter eg: raidfilter.json
```

Optionally, a formatting parameter can be added:

```
formatting = filename of formatting file eg: specialformat.ini
```

[example file](raidchannels.ini)

#### raidchats.txt

[example file](raidchats.txt)

#### pokechannels.ini

`pokechannels.ini` is formatted in blocks like this:

```
[channelId]
geofences = all OR a single geofence name (references geofences.txt) OR a list like [geofence1, geofence2]
filter = filename of filter eg: pokefilter.json
```

Optionally, a formatting parameter can be added:

```
formatting = filename of formatting file eg: specialformat.ini
```

[example file](pokechannels.ini)

### Filters

#### Pokemon filters

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
#### Raid filters

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

## <a name="usage"></a> Usage

### <a name="basic-examples"></a> Basic Examples
