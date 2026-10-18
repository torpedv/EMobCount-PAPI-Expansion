EMobCountExpansion 1.0.1 — external PlaceholderAPI expansion

Compatibility target: Purpur 1.21.8, PlaceholderAPI 2.12.2, MythicMobs 5.11.2.

Installation:
1. Remove the older EMobCountExpansion-1.0.0.jar from plugins/PlaceholderAPI/expansions/.
2. Put EMobCountExpansion-1.0.1.jar into plugins/PlaceholderAPI/expansions/.
3. Restart the server.
4. Check /papi list and test with /papi parse me %emobcount_global_MobInternalID%.

Placeholders:
%emobcount_global_<MythicMobInternalID>%
%emobcount_world_<worldName>_<MythicMobInternalID>%

Examples:
%emobcount_global_Dragon%
%emobcount_world_endoria_Dragon%

World names and MythicMob IDs are matched case-insensitively. The world placeholder matches the longest loaded world-name prefix, so world names containing underscores can be used if that world is loaded.

Counts active MythicMobs whose Bukkit entity is valid and not dead. The jar has no Bukkit plugin.yml; it is an external PlaceholderAPI expansion, not a standalone server plugin.

Version 1.0.1 fixes the Bukkit PluginManager.getPlugin(String) method descriptor used by the previous build. It was compiled locally against API stubs and has not been runtime-tested on a live server. If another startup error occurs, remove this jar and share the error.
