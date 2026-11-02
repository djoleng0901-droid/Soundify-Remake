package dev.febri.soundify;

import java.util.prefs.Preferences;

/** Small persistent client-side Soundify settings store. */
public final class SoundifySettings {
    private static final Preferences PREFS =
            Preferences.userNodeForPackage(SoundifySettings.class);
    private static final String VOLUME_KEY = "soundify_volume";
    private static final String ENABLED_KEY = "soundify_enabled";

    private static float volume = clamp(PREFS.getFloat(VOLUME_KEY, 0.65F));
    private static boolean enabled = PREFS.getBoolean(ENABLED_KEY, true);

    private SoundifySettings() {}

    public static float volume() {
        return enabled ? volume : 0.0F;
    }

    public static float rawVolume() {
        return volume;
    }

    public static boolean enabled() {
        return enabled;
    }

    public static void setVolume(float value) {
        volume = clamp(value);
        PREFS.putFloat(VOLUME_KEY, volume);
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        PREFS.putBoolean(ENABLED_KEY, enabled);
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
