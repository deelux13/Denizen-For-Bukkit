package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumesScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player consumes item
    // player consumes <item>
    //
    // @Regex ^on player consumes [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a player consumes an item.
    //
    // @Context
    // <context.item> returns the dItem.
    //
    // @Determine
    // dItem to change the item being consumed.
    //
    // -->

    public PlayerConsumesScriptEvent() {
        instance = this;
    }

    public static PlayerConsumesScriptEvent instance;

    public dItem item;
    public PlayerItemConsumeEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player consumes");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String iCheck = path.eventArgLowerAt(2);
        return tryItem(item, iCheck) && runInCheck(path, event.getPlayer().getLocation());
    }

    @Override
    public String getName() {
        return "PlayerConsumes";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        if (dItem.matches(determination)) {
            dItem newitem = dItem.valueOf(determination, dEntity.getPlayerFrom(event.getPlayer()), null);
            if (newitem != null) {
                event.setItem(newitem.getItemStack());
                return true;
            }
            else {
                dB.echoError("Invalid event 'item' check [" + getName() + "] ('determine item ????'): '" + determination + "' for " + container.getName());
            }

        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        // TODO: Store the player / npc?
        return new BukkitScriptEntryData(event != null ? dEntity.getPlayerFrom(event.getPlayer()) : null, null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("item")) {
            return item;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerConsumes(PlayerItemConsumeEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        item = new dItem(event.getItem());
        this.event = event;
        fire(event);
    }
}
