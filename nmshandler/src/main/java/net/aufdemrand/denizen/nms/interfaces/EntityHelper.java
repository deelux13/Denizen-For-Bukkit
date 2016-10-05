package net.aufdemrand.denizen.nms.interfaces;

import net.aufdemrand.denizen.nms.util.BoundingBox;
import net.aufdemrand.denizen.nms.util.jnbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public interface EntityHelper {

    void forceInteraction(Player player, Location location);

    Entity getEntity(World world, UUID uuid);

    boolean isBreeding(Animals entity);

    void setBreeding(Animals entity, boolean breeding);

    void setTarget(Creature entity, LivingEntity target);

    CompoundTag getNbtData(Entity entity);

    void setNbtData(Entity entity, CompoundTag compoundTag);

    void setSilent(Entity entity, boolean silent);

    boolean isSilent(Entity entity);

    ItemStack getItemInHand(LivingEntity entity);

    void setItemInHand(LivingEntity entity, ItemStack itemStack);

    ItemStack getItemInOffHand(LivingEntity entity);

    void setItemInOffHand(LivingEntity entity, ItemStack itemStack);

    void stopFollowing(Entity follower);

    void stopWalking(Entity entity);

    void toggleAI(Entity entity, boolean hasAI);

    boolean isAIDisabled(Entity entity);

    double getSpeed(Entity entity);

    void setSpeed(Entity entity, double speed);

    void follow(final Entity target, final Entity follower, final double speed, final double lead,
                final double maxRange, final boolean allowWander);

    void walkTo(final Entity entity, Location location, double speed, final Runnable callback);

    void hideEntity(Player player, Entity entity, boolean keepInTabList);

    void unhideEntity(Player player, Entity entity);

    boolean isHidden(Player player, UUID entity);

    /**
     * Rotates an entity.
     *
     * @param entity The Entity you want to rotate.
     * @param yaw    The new yaw of the entity.
     * @param pitch  The new pitch of the entity.
     */
    void rotate(Entity entity, float yaw, float pitch);

    // Taken from C2 NMS class for less dependency on C2
    void look(Entity entity, float yaw, float pitch);

    class MapTraceResult {
        public Location hitLocation;
        public BlockFace angle;
    }

    boolean canTrace(World world, Vector start, Vector end);

    MapTraceResult mapTrace(LivingEntity from, double range);

    /**
     * Gets the precise location in the specified direction.
     *
     * @param start     The location to start the check from.
     * @param direction The one-length vector to use as a direction.
     * @param range     The maximum distance between the start and end.
     * @return The location, or null if it isn't in range.
     */
    Location rayTrace(Location start, Vector direction, double range);

    Location getImpactNormal(Location start, Vector direction, double range);

    /**
     * Gets the precise location a LivingEntity is looking at.
     *
     * @param from  The LivingEntity to start the trace from.
     * @param range The maximum distance between the LivingEntity and the location.
     * @return The location, or null if it isn't in range.
     */
    Location eyeTrace(LivingEntity from, double range);

    Location faceLocation(Location from, Location at);

    /**
     * Changes an entity's yaw and pitch to make it face a location.
     *
     * @param from The Entity whose yaw and pitch you want to change.
     * @param at   The Location it should be looking at.
     */
    void faceLocation(Entity from, Location at);

    /**
     * Changes an entity's yaw and pitch to make it face another entity.
     *
     * @param entity The Entity whose yaw and pitch you want to change.
     * @param target The Entity it should be looking at.
     */
    void faceEntity(Entity entity, Entity target);

    /**
     * Checks if a Location's yaw is facing another Location.
     * <p/>
     * Note: do not use a player's location as the first argument,
     * because player yaws need to modified. Use the method
     * below this one instead.
     *
     * @param from        The Location we check.
     * @param at          The Location we want to know if the first Location's yaw
     *                    is facing
     * @param degreeLimit How many degrees can be between the direction the
     *                    first location's yaw is facing and the direction
     *                    we check if it is facing.
     * @return Returns a boolean.
     */
    boolean isFacingLocation(Location from, Location at, float degreeLimit);

    /**
     * Checks if an Entity is facing a Location.
     *
     * @param from        The Entity we check.
     * @param at          The Location we want to know if it is looking at.
     * @param degreeLimit How many degrees can be between the direction the
     *                    Entity is facing and the direction we check if it
     *                    is facing.
     * @return Returns a boolean.
     */
    boolean isFacingLocation(Entity from, Location at, float degreeLimit);

    /**
     * Checks if an Entity is facing another Entity.
     *
     * @param from        The Entity we check.
     * @param at          The Entity we want to know if it is looking at.
     * @param degreeLimit How many degrees can be between the direction the
     *                    Entity is facing and the direction we check if it
     *                    is facing.
     * @return Returns a boolean.
     */
    boolean isFacingEntity(Entity from, Entity at, float degreeLimit);

    /**
     * Normalizes Mincraft's yaws (which can be negative or can exceed 360)
     * by turning them into proper yaw values that only go from 0 to 359.
     *
     * @param yaw The original yaw.
     * @return The normalized yaw.
     */
    float normalizeYaw(float yaw);

    /**
     * Converts a vector to a yaw.
     * <p/>
     * Thanks to bergerkiller.
     *
     * @param vector The vector you want to get a yaw from.
     * @return The yaw.
     */
    float getYaw(Vector vector);

    /**
     * Converts a yaw to a cardinal direction name.
     *
     * @param yaw The yaw you want to get a cardinal direction from.
     * @return The name of the cardinal direction as a String.
     */
    String getCardinal(float yaw);

    void move(Entity entity, Vector vector);

    BoundingBox getBoundingBox(Entity entity);

    void setBoundingBox(Entity entity, BoundingBox boundingBox);
}
