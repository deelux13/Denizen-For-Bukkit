package net.aufdemrand.denizen.scripts.commands.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipCommand extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        Map<String, dItem> equipment = new HashMap<String, dItem>();

        // Initialize necessary fields
        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("entities")
                    && arg.matchesArgumentList(dEntity.class)) {

                scriptEntry.addObject("entities", arg.asType(dList.class).filter(dEntity.class, scriptEntry));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("head", "helmet")) {
                equipment.put("head", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("chest", "chestplate")) {
                equipment.put("chest", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("legs", "leggings")) {
                equipment.put("legs", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("boots", "feet")) {
                equipment.put("boots", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("saddle")) {
                equipment.put("saddle", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("horse_armor", "horse_armour")) {
                equipment.put("horse_armor", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matchesArgumentType(dItem.class)
                    && arg.matchesPrefix("offhand")) {
                equipment.put("offhand", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }

            // Default to item in hand if no prefix is used
            else if (arg.matchesArgumentType(dItem.class)) {
                equipment.put("hand", dItem.valueOf(arg.getValue(), scriptEntry.entryData.getTagContext()));
            }
            else if (arg.matches("player") && ((BukkitScriptEntryData) scriptEntry.entryData).hasPlayer()) {
                // Player arg for compatibility with old scripts
                scriptEntry.addObject("entities", Arrays.asList(((BukkitScriptEntryData) scriptEntry.entryData).getPlayer().getDenizenEntity()));
            }
            else {
                arg.reportUnhandled();
            }
        }

        // Make sure at least one equipment argument was used
        if (equipment.isEmpty()) {
            throw new InvalidArgumentsException("Must specify equipment!");
        }

        scriptEntry.addObject("equipment", equipment);

        // Use player or NPC as default entity
        scriptEntry.defaultObject("entities", (((BukkitScriptEntryData) scriptEntry.entryData).hasNPC() ? Arrays.asList(((BukkitScriptEntryData) scriptEntry.entryData).getNPC().getDenizenEntity()) : null),
                (((BukkitScriptEntryData) scriptEntry.entryData).hasPlayer() ? Arrays.asList(((BukkitScriptEntryData) scriptEntry.entryData).getPlayer().getDenizenEntity()) : null));

    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ScriptEntry scriptEntry)
            {

        Map<String, dItem> equipment = (Map<String, dItem>) scriptEntry.getObject("equipment");
        List<dEntity> entities = (List<dEntity>) scriptEntry.getObject("entities");

        // Report to dB
        if (scriptEntry.dbCallShouldDebug()) {
            dB.report(scriptEntry, getName(), aH.debugObj("entities", entities.toString()) +
                    aH.debugObj("equipment", equipment.toString()));
        }

        for (dEntity entity : entities) {

            if (entity.isGeneric()) {
                dB.echoError(scriptEntry.getResidingQueue(), "Cannot equip generic entity " + entity.identify() + "!");
            }
            else if (entity.isCitizensNPC()) {

                dNPC npc = entity.getDenizenNPC();

                if (npc != null) {

                    Equipment trait = npc.getEquipmentTrait();

                    if (equipment.get("hand") != null) {
                        trait.set(0, equipment.get("hand").getItemStack());
                    }
                    if (equipment.get("head") != null) {
                        trait.set(1, equipment.get("head").getItemStack());
                    }
                    if (equipment.get("chest") != null) {
                        trait.set(2, equipment.get("chest").getItemStack());
                    }
                    if (equipment.get("legs") != null) {
                        trait.set(3, equipment.get("legs").getItemStack());
                    }
                    if (equipment.get("boots") != null) {
                        trait.set(4, equipment.get("boots").getItemStack());
                    }
                    if (equipment.get("offhand") != null) {
                        trait.set(5, equipment.get("offhand").getItemStack());
                    }

                    if (npc.isSpawned()) {
                        LivingEntity livingEntity = npc.getLivingEntity();

                        // TODO: Citizens API for this blob?

                        if (livingEntity.getType() == EntityType.HORSE) {
                            if (equipment.get("saddle") != null) {
                                ((Horse) livingEntity).getInventory().setSaddle(equipment.get("saddle").getItemStack());
                            }
                            if (equipment.get("horse_armor") != null) {
                                ((Horse) livingEntity).getInventory().setArmor(equipment.get("horse_armor").getItemStack());
                            }
                        }
                        else if (livingEntity.getType() == EntityType.PIG) {
                            if (equipment.get("saddle") != null) {
                                dItem saddle = equipment.get("saddle");
                                if (saddle.getItemStack().getType() == Material.SADDLE) {
                                    ((Pig) livingEntity).setSaddle(true);
                                }
                                else {
                                    ((Pig) livingEntity).setSaddle(false);
                                }
                            }
                        }
                    }
                }

            }
            else {

                LivingEntity livingEntity = entity.getLivingEntity();

                if (livingEntity != null) {

                    if (livingEntity.getType() == EntityType.HORSE) {
                        if (equipment.get("saddle") != null) {
                            ((Horse) livingEntity).getInventory().setSaddle(equipment.get("saddle").getItemStack());
                        }
                        if (equipment.get("horse_armor") != null) {
                            ((Horse) livingEntity).getInventory().setArmor(equipment.get("horse_armor").getItemStack());
                        }
                    }
                    else if (livingEntity.getType() == EntityType.PIG) {
                        if (equipment.get("saddle") != null) {
                            dItem saddle = equipment.get("saddle");
                            if (saddle.getItemStack().getType() == Material.SADDLE) {
                                ((Pig) livingEntity).setSaddle(true);
                            }
                            else {
                                ((Pig) livingEntity).setSaddle(false);
                            }
                        }
                    }
                    else {

                        if (equipment.get("hand") != null) {
                            NMSHandler.getInstance().getEntityHelper().setItemInHand(livingEntity, equipment.get("hand").getItemStack());
                        }
                        if (equipment.get("head") != null) {
                            livingEntity.getEquipment().setHelmet(equipment.get("head").getItemStack());
                        }
                        if (equipment.get("chest") != null) {
                            livingEntity.getEquipment().setChestplate(equipment.get("chest").getItemStack());
                        }
                        if (equipment.get("legs") != null) {
                            livingEntity.getEquipment().setLeggings(equipment.get("legs").getItemStack());
                        }
                        if (equipment.get("boots") != null) {
                            livingEntity.getEquipment().setBoots(equipment.get("boots").getItemStack());
                        }
                        if (equipment.get("offhand") != null) {
                            NMSHandler.getInstance().getEntityHelper().setItemInOffHand(livingEntity, equipment.get("offhand").getItemStack());
                        }
                    }
                }
            }
        }
    }
}
