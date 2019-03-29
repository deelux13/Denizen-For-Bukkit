package net.aufdemrand.denizen.events.player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

public class PlayerAnimatesScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player animates (<animation>)
    //
    // @Regex ^on player animates [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a player performs an animation.
    //
    // @Context
    // <context.animation> returns the name of the animation.
    //
    // -->

    public PlayerAnimatesScriptEvent() {
        instance = this;
    }

    public static PlayerAnimatesScriptEvent instance;
    public String animation;
    private dLocation location;
    public PlayerAnimationEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player animates");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (dEntity.isNPC(event.getPlayer())) {
            return false;
        }

        String ani = path.eventArgLowerAt(2);
        if (ani.length() > 0 && !ani.equals("in") && !ani.equalsIgnoreCase(animation)) {
            return false;
        }

        return runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "PlayerAnimates";
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
        if (name.equals("animation")) {
            return new Element(animation);
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerAnimates(PlayerAnimationEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        location = new dLocation(event.getPlayer().getLocation());
        animation = event.getAnimationType().name();
        this.event = event;
        fire(event);
    }
}
