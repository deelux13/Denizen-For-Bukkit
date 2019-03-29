package net.aufdemrand.denizen.nms;

import net.aufdemrand.denizen.nms.abstracts.AnimationHelper;
import net.aufdemrand.denizen.nms.abstracts.BiomeNMS;
import net.aufdemrand.denizen.nms.abstracts.BlockLight;
import net.aufdemrand.denizen.nms.abstracts.ParticleHelper;
import net.aufdemrand.denizen.nms.abstracts.ProfileEditor;
import net.aufdemrand.denizen.nms.abstracts.Sidebar;
import net.aufdemrand.denizen.nms.interfaces.*;
import net.aufdemrand.denizen.nms.interfaces.packets.PacketHandler;
import net.aufdemrand.denizen.nms.util.PlayerProfile;
import net.aufdemrand.denizen.nms.util.jnbt.CompoundTag;
import net.aufdemrand.denizen.nms.util.jnbt.Tag;
import net.aufdemrand.denizencore.utilities.debugging.dB;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public abstract class NMSHandler {

    private static NMSHandler instance;
    private static NMSVersion version;
    private static JavaPlugin javaPlugin;

    public static boolean initialize(JavaPlugin plugin) {
        javaPlugin = plugin;
        String packageName = javaPlugin.getServer().getClass().getPackage().getName();
        try {
            // Check if we support this MC version
            version = NMSVersion.valueOf(packageName.substring(packageName.lastIndexOf('.') + 1));
        }
        catch (Exception e) {
            version = NMSVersion.NOT_SUPPORTED;
            instance = null;
            return false;
        }
        try {
            // Get the class of our handler for this version
            final Class<?> clazz = Class.forName("net.aufdemrand.denizen.nms.Handler_" + version.name());
            if (NMSHandler.class.isAssignableFrom(clazz)) {
                // Found and loaded - good to go!
                instance = (NMSHandler) clazz.newInstance();
                return true;
            }
        }
        catch (Exception e) {
            dB.echoError(e);
        }
        // Someone made an oopsie and didn't implement this version properly :(
        version = NMSVersion.NOT_SUPPORTED;
        instance = null;
        return false;
    }

    public static NMSHandler getInstance() {
        return instance;
    }

    public static NMSVersion getVersion() {
        return version;
    }

    public static JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public boolean isCorrectMappingsCode() {
        return true;
    }

    public abstract void disableAsyncCatcher();

    public abstract Sidebar createSidebar(Player player);

    public abstract BlockLight createBlockLight(Location location, int lightLevel, long ticks);

    public abstract PlayerProfile fillPlayerProfile(PlayerProfile playerProfile);

    public abstract PlayerProfile getPlayerProfile(Player player);

    public abstract ProfileEditor getProfileEditor();

    public abstract BiomeNMS getBiomeNMS(Biome biome);

    public abstract Thread getMainThread();

    public abstract double[] getRecentTps();

    public abstract AdvancementHelper getAdvancementHelper();

    public abstract AnimationHelper getAnimationHelper();

    public abstract BlockHelper getBlockHelper();

    public abstract ChunkHelper getChunkHelper();

    public abstract CustomEntityHelper getCustomEntityHelper();

    public abstract EntityHelper getEntityHelper();

    public abstract FishingHelper getFishingHelper();

    public abstract ItemHelper getItemHelper();

    public abstract SoundHelper getSoundHelper();

    public abstract PacketHelper getPacketHelper();

    public abstract ParticleHelper getParticleHelper();

    public abstract PlayerHelper getPlayerHelper();

    public abstract WorldHelper getWorldHelper();

    public abstract void enablePacketInterception(PacketHandler packetHandler);

    public abstract CompoundTag createCompoundTag(Map<String, Tag> value);

    public abstract int getPort();

    public static Vector fixOffset(Vector offset, double yaw, double pitch) {
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        Vector offsetPatched = offset.clone();
        // x rotation
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);
        double y1 = (offsetPatched.getY() * cosPitch) - (offsetPatched.getZ() * sinPitch);
        double z1 = (offsetPatched.getY() * sinPitch) + (offsetPatched.getZ() * cosPitch);
        offsetPatched.setY(y1);
        offsetPatched.setZ(z1);
        // y rotation
        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double x2 = (offsetPatched.getX() * cosYaw) + (offsetPatched.getZ() * sinYaw);
        double z2 = (offsetPatched.getX() * -sinYaw) + (offsetPatched.getZ() * cosYaw);
        offsetPatched.setX(x2);
        offsetPatched.setZ(z2);
        return offsetPatched;
    }


    public HashMap<UUID, UUID> attachmentsA = new HashMap<>(); // Key follows value
    public HashMap<UUID, UUID> attachments2 = new HashMap<>(); // Value follows key
    public HashMap<UUID, Vector> attachmentOffsets = new HashMap<>();
    public HashSet<UUID> attachmentRotations = new HashSet<>();
    public HashMap<UUID, Vector> visiblePositions = new HashMap<>();

    public void forceAttachMove(Entity a, Entity b, Vector offset, boolean matchRotation) {
        if (!getVersion().isAtLeast(NMSVersion.v1_12_R1)) {
            throw new RuntimeException("Unsupported forceAttachMove!");
        }
        if (attachmentsA.containsKey(a.getUniqueId())) {
            attachments2.remove(attachmentsA.get(a.getUniqueId()));
            attachmentsA.remove(a.getUniqueId());
            attachmentOffsets.remove(a.getUniqueId());
            attachmentRotations.remove(a.getUniqueId());
        }
        if (b == null) {
            return;
        }
        attachmentsA.put(a.getUniqueId(), b.getUniqueId());
        attachments2.put(b.getUniqueId(), a.getUniqueId());
        attachmentOffsets.put(a.getUniqueId(), offset);
        if (matchRotation) {
            attachmentRotations.add(a.getUniqueId());
        }
    }

    public Boolean getSwitchState(Block b) {
        return null;
    }

    public boolean setSwitchState(Location interactLocation, boolean state) {
        return false;
    }
}
