package net.aufdemrand.denizen.nms.helpers;

import net.aufdemrand.denizen.nms.interfaces.SoundHelper;
import net.aufdemrand.denizencore.utilities.debugging.dB;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundHelper_v1_13_R2 implements SoundHelper {

    @Override
    public Sound getMidiInstrumentFromPatch(int patch) {
        // look up the instrument matching the patch
        // TODO: 1.13 - does this still work?
        switch (instruments_1_12[patch]) {
            case 0:
                return Sound.BLOCK_NOTE_BLOCK_HARP;
            case 1:
                return Sound.BLOCK_NOTE_BLOCK_BASS;
            case 2:
                return Sound.BLOCK_NOTE_BLOCK_SNARE;
            case 3:
                return Sound.BLOCK_NOTE_BLOCK_HAT;
            case 4:
                return Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
            case 5:
                return Sound.BLOCK_NOTE_BLOCK_GUITAR;
            case 6:
                return Sound.BLOCK_NOTE_BLOCK_BELL;
            case 7:
                return Sound.BLOCK_NOTE_BLOCK_CHIME;
            case 8:
                return Sound.BLOCK_NOTE_BLOCK_FLUTE;
            case 9:
                return Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
            case 10:
                return Sound.BLOCK_NOTE_BLOCK_PLING;
        }
        return getDefaultMidiInstrument();
    }

    @Override
    public Sound getDefaultMidiInstrument() {
        return Sound.BLOCK_NOTE_BLOCK_HARP;
    }

    @Override
    public void playSound(Player player, Location location, String sound, float volume, float pitch, String category) {
        SoundCategory categoryEnum = SoundCategory.MASTER;
        try {
            if (category != null) {
                categoryEnum = SoundCategory.valueOf(category);
            }
        }
        catch (Exception ex) {
            dB.echoError(ex);
        }
        if (player == null) {
            location.getWorld().playSound(location, sound, categoryEnum, volume, pitch);
        }
        else {
            player.playSound(location, sound, categoryEnum, volume, pitch);
        }
    }

    @Override
    public void playSound(Player player, Location location, Sound sound, float volume, float pitch, String category) {
        SoundCategory categoryEnum = SoundCategory.MASTER;
        try {
            if (category != null) {
                categoryEnum = SoundCategory.valueOf(category);
            }
        }
        catch (Exception ex) {
            dB.echoError(ex);
        }
        if (player == null) {
            location.getWorld().playSound(location, sound, categoryEnum, volume, pitch);
        }
        else {
            player.playSound(location, sound, categoryEnum, volume, pitch);
        }
    }
}
