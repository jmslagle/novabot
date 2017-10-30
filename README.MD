# novaBot

novaBot is a [Discord](https://discordapp.com) bot that uses data from [RocketMap](https://github.com/RocketMap/RocketMap) to provide a complete, configurable package for Pokemon Go communities.
novaBot's features include:

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
Make sure you select the 64-bit installer or novaBot will not work correctly.

##### MariaDB

https://mariadb.org/download/

#### Running novaBot

There a two ways of running novaBot. 

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

#### Running novaBot

There a two ways of running novaBot. 

Starting it from cmd.exe (or alternative shell) with:

`java -jar novabot.jar`

Or simply double clicking the .jar file in Windows Explorer.

You can find the .jar file [here](https://github.com/novskey/novabot/releases).
### <a name="mac-install"></a> Mac

Detailed instructions not written yet, however, if you install the following dependencies, it should work:

- [Java JRE](https://java.com/en/download/manual.jsp)
#### Running novaBot

There a two ways of running novaBot. 

Starting it from cmd.exe (or alternative shell) with:

`java -jar novabot.jar`

Or simply double clicking the .jar file in Windows Explorer.

You can find the .jar file [here](https://github.com/novskey/novabot/releases).

## <a name="configuration"></a> Configuration

### config.ini

### geofences.txt

[example file](geofences.txt)

### suburbs.txt

### formatting.ini

### gkeys.txt

### supporterlevels.txt

### raidalerts.txt

### raidchats.txt

### pokechannels.ini



## <a name="usage"></a> Usage

For more detailed documentation, please visit the [wiki](http://pallettown.readthedocs.io/).

### <a name="basic-examples"></a> Basic Examples

**Note: When using a proxy list, you may encounter errors if you have a proxy (possibly VPN too) configured for your system.
Please disable the proxy/VPN if you encounter any errors**

#### Create a single account with random details with manual captcha

- Enter any email address that is plusmail compatible into the Email field. (If you aren't sure, check the FAQ [here](#faq-plusmail))
- Click Create Accounts

Username, password, and date of birth will be randomly generated and entered for you.
All you have to do is wait for the captcha to appear, and solve it.

![Create a single account with random details](images/1-random-manual.jpg)

#### Create 5 accounts with random details with manual captcha, and save the usernames/passwords to a text file

- Enter a plusmail compatible email address in the Email field. (See [the FAQ](#faq-plusmail) if you're unsure)
- Enter the number 5 into the Number of accounts field.
- Click the Output File field, which will open up a file browser (It may take a while on some systems)
- Select your output file
- Click Create Accounts

![Create 5 accounts with random details with manual captcha, and save the usernames/passwords to a text file](images/5-random-manual-save.jpg)

#### Create 10 accounts with random details with 2captcha and a proxy list, and save the details to a file

- Enter a plusmail compatible email address in the Email field. (See [the FAQ](#faq-plusmail) if you're unsure)
- Enter the number 10 into the Number of accounts field.
- Enter your 2Captcha key into the 2Captcha Key field.
- Click the Output File field, which will open up a file browser (It may take a while on some systems)
- Select your output file
- Click the Proxy File field, which will open a file browser (It may take a while on some systems)
- Select your proxy file. It should be formatted as 1 proxy per line, eg:
    ```
    https://13.41.123.231:23123
    http://14.51.233.135:23321
    11.52.231.136:23321
    https://username:password@12.13.145.251:21313
    ```
- Click Create Accounts

![Create 10 accounts with random details with 2captcha and a proxy list, and save the details to a file](images/10-random-2captcha-proxies-save.jpg)

#### Create 5 numbered accounts (eg: user1,user2,user3) with a specific username and password, with manual captcha, and save the details to a file

- Enter a plusmail compatible email address in the Email field. (See [the FAQ](#faq-plusmail) if you're unsure)
- Enter the username you want to use into the Username field.
- Enter the password you want to use into the Password field. (It must contain a capital letter, number, and symbol)
- Enter the number 5 into the Number of accounts field.
- Enter the number 1 into the Start number field. Start number is where the accounts will be numbered from, eg:
    ```
    Start number 1, 5 accounts:
    user1,user2,user3,user4,user5
    Start number 10, 5 accounts:
    user10,user11,user12,user13,user14
    ```
  For more info on proxies see the FAQ [here](#faq-proxy-types)
- Click the Output File field, which will open up a file browser (It may take a while on some systems)
- Select your output file
- Click Create Accounts

![Create 5 numbered accounts (eg: user1,user2,user3) with a specific username and password, with manual captcha, and save the details to a file](images/5-specified-manual-save.jpg)

#### Create 10 accounts with random details, using 2Captcha, automatic Gmail verification, save to file, and use proxy list

- Enter a plusmail compatible email address in the Email field. (See [the FAQ](#faq-plusmail) if you're unsure)
- Enter the number 10 into the Number of accounts field.
- Enter your 2Captcha key into the 2Captcha Key field.
- Check the Auto Verify Accounts box.
- Enter your email account for auto verify. (eg: myaddress@hotmail.com, or mygmail@gmail.com if you've set up forwarding to gmail)
- Enter your email account password.
- Click the Output File field, which will open up a file browser (It may take a while on some systems)
- Select your output file
- Click the Proxy File field, which will open a file browser (It may take a while on some systems)
- Select your proxy file. It should be formatted as 1 proxy per line, eg:
    ```
    https://13.41.123.231:23123
    http://14.51.233.135:23321
    11.52.231.136:23321
    https://username:password@12.13.145.251:21313
    ```
  For more info on proxies see the FAQ [here](#faq-proxy-types)
- Click Create Accounts

![Create 10 accounts with random details, using 2Captcha, automatic verification, save to file, and use proxy list](images/10-random-2captcha-gmailav-proxies-save.jpg)

## <a name="faq"></a> FAQ

For more help, please ask on the [discord](https://discord.gg/RgWSqyU).

#### <a name="faq-proxy-types"></a> What proxy types are supported?
PalletTown currently supports the following proxy protocols:

- HTTP/HTTPS
- SOCKS4/SOCKS5

#### <a name="faq-proxy-sources"></a> Where can I find proxies to use with PalletTown?
PalletTown works best with private proxies. Public proxies often have unstable connections, and can even be banned by Niantic.
For best results, google "private proxies".

#### <a name="faq-plusmail"></a> What is a "plusmail compatible email address"?

A plusmail compatible email address is an email where emails sent to myaddress+123@domain.com get redirected to myaddress@domain.com.
This is a very useful feature as it allows the creation of many PTC accounts with a single email address.

Hotmail, Outlook, and Gmail support this feature, however Niantic is able to detect it being used in gmail addresses so it won't work for Pallet Town.

You can test if your email service supports plusmail like so:
    
For address myaddress@domain.com, send an email to myaddress+1234@domain.com, you should receive it in your email myaddress@domain.com.
If it doesn't arrive in your email, your email service does not support the plusmail trick, and you'll need a different email to use Pallet Town.

#### <a name="hotmail-forwarding"></a> How do I automatically forward emails from Hotmail to Gmail?

***NOTE: Hotmail to Gmail forwarding is no longer required! Simply use your hotmail address in the auto verification box***

##### If you've already received at least 1 activation email in your hotmail
1. Log into your hotmail account.
2. Click on an activation email from niantic
3. At the top of the window, click the three dots on the far right
![Three dot button](images/hotmail-threedots.png)
4. Click the "Create rule..." option
![Create rule option](images/hotmail-createrule.png)
5. Remove the middle condition so that only received from, and matches subject remain
![Forwarding conditions](images/hotmail-conditions.png)
6. Click the drop down under do all of the following, then "Forward, redirect or send", and then "Forward the message to..."
![Forward the message to](images/hotmail-forwardto.png)
7. Enter your gmail address in the box that appears, and click ok, then ok again to create the rule.

Done!

##### If you haven't received at least 1 activation email in your hotmail

TBA