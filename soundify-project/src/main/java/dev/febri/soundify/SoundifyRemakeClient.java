package dev.febri.soundify;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Client-only sound triggers. Every trigger is edge-based so a sustained state
 * (for example, a potion effect) plays once when it begins, not every tick.
 */
public final class SoundifyRemakeClient implements ClientModInitializer {
    public static final String MOD_ID = "soundify_remake";

    private static int lastSelectedSlot = -1;
    private static int lastExperienceLevel = -1;
    private static int lastKillStat = -1;
    private static int killStreak;
    private static long clientTick;
    private static long lastKillTick = -1L;
    private static boolean wasDead;
    private static boolean wasOnGround = true;
    private static boolean hadSpeed;
    private static boolean hadSlowness;
    private static boolean hadNightVision;
    private static boolean hadJumpBoost;
    private static boolean wasUsingGoldenApple;
    private static boolean usingGoldenApple;

    public static final SoundEvent INVENTORY_OPEN = registerSound("inventory_open");
    public static final SoundEvent INVENTORY_CLOSE = registerSound("inventory_close");
    public static final SoundEvent TOOL_SELECT_AXE = registerSound("tool_axe_select");
    public static final SoundEvent TOOL_SELECT_SWORD = registerSound("tool_sword_select");
    public static final SoundEvent TOOL_SELECT_MACE = registerSound("tool_mace_select");
    public static final SoundEvent TOOL_SELECT_SPEAR = registerSound("tool_spear_select");
    public static final SoundEvent TOOL_SELECT_SHIELD = registerSound("tool_shield_select");

    // Video reference clips. Their OGG resources are kept under sounds/video/reference/.
    public static final SoundEvent VIDEO_EXPLOSION = registerSound("video_explosion");
    public static final SoundEvent PLAYER_DEATH = registerSound("player_death");
    public static final SoundEvent PLAYER_RESPAWN = registerSound("player_respawn");
    public static final SoundEvent KILLSTREAK_1 = registerSound("killstreak_1");
    public static final SoundEvent KILLSTREAK_2 = registerSound("killstreak_2");
    public static final SoundEvent KILLSTREAK_3 = registerSound("killstreak_3");
    public static final SoundEvent KILLSTREAK_4_PLUS = registerSound("killstreak_4_plus");
    public static final SoundEvent SPEED_EFFECT = registerSound("speed_effect");
    public static final SoundEvent SLOWNESS_EFFECT = registerSound("slowness_effect");
    public static final SoundEvent NIGHT_VISION_EFFECT = registerSound("night_vision_effect");
    public static final SoundEvent JUMP_BOOST_EFFECT = registerSound("jump_boost_effect");
    public static final SoundEvent JUMP_WITH_BOOST = registerSound("jump_with_boost");
    public static final SoundEvent VIDEO_ITEM_DROP = registerSound("video_item_drop");
    public static final SoundEvent VIDEO_MACE_SELECT = registerSound("video_mace_select");
    public static final SoundEvent ENDER_PEARL_TELEPORT = registerSound("ender_pearl_teleport");
    public static final SoundEvent XP_LEVEL_UP = registerSound("xp_level_up");
    public static final SoundEvent GOLDEN_APPLE_EFFECT = registerSound("golden_apple_effect");
    public static final SoundEvent VIDEO_BLOCK_PLACE = registerSound("video_block_place");
    public static final SoundEvent VIDEO_INVENTORY_SELECT = registerSound("video_inventory_select");

    private static SoundEvent registerSound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    private static void playSound(Minecraft client, SoundEvent sound, SoundSource source) {
        if (client.level == null || client.player == null || !SoundifySettings.enabled()) {
            return;
        }

        float volume = SoundifySettings.volume();
        if (volume <= 0.0F) return;
        client.level.playPlayerSound(sound, source, volume, 1.0F);
    }

    private static void resetPlayerState() {
        lastSelectedSlot = -1;
        lastExperienceLevel = -1;
        lastKillStat = -1;
        killStreak = 0;
        clientTick = 0L;
        lastKillTick = -1L;
        wasDead = false;
        wasOnGround = true;
        hadSpeed = false;
        hadSlowness = false;
        hadNightVision = false;
        hadJumpBoost = false;
        wasUsingGoldenApple = false;
        usingGoldenApple = false;
    }

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof InventoryScreen)) return;

            playSound(client, INVENTORY_OPEN, SoundSource.UI);
            ScreenEvents.remove(screen).register(
                    removedScreen -> playSound(client, INVENTORY_CLOSE, SoundSource.UI)
            );
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                resetPlayerState();
                return;
            }

            var player = client.player;
            clientTick++;

            // Vanilla kill statistics are server-authoritative: unlike attack
            // callbacks, they only advance when the server credits a real kill.
            int killStat = player.getStats().getValue(Stats.CUSTOM.get(Stats.MOB_KILLS))
                    + player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAYER_KILLS));
            if (lastKillStat < 0) {
                lastKillStat = killStat;
            } else if (killStat < lastKillStat) {
                // Stats can be reset by a world/server change.
                lastKillStat = killStat;
                killStreak = 0;
                lastKillTick = -1L;
            } else if (killStat > lastKillStat) {
                int newKills = killStat - lastKillStat;
                lastKillStat = killStat;
                for (int i = 0; i < newKills; i++) {
                    if (lastKillTick < 0L || clientTick - lastKillTick > 160L) {
                        killStreak = 0;
                    }
                    killStreak++;
                    lastKillTick = clientTick;
                    SoundEvent streakSound = killStreak == 1 ? KILLSTREAK_1
                            : killStreak == 2 ? KILLSTREAK_2
                            : killStreak == 3 ? KILLSTREAK_3 : KILLSTREAK_4_PLUS;
                    playSound(client, streakSound, SoundSource.PLAYERS);
                }
            }
            if (killStreak > 0 && lastKillTick >= 0L
                    && clientTick - lastKillTick > 160L) {
                killStreak = 0;
                lastKillTick = -1L;
            }

            // Player death/respawn transitions.
            boolean deadNow = player.isDeadOrDying();
            if (deadNow && !wasDead) {
                playSound(client, PLAYER_DEATH, SoundSource.PLAYERS);
            } else if (!deadNow && wasDead) {
                playSound(client, PLAYER_RESPAWN, SoundSource.PLAYERS);
            }
            wasDead = deadNow;

            // Status effects: only play when each effect becomes active.
            boolean speedNow = player.hasEffect(MobEffects.SPEED);
            boolean slownessNow = player.hasEffect(MobEffects.SLOWNESS);
            boolean nightVisionNow = player.hasEffect(MobEffects.NIGHT_VISION);
            boolean jumpBoostNow = player.hasEffect(MobEffects.JUMP_BOOST);

            if (speedNow && !hadSpeed) playSound(client, SPEED_EFFECT, SoundSource.PLAYERS);
            if (slownessNow && !hadSlowness) playSound(client, SLOWNESS_EFFECT, SoundSource.PLAYERS);
            if (nightVisionNow && !hadNightVision) playSound(client, NIGHT_VISION_EFFECT, SoundSource.PLAYERS);
            if (jumpBoostNow && !hadJumpBoost) playSound(client, JUMP_BOOST_EFFECT, SoundSource.PLAYERS);

            hadSpeed = speedNow;
            hadSlowness = slownessNow;
            hadNightVision = nightVisionNow;
            hadJumpBoost = jumpBoostNow;

            // Jump sound is specific to a jump while Jump Boost is active.
            boolean onGroundNow = player.onGround();
            if (wasOnGround && !onGroundNow && jumpBoostNow) {
                playSound(client, JUMP_WITH_BOOST, SoundSource.PLAYERS);
            }
            wasOnGround = onGroundNow;

            // XP level-up transition.
            if (lastExperienceLevel >= 0 && player.experienceLevel > lastExperienceLevel) {
                playSound(client, XP_LEVEL_UP, SoundSource.PLAYERS);
            }
            lastExperienceLevel = player.experienceLevel;

            // Detect completed golden-apple use without firing during every use tick.
            usingGoldenApple = player.isUsingItem()
                    && (player.getUseItem().is(Items.GOLDEN_APPLE)
                    || player.getUseItem().is(Items.ENCHANTED_GOLDEN_APPLE));
            if (wasUsingGoldenApple && !usingGoldenApple) {
                playSound(client, GOLDEN_APPLE_EFFECT, SoundSource.PLAYERS);
            }
            wasUsingGoldenApple = usingGoldenApple;

            // Actual hotbar slot changes, with exact item-category checks.
            int selectedSlot = player.getInventory().getSelectedSlot();
            if (lastSelectedSlot == -1) {
                lastSelectedSlot = selectedSlot;
            } else if (selectedSlot != lastSelectedSlot) {
                lastSelectedSlot = selectedSlot;
                ItemStack stack = player.getInventory().getSelectedItem();

                if (stack.is(ItemTags.AXES) || stack.is(ItemTags.PICKAXES)) {
                    playSound(client, TOOL_SELECT_AXE, SoundSource.UI);
                } else if (stack.is(ItemTags.SWORDS)) {
                    playSound(client, TOOL_SELECT_SWORD, SoundSource.UI);
                } else if (stack.is(Items.MACE)) {
                    playSound(client, VIDEO_MACE_SELECT, SoundSource.UI);
                } else if (stack.is(ItemTags.SPEARS)) {
                    playSound(client, TOOL_SELECT_SPEAR, SoundSource.UI);
                } else if (stack.is(Items.SHIELD)) {
                    playSound(client, TOOL_SELECT_SHIELD, SoundSource.UI);
                }
            }
        });

        System.out.println("[Soundify Remake] Client audio triggers initialized.");
    }
}
