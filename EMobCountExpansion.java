package ru.endoria.emobcount;

import java.util.Collection;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/** External PlaceholderAPI expansion for counting living MythicMobs. */
public final class EMobCountExpansion extends PlaceholderExpansion {
    @Override public String getIdentifier() { return "emobcount"; }
    @Override public String getAuthor() { return "Endoria"; }
    @Override public String getVersion() { return "1.0.1"; }
    @Override public String getRequiredPlugin() { return "MythicMobs"; }
    @Override public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin("MythicMobs") != null;
    }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params == null) return null;
        if (params.regionMatches(true, 0, "global_", 0, 7)) {
            String mobId = params.substring(7);
            return mobId.isBlank() ? "0" : Integer.toString(count(mobId, null));
        }
        if (params.regionMatches(true, 0, "world_", 0, 6)) {
            String worldAndMob = params.substring(6);
            World match = null;
            String mobId = null;
            for (World world : Bukkit.getWorlds()) {
                String prefix = world.getName() + "_";
                if (worldAndMob.length() > prefix.length()
                        && worldAndMob.regionMatches(true, 0, prefix, 0, prefix.length())
                        && (match == null || world.getName().length() > match.getName().length())) {
                    match = world;
                    mobId = worldAndMob.substring(prefix.length());
                }
            }
            return match == null || mobId == null || mobId.isBlank()
                    ? "0" : Integer.toString(count(mobId, match));
        }
        return null;
    }

    private int count(String requestedMobId, World onlyWorld) {
        try {
            Class<?> mythicBukkitClass = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            Object mythic = mythicBukkitClass.getMethod("inst").invoke(null);
            Object manager = mythic.getClass().getMethod("getMobManager").invoke(mythic);
            Object active = manager.getClass().getMethod("getActiveMobs").invoke(manager);
            if (!(active instanceof Collection<?> mobs)) return 0;
            int total = 0;
            for (Object mob : mobs) {
                if (mob == null) continue;
                String typeId = getMobTypeId(mob);
                if (typeId == null || !typeId.equalsIgnoreCase(requestedMobId)) continue;
                Entity entity = getBukkitEntity(mob);
                if (entity == null || entity.isDead() || !entity.isValid()) continue;
                if (onlyWorld != null && !onlyWorld.equals(entity.getWorld())) continue;
                total++;
            }
            return total;
        } catch (ReflectiveOperationException | LinkageError ex) {
            Bukkit.getLogger().warning("[EMobCount] Could not count MythicMobs: "
                    + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return 0;
        }
    }

    private String getMobTypeId(Object activeMob) throws ReflectiveOperationException {
        Object type;
        try {
            type = activeMob.getClass().getMethod("getType").invoke(activeMob);
        } catch (NoSuchMethodException ex) {
            type = activeMob.getClass().getMethod("getMobType").invoke(activeMob);
        }
        if (type == null) return null;
        if (type instanceof String s) return s;
        for (String method : new String[] {"getInternalName", "getName", "getMobType"}) {
            try {
                Object value = type.getClass().getMethod(method).invoke(type);
                if (value != null) return value.toString();
            } catch (NoSuchMethodException ignored) { }
        }
        return type.toString();
    }

    private Entity getBukkitEntity(Object activeMob) throws ReflectiveOperationException {
        Object abstractEntity = activeMob.getClass().getMethod("getEntity").invoke(activeMob);
        if (abstractEntity == null) return null;
        try {
            Object entity = abstractEntity.getClass().getMethod("getBukkitEntity").invoke(abstractEntity);
            return entity instanceof Entity e ? e : null;
        } catch (NoSuchMethodException ex) {
            return abstractEntity instanceof Entity e ? e : null;
        }
    }
}
