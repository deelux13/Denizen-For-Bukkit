package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dCuboid;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityTargetsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity targets (<entity>) (because <cause>)
    // <entity> targets (<entity>) (because <cause>)
    //
    // @Regex ^on [^\s]+ targets( [^\s]+)?( because [^\s]+)?$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when an entity targets a new entity.
    //
    // @Context
    // <context.entity> returns the targeting entity.
    // <context.reason> returns the reason the entity changed targets.
    // <context.target> returns the targeted entity.
    // <context.cuboids> DEPRECATED.
    //
    // @Determine
    // dEntity to make the entity target a different entity instead.
    //
    // @Player when the entity being targetted is a player.
    //
    // -->

    public EntityTargetsScriptEvent() {
        instance = this;
    }

    public static EntityTargetsScriptEvent instance;
    public dEntity entity;
    public Element reason;
    public dEntity target;
    public dList cuboids;
    private dLocation location;
    public EntityTargetEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.getXthArg(1, CoreUtilities.toLowerCase(s)).equals("targets");
    }

    @Override
    public boolean matches(ScriptPath path) {

        if (!tryEntity(entity, path.eventArgLowerAt(0))) {
            return false;
        }

        String victim = path.eventArgLowerAt(2);
        if (!victim.equals("in") && !victim.equals("because") && !victim.equals("") && !tryEntity(target, victim)) {
            return false;
        }

        if (!runInCheck(path, location)) {
            return false;
        }

        int index = path.eventArgLowerAt(3).equals("because") ? 3 : (path.eventArgAt(2).equals("because") ? 2 : -1);
        if (index > 0 && !path.eventArgLowerAt(index + 1).equals(CoreUtilities.toLowerCase(reason.toString()))) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "EntityTargets";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        if (dEntity.matches(determination)) {
            target = dEntity.valueOf(determination);
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
            return entity.getDenizenObject();
        }
        else if (name.equals("reason")) {
            return reason;
        }
        else if (name.equals("cuboids")) { // NOTE: Deprecated
            if (cuboids == null) {
                cuboids = new dList();
                for (dCuboid cuboid : dCuboid.getNotableCuboidsContaining(location)) {
                    cuboids.add(cuboid.identifySimple());
                }
            }
            return cuboids;
        }
        else if (name.equals("target") && target != null) {
            return target.getDenizenObject();
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onEntityTargets(EntityTargetEvent event) {
        entity = new dEntity(event.getEntity());
        reason = new Element(event.getReason().toString());
        target = event.getTarget() != null ? new dEntity(event.getTarget()) : null;
        location = new dLocation(event.getEntity().getLocation());
        cuboids = null;
        this.event = event;
        fire(event);
    }

}
