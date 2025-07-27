# ServerSystem

A comprehensive Minecraft server management plugin for Paper servers, providing essential administrative, moderation,
and utility commands.

In short, this is an alternative to plugins like Essentials.

## Why?

While Essentials is a fine plugin, more options and competition is always a good thing.<br>
This plugin also aims to be much more customizable and (mostly) be a drop-in solution.

## Features

- **Administrative Tools**: Gamemode management, teleportation, time/weather control
- **Economy System**: Balance management, payments, economy administration (requires Vault)
- **Moderation Tools**: Ban/mute system, command spy, vanish, clear chat
- **Utility Tools**: Repair tools, trash can, heal, feed
- **Player Utilities**: Homes, warps, kits, mobile workbench access
- **Teleportation System**: TPA requests, back command, spawn management
- **Communication**: Private messaging, team chat, broadcast system
- **Clickable Signs**: Interactive signs for various functions
- **Multi-language Support**: Per-User language support, defaulting to player's locale (English, German and Slovene
  only)

## Requirements

- **Minecraft Version**: 1.21+
- **Server Software**: Paper (or Paper-based forks)
- **Java Version**: 21+
- **Optional Dependencies**:
    - Vault (Provides a universal Economy API for other plugins to use)
    - NBTAPI (For loading/saving offline player data)

## Installation

1. Download the latest ServerSystem JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin using the generated configuration files
5. Reload or restart the server to apply changes

## Configuration

The plugin generates several configuration files:

- `config.yml` - Contains configuration for sections that are either too small or too generic
- `commands.yml` - Command settings and aliases
- `permissions.yml` - Permission definitions
- `economy.yml` - Economy related settings
- `moderation.yml` Moderation (Ban/Mute) related settings
- `messages/` - Language files for different locales

## Commands

(xy) = optional argument<br>
\[xy] = required argument<br>

### Administrative Commands

| Command                      | Aliases      | Description                       | Permission                                      |
|------------------------------|--------------|-----------------------------------|-------------------------------------------------|
| `/gamemode [mode] (player)`  | `/gm`        | Change gamemode                   | `serversystem.admin.command.gamemode.use`       |
| `/gms (player)`              | -            | Set survival mode                 | `serversystem.admin.command.gamemode.survival`  |
| `/gmc (player)`              | -            | Set creative mode                 | `serversystem.admin.command.gamemode.creative`  |
| `/gma (player)`              | -            | Set adventure mode                | `serversystem.admin.command.gamemode.adventure` |
| `/gmsp (player)`             | -            | Set spectator mode                | `serversystem.admin.command.gamemode.spectator` |
| `/time [time] (world)`       | -            | Set world time                    | `serversystem.admin.command.time.use`           |
| `/day (world)`               | -            | Set time to day                   | `serversystem.admin.command.time.use`           |
| `/night (world)`             | -            | Set time to night                 | `serversystem.admin.command.time.use`           |
| `/noon (world)`              | -            | Set time to noon                  | `serversystem.admin.command.time.use`           |
| `/midnight (world)`          | -            | Set time to midnight              | `serversystem.admin.command.time.use`           |
| `/weather [type] (world)`    | -            | Change weather                    | `serversystem.admin.command.weather.use`        |
| `/sun (world)`               | -            | Set sunny weather                 | `serversystem.admin.command.weather.use`        |
| `/rain (world)`              | -            | Set rainy weather                 | `serversystem.admin.command.weather.use`        |
| `/storm (world)`             | -            | Set stormy weather                | `serversystem.admin.command.weather.use`        |
| `/sudo [player] [command]`   | -            | Execute command as another player | `serversystem.admin.command.sudo.use`           |
| `/unlimited (player)`        | -            | Toggle unlimited items            | `serversystem.admin.command.unlimited.use`      |
| `/speed [1-10] (player)`     | -            | Set player speed                  | `serversystem.admin.command.speed.use`          |
| `/flyspeed [1-10] (player)`  | `/speedfly`  | Set fly speed                     | `serversystem.admin.command.speed.use`          |
| `/walkspeed [1-10] (player)` | `/speedwalk` | Set walk speed                    | `serversystem.admin.command.speed.use`          |

### Teleportation Commands

| Command                               | Aliases    | Description                           | Permission                                                |
|---------------------------------------|------------|---------------------------------------|-----------------------------------------------------------|
| `/teleport [player] (target)`         | `/tp`      | Teleport to player or location        | `serversystem.support.command.teleport.use`               |
| `/tppos [x] [y] [z] (world) (player)` | -          | Teleport to coordinates               | `serversystem.support.command.teleportposition.use`       |
| `/tphere [player]`                    | -          | Teleport player to you                | `serversystem.support.command.teleporthere.use`           |
| `/tpall (world)`                      | -          | Teleport all players to you           | `serversystem.admin.command.teleportall.use`              |
| `/tpa [player]`                       | -          | Request teleport to player            | `serversystem.command.teleportask.use`                    |
| `/tpahere [player]`                   | -          | Request player teleport to you        | `serversystem.command.teleporthereask.use`                |
| `/tpaccept`                           | -          | Accept teleport request               | `serversystem.command.teleportaccept.use`                 |
| `/tpdeny`                             | -          | Deny teleport request                 | `serversystem.command.teleportdeny.use`                   |
| `/tptoggle (player)`                  | -          | Toggle teleport requests              | `serversystem.command.teleporttoggle.use`                 |
| `/back`                               | -          | Return to previous location           | `serversystem.command.back.use`                           |
| `/offlinetp [player]`                 | `/otp`     | Teleport to offline player's location | `serversystem.moderation.command.offlineteleport.use`     |
| `/offlinetphere [player]`             | `/otphere` | Teleport offline player to you        | `serversystem.moderation.command.offlineteleporthere.use` |

### Economy Commands

| Command                                      | Aliases   | Description           | Permission                               |
|----------------------------------------------|-----------|-----------------------|------------------------------------------|
| `/balance (player)`                          | `/money`  | Check balance         | `serversystem.command.balance.use`       |
| `/balancetop`                                | `/baltop` | View top balances     | `serversystem.command.baltop.use`        |
| `/pay [player] [amount]`                     | -         | Send money to player  | `serversystem.command.pay.use`           |
| `/economy [set/give/take] [player] [amount]` | `/eco`    | Manage player economy | `serversystem.admin.command.economy.use` |

### Moderation Commands

| Command                          | Aliases         | Description                 | Permission                                           |
|----------------------------------|-----------------|-----------------------------|------------------------------------------------------|
| `/ban [player] (reason)`         | -               | Ban a player                | `serversystem.moderation.ban.use`                    |
| `/unban [player]`                | `/pardon`       | Unban a player              | `serversystem.moderation.ban.remove`                 |
| `/mute [player] (time) (reason)` | -               | Mute a player               | `serversystem.moderation.mute.use`                   |
| `/unmute [player]`               | -               | Unmute a player             | `serversystem.moderation.mute.remove`                |
| `/shadowmute [player]`           | -               | Shadow mute a player        | `serversystem.moderation.mute.shadow`                |
| `/vanish (player)`               | `/v`            | Toggle vanish mode          | `serversystem.moderation.command.vanish.use`         |
| `/commandspy (player)`           | -               | Toggle command spy          | `serversystem.moderation.command.commandspy.use`     |
| `/clearchat`                     | `/cc`           | Clear chat for all players  | `serversystem.moderation.command.clearchat.use`      |
| `/broadcast [message]`           | `/bc`           | Broadcast message to server | `serversystem.moderation.command.broadcast.use`      |
| `/clearinventory (player)`       | `/clear`, `/ci` | Clear player inventory      | `serversystem.moderation.command.clearinventory.use` |
| `/ip [player]`                   | -               | View player's IP address    | `serversystem.moderation.command.ip.use`             |

### Utility Commands

| Command                       | Aliases                | Description                       | Permission                                           |
|-------------------------------|------------------------|-----------------------------------|------------------------------------------------------|
| `/workbench`                  | `/craft`, `/wb`        | Open crafting table               | `serversystem.command.workbench.use`                 |
| `/anvil`                      | -                      | Open anvil interface              | `serversystem.command.anvil.use`                     |
| `/smithing`                   | -                      | Open smithing table               | `serversystem.command.smithing.use`                  |
| `/loom`                       | -                      | Open loom interface               | `serversystem.command.loom.use`                      |
| `/grindstone`                 | -                      | Open grindstone interface         | `serversystem.command.grindstone.use`                |
| `/cartography`                | -                      | Open cartography table            | `serversystem.command.cartography.use`               |
| `/stonecutter`                | -                      | Open stonecutter interface        | `serversystem.command.stonecutter.use`               |
| `/disposal`                   | `/trash`, `/trashcan`  | Open disposal interface           | `serversystem.command.disposal.use`                  |
| `/enderchest (player)`        | `/ec`                  | Open ender chest                  | `serversystem.command.enderchest.use`                |
| `/offlineenderchest [player]` | `/oenderchest`, `/oec` | Open offline player's ender chest | `serversystem.admin.command.offlineenderchest.use`   |
| `/invsee [player]`            | -                      | View player's inventory           | `serversystem.command.inventorysee.use`              |
| `/offlineinvsee [player]`     | `/oinvsee`             | View offline player's inventory   | `serversystem.admin.command.offlineinventorysee.use` |
| `/smelt`                      | -                      | Smelt items in inventory          | `serversystem.command.smelt.use`                     |
| `/stack`                      | -                      | Stack items in inventory          | `serversystem.admin.command.stack.use`               |
| `/repair (all)`               | -                      | Repair items in hand or all       | `serversystem.admin.command.repair.use`              |
| `/skull (player)`             | -                      | Get player skull                  | `serversystem.admin.command.skull.use`               |
| `/ping (player)`              | -                      | Check ping                        | `serversystem.command.ping.use`                      |
| `/seen [player]`              | -                      | Check when player was last online | `serversystem.support.command.seen.use`              |
| `/god (player)`               | -                      | Toggle god mode                   | `serversystem.support.command.god.use`               |
| `/fly (player)`               | -                      | Toggle flight                     | `serversystem.support.command.fly.use`               |
| `/heal (player)`              | -                      | Heal player                       | `serversystem.support.command.heal.use`              |
| `/feed (player)`              | -                      | Feed player                       | `serversystem.support.command.feed.use`              |

### Home & Warp Commands

| Command                         | Aliases | Description               | Permission                                    |
|---------------------------------|---------|---------------------------|-----------------------------------------------|
| `/spawn (player)`               | -       | Teleport to spawn         | `serversystem.command.spawn.use`              |
| `/setspawn`                     | -       | Set server spawn          | `serversystem.admin.command.spawn.set`        |
| `/home [name]`                  | -       | Teleport to home          | `serversystem.command.home.use`               |
| `/sethome [name]`               | -       | Set home location         | `serversystem.command.home.set`               |
| `/delhome [name]`               | -       | Delete home               | `serversystem.command.home.delete`            |
| `/adminhome [player] [name]`    | -       | Teleport to player's home | `serversystem.moderation.command.home.use`    |
| `/adminsethome [player] [name]` | -       | Set home for player       | `serversystem.moderation.command.home.set`    |
| `/admindelhome [player] [name]` | -       | Delete player's home      | `serversystem.moderation.command.home.delete` |
| `/warp [name]`                  | -       | Teleport to warp          | `serversystem.command.warp.use`               |
| `/setwarp [name]`               | -       | Create warp               | `serversystem.admin.command.warp.set`         |
| `/delwarp [name]`               | -       | Delete warp               | `serversystem.admin.command.warp.delete`      |

### Communication Commands

| Command                   | Aliases             | Description              | Permission                                  |
|---------------------------|---------------------|--------------------------|---------------------------------------------|
| `/msg [player] [message]` | `/tell`, `/whisper` | Send private message     | `serversystem.command.privatemessage.use`   |
| `/reply [message]`        | `/r`                | Reply to last message    | `serversystem.command.privatemessage.use`   |
| `/msgtoggle (player)`     | -                   | Toggle private messages  | `serversystem.command.messagetoggle.use`    |
| `/teamchat [message]`     | `/tc`               | Send team chat message   | `serversystem.support.command.teamchat.use` |
| `/ignore [player]`        | -                   | Ignore player messages   | `serversystem.command.ignore.use`           |
| `/unignore [player]`      | -                   | Unignore player messages | `serversystem.command.unignore.use`         |

### Kit Commands

| Command                | Aliases | Description               | Permission                              |
|------------------------|---------|---------------------------|-----------------------------------------|
| `/kit [name] (player)` | -       | Give kit to player        | `serversystem.command.kit.use`          |
| `/createkit [name]`    | -       | Create kit from inventory | `serversystem.admin.command.kit.create` |
| `/deletekit [name]`    | -       | Delete kit                | `serversystem.admin.command.kit.delete` |

### Miscellaneous Commands

| Command              | Aliases | Description                        | Permission                                   |
|----------------------|---------|------------------------------------|----------------------------------------------|
| `/language [lang]`   | -       | Change language                    | `serversystem.command.language.use`          |
| `/signcost`          | -       | Manage sign costs                  | `serversystem.admin.command.signcost.use`    |
| `/drop (player)`     | -       | Toggle item dropping in vanish     | `serversystem.moderation.command.vanish.use` |
| `/pickup (player)`   | -       | Toggle item pickup in vanish       | `serversystem.moderation.command.vanish.use` |
| `/interact (player)` | -       | Toggle block interaction in vanish | `serversystem.moderation.command.vanish.use` |
| `/message (player)`  | -       | Toggle messaging ability in vanish | `serversystem.moderation.command.vanish.use` |

## Permissions

ServerSystem groups permissions into the following main categories:

- `serversystem.admin.*` - Full administrative access
- `serversystem.moderation.*` - Moderation tools
- `serversystem.support.*` - Support/helper permissions
- `serversystem.command.*` - Basic command permissions

Most permissions suitable for everyday player usage are already given by default.<br>
This means that players can use these commands out of the box (e.g. `/home`)<br>
You can set any permission to (not) be required inside the `permissions.yml` file.<br>

### Permission Levels

- **Admin**: Full server control (gamemode, time, weather, economy, etc.)
- **Moderation**: Player management (ban, mute, vanish, etc.)
- **Support**: Helper tools (teleport, heal, god mode, etc.)
- **Player**: Basic commands (homes, warps, messaging, etc.)

### Special Permissions

- `serversystem.homes.unlimited` - Unlimited homes
- `serversystem.homes.<number>` - Specific home limit
- `serversystem.chat.colored` - Use color codes in chat
- `serversystem.command.teleportask.instant` - Instant teleport without delay

## Clickable Signs

The plugin supports interactive signs for various functions:

- **Give Signs**: Give items to players
- **Kit Signs**: Provide kits to players
- **Time Signs**: Change server time
- **Weather Signs**: Change server weather
- **Warp Signs**: Teleport to warps

Each sign type has create/destroy permissions for administrators.

## Data Migration

While Pre-Rewrite (2.x.x) ServerSystem supported migrating
from or to Essentials, 3.x.x, as of 18. July 2025, does not.<br>
The reason for this is simple. I just didn't get to it before I deemed the plugin Release-Ready.

## PlaceholderAPI

This plugin has support for PlaceholderAPI. You can use it's placeholders in
ServerSystem's messages or use the ones ServerSystem adds:

- `%serversystem_onlineplayers%` -> Shows the online player count, excluding vanished players,
  if the target cannot see vanished players
- `%serversystem_balance%` (`%serversystem_balance_<playername>%`) -> Shows the formatted balance for a player
- `%serversystem_unformattedbalance%` (`%serversystem_unformattedbalance_<playername>%`) -> Shows the *unformatted*
  balance for a player
- `%serversystem_baltop_name_<number>%` -> Shows the name of place <number> in baltop (1 - 10 only!)
- `%serversystem_baltop_balance_<number>%` -> Shows the formatted balance of place <number> in baltop (1 - 10 only!)
- `%serversystem_baltop_unformattedsbalance_<number>%` -> Shows the *unformatted*
  balance of place <number> in baltop (1 - 10 only!)

## Building

### Prerequisites

- Java 21 or higher
- Maven 3.8.0 or higher

### Build Instructions

```bash
git clone https://github.com/Test-Account666/ServerSystem
cd ServerSystem
mvn paper-nms:init -f pom.xml # Not required for consecutive builds
mvn clean package
```

### Misc Developer Information

<details>
<summary>Misc Information</summary>

This plugin uses Manifold for String interpolation, e.g.:

```pseudo
var world = "World!"
print("Hello ${world}")
```

This plugin also uses Lombok for reducing boilerplate code, e.g.:

```pseudo
@Getter
@Setter
private string helloWorld = "Hello World!"

public void printHelloWorld() {
    print(getHelloWorld())
}
```

vs

```pseudo
private string helloWorld = "Hello World!"

public void setHelloWorld(string value) {
    helloWorld = value
}

public string getHelloWorld() {
    return helloWorld
}

public void printHelloWorld() {
    print(getHelloWorld())
}
```

The variable naming conventions are enforced by CheckStyle and use C#-ish conventions.

You may need to install the mentioned IDE-Plugins to work with this project properly.

</details>

## Support

For bug reports, please visit the [issue-tracker](https://github.com/Test-Account666/ServerSystem/issues).<br>
For feature or support requests, please visit
the [discussions](https://github.com/Test-Account666/ServerSystem/discussions).<br>
I also have a [discord server](https://discord.gg/GxEFhVY6ff) for general plugin and minecraft support<br>

## Auto Updates

As a user, you can disable Auto-Updates via the `config.yml`.

If you are a server hoster or just want to use JVM Flags, you can do the same like this:
`java -Dserversystem.disable-auto-download=true -jar server.jar`

## License

This project is licensed under the terms specified in the LICENSE file.
