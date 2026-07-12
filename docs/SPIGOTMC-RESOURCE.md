[CENTER][SIZE=7][B]XyKit[/B][/SIZE]
[SIZE=4]Starter Kits, Redeemable CDK Codes and Safe YAML Data Management[/SIZE][/CENTER]

[COLOR=#ff4d4d][B]Language notice:[/B][/COLOR] The SpigotMC resource page, documentation and support channel are English-only. Chinese-language support is not provided on SpigotMC. The plugin's default configuration comments, player messages, administrator feedback and console messages are currently Chinese.

[SIZE=5][B]About XyKit[/B][/SIZE]
XyKit is a lightweight kit and redemption-code plugin for Paper servers. Administrators define starter or CDK kits in YAML and choose whether each reward action runs as the player, the console, a temporary operator command, a direct message or a broadcast.

Starter-kit claims are recorded by player UUID to prevent repeat claims. CDK codes have configurable random formats, are checked for uniqueness and can be created individually or in batches with a maximum-use count.

[SIZE=5][B]Compatibility[/B][/SIZE]
[LIST]
[*][B]Server software:[/B] Paper 26.2.x
[*][B]Java:[/B] 25 or newer
[*][B]Plugin version:[/B] 1.3.3
[*][B]API dependency:[/B] Paper API 26.2 is provided by the server and is not bundled
[*][B]Server testing:[/B] The 1.3.3 Paper 26.2 update was built successfully; no additional live Paper 26.2 server test was performed for that update
[*]Spigot, Purpur and other server implementations have not been tested and are not claimed as supported
[/LIST]

[SIZE=5][B]Free Resource[/B][/SIZE]
XyKit should be published as a [B]free resource[/B] at [B]$0.00[/B]. It is a small, self-contained utility released under the MIT License, with its complete source code and release binaries publicly available on GitHub. It has no paid service, premium dependency, license server or feature suitable for a separate paid edition.

[SIZE=5][B]Main Features[/B][/SIZE]
[LIST]
[*]One-time starter kits tracked separately for each player UUID and kit name
[*]Redeemable CDK kits with per-code maximum-use limits
[*]Single or batch CDK generation, up to 100 codes per command
[*]Configurable CDK length and character set
[*]In-memory and YAML duplicate-code checks
[*]Player, console, temporary-operator, message and broadcast reward actions
[*]Administrative CDK statistics and exhausted-code cleanup
[*]Configuration and data reload command
[*]Automatic rolling backup before YAML data saves
[*]Timestamped manual backups and guarded restore workflow
[/LIST]

[SIZE=5][B]Claim and Redemption Safety[/B][/SIZE]
Starter claims are stored under each player's UUID, so changing a player name does not reset claim history. CDK records store the linked kit, maximum uses, current uses and creation timestamp. A code is validated against its assigned kit before its use counter is increased and saved.

[SIZE=5][B]Data and Backup Model[/B][/SIZE]
All persistent data is stored in [ICODE]plugins/XyKit/data.yml[/ICODE]. Before saving, XyKit replaces [ICODE]data.yml.backup[/ICODE] with a copy of the current data file. Administrators can also create timestamped manual backup files with [ICODE]/kit backup[/ICODE]. Stop the server and keep an external copy of the plugin directory before major upgrades or bulk data operations.

[SIZE=5][B]Flexible Reward Actions[/B][/SIZE]
Kit actions support [ICODE]{player}[/ICODE] replacement. Use an unprefixed command for player execution, [ICODE]cmd:[/ICODE] for console execution, [ICODE]op:[/ICODE] for temporary operator execution, [ICODE]msg [/ICODE] for a private message and [ICODE]broadcast:[/ICODE] for a server-wide message. Only trusted administrators should be allowed to edit kit commands.

[SIZE=5][B]Dependencies[/B][/SIZE]
[LIST]
[*][B]Required plugins:[/B] None
[*][B]Bundled third-party libraries:[/B] None
[*][B]Server-provided API:[/B] Paper API
[/LIST]

[SIZE=5][B]Links[/B][/SIZE]
[LIST]
[*][URL=https://github.com/yangzijian52/xykit]Source Code[/URL]
[*][URL=https://github.com/yangzijian52/xykit/releases]Downloads[/URL]
[*][URL=https://github.com/yangzijian52/xykit/issues]English Support and Bug Reports[/URL]
[*][URL=https://github.com/yangzijian52/xykit/blob/main/LICENSE]MIT License[/URL]
[*][URL=https://github.com/yangzijian52/xykit/blob/main/docs/SPIGOTMC-RESOURCE-BBCODE.txt]Full Documentation[/URL]
[/LIST]

[SIZE=5][B]Important Notes[/B][/SIZE]
[LIST]
[*]The runtime interface and bundled configuration comments are currently Chinese even though the SpigotMC listing and support channel are English-only.
[*]Only Paper 26.2.x with Java 25 or newer is claimed as supported.
[*]The current implementation always uses [ICODE]data.yml[/ICODE]. The [ICODE]database.mysql[/ICODE] example settings do not enable a MySQL backend.
[*]The configured [ICODE]cooldown[/ICODE] value is loaded but is not currently applied; starter kits remain one-time claims.
[*][ICODE]/kit create[/ICODE] does not write a new kit automatically; it instructs the administrator to edit [ICODE]config.yml[/ICODE].
[*]The [ICODE]op:[/ICODE] action temporarily grants operator status. Use it only with trusted, reviewed commands.
[*]Back up [ICODE]plugins/XyKit/data.yml[/ICODE] before replacing the jar or editing stored data.
[/LIST]
