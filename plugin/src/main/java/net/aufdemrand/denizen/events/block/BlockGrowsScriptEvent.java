package net.aufdemrand.denizen.events.block;

import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dMaterial;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class BlockGrowsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // block grows
    // <block> grows
    //
    // @Regex ^on [^\s]+ grows$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a block grows naturally in the world, EG, when wheat, sugar canes, cacti, watermelons or pumpkins grow.
    // @Context
    // <context.location> returns the dLocation of the block that grew.
    // <context.material> returns the dMaterial of the block that grew.
    //
    // -->

    public BlockGrowsScriptEvent() {
        instance = this;
    }

    public static BlockGrowsScriptEvent instance;
    public dLocation location;
    public dMaterial material;
    public BlockGrowEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cmd = CoreUtilities.getXthArg(1, lower);
        String block = CoreUtilities.getXthArg(0, lower);
        dMaterial mat = dMaterial.valueOf(block);
        return cmd.equals("grows")
                && (block.equals("block") || (mat != null && !mat.isStructure()));
    }

    @Override
    public boolean matches(ScriptPath path) {
        String mat = path.eventArgLowerAt(0);
        if (!tryMaterial(material, mat)) {
            return false;
        }
        if (material.isStructure()) {
            return false;
        }
        return runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "BlockGrows";
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
        else if (name.equals("material")) {
            return material;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onBlockGrows(BlockGrowEvent event) {
        location = new dLocation(event.getBlock().getLocation());
        material = new dMaterial(event.getNewState());
        this.event = event;
        fire(event);
    }
}
