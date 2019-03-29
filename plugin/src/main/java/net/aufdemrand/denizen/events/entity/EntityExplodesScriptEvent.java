package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodesScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity explodes
    // <entity> explodes
    //
    // @Regex ^on [^\s]+ explodes$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when an entity explodes.
    //
    // @Context
    // <context.blocks> returns a dList of blocks that the entity blew up.
    // <context.entity> returns the dEntity that exploded.
    // <context.location> returns the dLocation the entity blew up at.
    // <context.strength> returns an Element(Decimal) of the strength of the explosion.
    //
    // @Determine
    // dList(dLocation) to set a new lists of blocks that are to be affected by the explosion.
    // Element(Decimal) to change the strength of the explosion.
    //
    // -->

    public EntityExplodesScriptEvent() {
        instance = this;
    }

    public static EntityExplodesScriptEvent instance;
    public dEntity entity;
    public dList blocks;
    public dLocation location;
    public float strength;
    private Boolean blockSet;
    public EntityExplodeEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.getXthArg(1, CoreUtilities.toLowerCase(s)).equals("explodes");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String target = path.eventArgLowerAt(0);

        if (!tryEntity(entity, target)) {
            return false;
        }

        if (!runInCheck(path, entity.getLocation())) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "EntityExplodes";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        if (aH.matchesDouble(determination)) {
            strength = aH.getFloatFrom(determination);
            return true;
        }
        if (dList.matches(determination)) {
            blocks = new dList();
            blockSet = true;
            for (String loc : dList.valueOf(determination)) {
                dLocation location = dLocation.valueOf(loc);
                if (location == null) {
                    dB.echoError("Invalid location '" + loc + "' check [" + getName() + "]: '  for " + container.getName());
                }
                else {
                    blocks.add(location.identifySimple());
                }
            }
            return true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(entity.isPlayer() ? dEntity.getPlayerFrom(event.getEntity()) : null,
                entity.isCitizensNPC() ? dEntity.getNPCFrom(event.getEntity()) : null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("entity")) {
            return entity;
        }
        else if (name.equals("location")) {
            return location;
        }
        else if (name.equals("blocks")) {
            return blocks;
        }
        else if (name.equals("strength")) {
            return new Element(strength);
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onEntityExplodes(EntityExplodeEvent event) {
        entity = new dEntity(event.getEntity());
        location = new dLocation(event.getLocation());
        strength = event.getYield();
        blocks = new dList();
        blockSet = false;
        for (Block block : event.blockList()) {
            blocks.add(new dLocation(block.getLocation()).identify());
        }
        this.event = event;
        fire(event);
        if (blockSet) {
            event.blockList().clear();
            if (blocks.size() > 0) {
                event.blockList().clear();
                for (String loc : blocks) {
                    dLocation location = dLocation.valueOf(loc);
                    event.blockList().add(location.getWorld().getBlockAt(location));
                }
            }
        }
        event.setYield(strength);
    }
}
