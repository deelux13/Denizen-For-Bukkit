package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EntityTeleportScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity teleports
    // <entity> teleports
    //
    // @Regex ^on [^\s]+ teleports$
    // @Switch in <area>
    //
    // @Triggers when an entity teleports.
    //
    // @Cancellable true
    //
    // @Context
    // <context.entity> returns the dEntity.
    // <context.origin> returns the dLocation the entity teleported from.
    // <context.destination> returns the dLocation the entity teleported to.
    // <context.cause> returns an Element of the teleport cause. Can be:
    // COMMAND, END_PORTAL, ENDER_PEARL, NETHER_PORTAL, PLUGIN, END_GATEWAY, CHORUS_FRUIT, SPECTATE, UNKNOWN, or ENTITY_TELEPORT
    //
    // @Determine
    // "ORIGIN:" + dLocation to change the location the entity teleported from.
    // "DESTINATION:" + dLocation to change the location the entity teleports to.
    //
    // @Player when the entity being teleported is a player.
    //
    // @NPC when the entity being teleported is an NPC.
    //
    // -->

    public EntityTeleportScriptEvent() {
        instance = this;
    }

    public static EntityTeleportScriptEvent instance;
    public dEntity entity;
    public dLocation from;
    public dLocation to;
    public String cause;
    public EntityTeleportEvent event;
    public PlayerTeleportEvent pEvent;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return CoreUtilities.xthArgEquals(1, lower, "teleports");
    }

    @Override
    public boolean matches(ScriptPath path) {

        if (!tryEntity(entity, path.eventArgLowerAt(0))) {
            return false;
        }

        if (!runInCheck(path, from)) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "EntityTeleports";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        String dlow = CoreUtilities.toLowerCase(determination);
        if (dlow.startsWith("origin:")) {
            dLocation new_from = dLocation.valueOf(determination.substring("origin:".length()));
            if (new_from != null) {
                from = new_from;
                return true;
            }
        }
        else if (dlow.startsWith("destination:")) {
            dLocation new_to = dLocation.valueOf(determination.substring("destination:".length()));
            if (new_to != null) {
                to = new_to;
                return true;
            }
        }
        else if (dLocation.matches(determination)) {
            dLocation new_to = dLocation.valueOf(determination);
            if (new_to != null) {
                to = new_to;
                return true;
            }
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        // TODO: Store the player / npc?
        return new BukkitScriptEntryData(pEvent != null ? dEntity.getPlayerFrom(pEvent.getPlayer()) : null,
                entity.isCitizensNPC() ? entity.getDenizenNPC() : null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("origin")) {
            return from;
        }
        else if (name.equals("destination")) {
            return to;
        }
        else if (name.equals("entity")) {
            return entity.getDenizenObject();
        }
        else if (name.equals("cause")) {
            return new Element(cause);
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onEntityTeleports(EntityTeleportEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        to = new dLocation(event.getTo());
        from = new dLocation(event.getFrom());
        entity = new dEntity(event.getEntity());
        cause = "ENTITY_TELEPORT";
        this.event = event;
        pEvent = null;
        fire(event);
        event.setFrom(from);
        event.setTo(to);
    }

    @EventHandler
    public void onPlayerTeleports(PlayerTeleportEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        from = new dLocation(event.getFrom());
        to = new dLocation(event.getTo());
        entity = new dEntity(event.getPlayer());
        cause = event.getCause().name();
        this.event = null;
        pEvent = event;
        fire(event);
        event.setFrom(from);
        event.setTo(to);
    }
}
