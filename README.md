# ServerSystem

# Developers: I recommend you using my [setup.sh file](https://raw.githubusercontent.com/TheBlackEntity/ServerSystem/master/setup.sh) it is compatible with Windows 8+ through `Git Bash` too. But I recommend using `WSL 2` (`Windows Subsystem Linux 2` included in Windows 10+)

\~~~~~~~~~~~~~~~~~~~~~~ English ~~~~~~~~~~~~~~~~~~~~~~

You want to know what I'm currently working on?

Here's my todo list:
https://trello.com/b/mMbYQANl/serversystem-todo-list

This plugin is supposed to be an alternative to essentials.
It aims to be even better than essentials (for example you can set every single message and permission by yourself).

Does this plugin need any dependencies?
It has some dependencies, but they are all optional: Vault, PlotSquared and PlaceholderAPI.

Why an alternative to essentials?
First, essentials doesn't look all that good out of the box.
Also you can set every single message and permission.
Plus this plugin has some useful functions that are NOT included in essentials.

What commands will this plugin add?

[You can download the list of commands here](https://www.dropbox.com/s/62f56n2flw8pvbe/Command_Reference.pdf?dl=0)

What other cool features does this plugin have?
You can let it "hand over" commands to other plugins.
For example:
You want to use another plugin's /ban, but my plugin overrides the commands, you can let that happen!

Also you can just disable commands, if you like.

<details><summary>What placeholders does it add? (PlaceholderAPI)</summary>
<pre>

%serversystem_money% -> Shows the unformatted balance

%serversystem_formattedmoney% -> Shows the formatted balance

%serversystem_drop% -> Shows if the player can drop items in vanish

%serversystem_pickup% -> Shows if the player can pick up items in vanish

%serversystem_chat% -> Shows if the place can chat in vanish

%serversystem_interact% -> Shows if the player can interact in vanish

%serversystem_vanish% -> Shows if the player is in vanish

%serversystem_god% -> Shows if the player is in god mode

%serversystem_onlineplayers% -> Shows online player count, excluding players you cannot see (Aka. vanish)

%baltop_formattedmoney_X% -> Shows the formatted balance of top place X (1 - 10)

%baltop_money_X% -> Shows the unformatted balance of top place X (1 - 10)

%baltop_player_X% -> Shows the player name of top place X (1 - 10)

</pre>
</details>

How do I install this plugin?

Just put it into you plugins folder

I found a bug or need help configuring the plugin, what do I do?

You can always join on this discord: [https://discord.gg/dBhfCzdZxq](https://discord.gg/GxEFhVY6ff) and ask me some
questions.