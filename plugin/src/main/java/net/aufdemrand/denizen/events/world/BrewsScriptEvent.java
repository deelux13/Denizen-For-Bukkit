package net.aufdemrand.denizen.events.world;

import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dInventory;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

public class BrewsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // brewing stand brews
    //
    // @Regex ^on brewing stand brews$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a brewing stand brews a potion.
    //
    // @Context
    // <context.location> returns the dLocation of the brewing stand.
    // <context.inventory> returns the dInventory of the brewing stand's contents.
    //
    // -->

    public BrewsScriptEvent() {
        instance = this;
    }

    public static BrewsScriptEvent instance;
    public dInventory inventory;
    public dLocation location;
    public BrewEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("brewing stand brews");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "Brews";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("inventory")) {
            return inventory;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onBrews(BrewEvent event) {
        location = new dLocation(event.getBlock().getLocation());
        inventory = dInventory.mirrorBukkitInventory(event.getContents());
        this.event = event;
        fire(event);
    }
}
