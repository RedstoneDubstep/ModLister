[![](http://cf.way2muchnoise.eu/full_modlistobserver_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/modlistobserver) [![](http://cf.way2muchnoise.eu/versions/For%20MC_modlistobserver_all.svg)](https://www.curseforge.com/minecraft/mc-mods/modlistobserver/files)

ModListObserver
=============

Source code for the Forge mod "ModListObserver".

This mod enables server admins to observe what mods clients joined the server with. Insight is granted via the added /modlist command, as well as an optional (enabled by default) message containing the current mod list that gets logged when a player joins the server.

**Command usage:**

    /modlist <target> current     gives you the mod list the target(s) last joined the server with
    /modlist <target> all         gives you the list of all mods the target(s) joined the server with since the last server restart

This is a Forge mod, a Fabric port may happen in the future (if there is demand).

This mod is serverside only (meaning that this mod only needs to be installed on the server to work).

**As a final note:** 
The mod list observable by ModListObserver is not authorative. A mod missing from the list does not necessarily mean the mod isn't there, and similarly a mod present in the list does not necessarily mean it is there. People using hacked clients may and will hack their mod list to make it look unsuspicious. Because of this, this mod will never add a feature to automatically blacklist players with certain mod lists, its sole purpose is to help admins with their decision of whether a player is using a disallowed mod.
