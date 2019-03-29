package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRiptideEvent;

public class PlayerRiptideScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player activates riptide
    //
    // @Regex ^on player activates riptide$
    // @Switch in <area>
    //
    // @Cancellable false
    //
    // @Triggers when a player activates the riptide effect.
    //
    // @Context
    // <context.item> returns the dItem of the trident.
    //
    // -->

    public PlayerRiptideScriptEvent() {
        instance = this;
    }

    public static PlayerRiptideScriptEvent instance;
    public PlayerRiptideEvent event;
    private dItem item;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player activates riptide");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return runInCheck(path, event.getPlayer().getLocation());
    }

    @Override
    public String getName() {
        return "PlayerActivatesRiptide";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new dPlayer(event.getPlayer()), null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("item")) {
            return item;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerRiptide(PlayerRiptideEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        this.item = new dItem(event.getItem());
        this.event = event;
        fire();
    }
}
