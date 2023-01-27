# 1Password Login

Log into your account using the 1Password Command Line Interface (CLI)

## How it works

This plugin is using the 1Password Command Line Interface (CLI) to enter your password for you, just
like it is done in with the 1Password browser plugin or mobile app.

To do this we need to know where you store your OSRS login credentials in 1Password.

## Security considerations

* Your user ID and the item ID will be uploaded the runelites server.

1Password is designed to minimize the damage to its user in the case of a breach of an attack.
As only the IDs of the account and the item are stored using this plugin will leak no usable
information to a potential hacker.

* You must trust the author of this plugin to not include malicious code

As long as you are using the official runelite client, you will be using a plugin
reviewed the runelite team.
Essentially, if you trust the other community plugins to not be malicious, then you should trust
this plugin.

## First time set up

1. install the 1Password CLI
    * https://developer.1password.com/docs/cli/
    * **On Windows:** You'll need
      to [turn on unlock with Windows Hello](https://support.1password.com/windows-hello/) in the
      1Password app before you can use it with the 1Password CLI
2. Sign in to the 1Password CLI with `op signin`
3. Retrieve your `User ID` with `op whoami` and enter it into the `1Password User ID`
   1Password-login plugin settings field.
4. Retrieve the id of your OSRS item.
    1. Find the title of your OSRS vault item (For example `RuneScape`)
    2. Execute the command ` op item get <item-name> --format json` where `<item-name>` is the title
       of your osrs vault item.
    3. In the response look for a field called `id` consisting of ~25 random lowercase characters (
       for example `axn4re6a65epre2tw4de6mezfo`).
    4. Enter the found `id` into the `1Password RuneScape Item`  1Password-login plugin settings
       field.
5. Automatically log into your account!