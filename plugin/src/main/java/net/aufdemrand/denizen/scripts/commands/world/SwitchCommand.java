package net.aufdemrand.denizen.scripts.commands.world;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.nms.NMSVersion;
import net.aufdemrand.denizen.nms.interfaces.BlockData;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizen.utilities.MaterialCompat;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.depends.Depends;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Duration;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SwitchCommand extends AbstractCommand {

    private enum SwitchState {ON, OFF, TOGGLE}

    private Map<Location, Integer> taskMap = new ConcurrentHashMap<Location, Integer>(8, 0.9f, 1);

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("locations") &&
                    arg.matchesArgumentList(dLocation.class)) {
                scriptEntry.addObject("locations", arg.asType(dList.class));
            }
            else if (!scriptEntry.hasObject("duration") &&
                    arg.matchesArgumentType(Duration.class)) {
                scriptEntry.addObject("duration", arg.asType(Duration.class));
            }
            else if (!scriptEntry.hasObject("state") &&
                    arg.matchesEnum(SwitchState.values())) {
                scriptEntry.addObject("switchstate", new Element(arg.getValue().toUpperCase()));
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("locations")) {
            throw new InvalidArgumentsException("Must specify a location!");
        }

        scriptEntry.defaultObject("duration", new Duration(0));
        scriptEntry.defaultObject("switchstate", new Element("TOGGLE"));
    }

    @Override
    public void execute(final ScriptEntry scriptEntry) {
        final dList interactLocations = scriptEntry.getdObject("locations");
        long duration = ((Duration) scriptEntry.getObject("duration")).getTicks();
        final SwitchState switchState = SwitchState.valueOf(scriptEntry.getElement("switchstate").asString());

        final Player player = ((BukkitScriptEntryData) scriptEntry.entryData).hasPlayer() ? ((BukkitScriptEntryData) scriptEntry.entryData).getPlayer().getPlayerEntity() : null;
        // Switch the Block
        if (scriptEntry.dbCallShouldDebug()) {
            dB.report(scriptEntry, getName(), interactLocations.debug()
                    + aH.debugObj("duration", duration + "t")
                    + aH.debugObj("switchstate", switchState.name()));
        }

        for (final dLocation interactLocation : interactLocations.filter(dLocation.class, scriptEntry)) {
            switchBlock(scriptEntry, interactLocation, switchState, player);

            // If duration set, schedule a delayed task.
            if (duration > 0) {
                // If this block already had a delayed task, cancel it.
                if (taskMap.containsKey(interactLocation)) {
                    try {
                        Bukkit.getScheduler().cancelTask(taskMap.get(interactLocation));
                    }
                    catch (Exception e) {
                    }
                }
                dB.log("Setting delayed task 'SWITCH' for " + interactLocation.identify());
                // Store new delayed task ID, for checking against, then schedule new delayed task.
                taskMap.put(interactLocation, Bukkit.getScheduler().scheduleSyncDelayedTask(DenizenAPI.getCurrentInstance(),
                        new Runnable() {
                            public void run() {
                                switchBlock(scriptEntry, interactLocation, SwitchState.TOGGLE, player);
                            }
                        }, duration));
            }
        }

    }

    public static boolean switchState(Block b) {
        Boolean switchState = NMSHandler.getInstance().getSwitchState(b);
        if (switchState != null) {
            return switchState;
        }
        //return (b.getData() & 0x8) > 0;
        Material type = b.getType();
        if (type == MaterialCompat.IRON_DOOR
                || type == MaterialCompat.OAK_DOOR
                || type == Material.DARK_OAK_DOOR
                || type == Material.BIRCH_DOOR
                || type == Material.ACACIA_DOOR
                || type == Material.JUNGLE_DOOR
                || type == Material.SPRUCE_DOOR) {
            Location location = b.getLocation();
            int data = b.getData();
            if (data >= 8) {
                location = b.getLocation().clone().add(0, -1, 0);
            }
            return (location.getBlock().getData() & 0x4) > 0;
        }
        else if ((type == MaterialCompat.OAK_TRAPDOOR
                || type == Material.IRON_TRAPDOOR)
                || (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_13_R2)
                && (type == Material.DARK_OAK_TRAPDOOR
                || type == Material.BIRCH_TRAPDOOR
                || type == Material.ACACIA_TRAPDOOR
                || type == Material.JUNGLE_TRAPDOOR
                || type == Material.SPRUCE_TRAPDOOR))) {
            return (b.getData() & 0x4) > 0;
        }
        else {
            return (b.getData() & 0x8) > 0;
        }
    }

    // Break off this portion of the code from execute() so it can be used in both execute and the delayed runnable
    public void switchBlock(ScriptEntry scriptEntry, Location interactLocation, SwitchState switchState, Player player) {
        boolean currentState = switchState(interactLocation.getBlock());

        if ((switchState.equals(SwitchState.ON) && !currentState) ||
                (switchState.equals(SwitchState.OFF) && currentState) ||
                switchState.equals(SwitchState.TOGGLE)) {

            if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_13_R2)) {
                NMSHandler.getInstance().setSwitchState(interactLocation, !currentState);
            }
            else {
                try {
                    if (interactLocation.getBlock().getType() == MaterialCompat.IRON_DOOR) {
                        Location block;
                        if (interactLocation.clone().add(0, -1, 0).getBlock().getType() == MaterialCompat.IRON_DOOR) {
                            block = interactLocation.clone().add(0, -1, 0);
                        }
                        else {
                            block = interactLocation;
                        }
                        BlockData blockData = NMSHandler.getInstance().getBlockHelper().getBlockData(MaterialCompat.IRON_DOOR, (byte) (block.getBlock().getData() ^ 4));
                        blockData.setBlock(block.getBlock(), false);
                    }
                    else {
                        // Try for a linked player
                        if (player == null && Bukkit.getOnlinePlayers().size() > 0) {
                            // If there's none, link any player
                            if (Bukkit.getOnlinePlayers().size() > 0) {
                                player = (Player) Bukkit.getOnlinePlayers().toArray()[0];
                            }
                            else if (Depends.citizens != null) {
                                // If there are no players, link any Human NPC
                                for (NPC npc : CitizensAPI.getNPCRegistry()) {
                                    if (npc.isSpawned() && npc.getEntity() instanceof Player) {
                                        player = (Player) npc.getEntity();
                                        break;
                                    }
                                }
                                // TODO: backup if no human NPC available? (Fake EntityPlayer instance?)
                            }
                        }
                        NMSHandler.getInstance().getEntityHelper().forceInteraction(player, interactLocation);
                    }

                }
                catch (NullPointerException e) {
                    dB.echoError("Cannot switch " + interactLocation.getBlock().getType().toString() + "!");
                    return;
                }
            }

            dB.echoDebug(scriptEntry, "Switched " + interactLocation.getBlock().getType().toString() + "! Current state now: " +
                    (switchState(interactLocation.getBlock()) ? "ON" : "OFF"));
        }
    }
}
