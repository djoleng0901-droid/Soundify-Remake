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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class SoundifyRemakeClient implements ClientModInitializer {
    public static final String MOD_ID = "soundify_remake";

    private static int lastSelectedSlot = -1;

    public static final SoundEvent INVENTORY_OPEN =
            registerSound("inventory_open");
    public static final SoundEvent INVENTORY_CLOSE =
            registerSound("inventory_close");

    public static final SoundEvent TOOL_SELECT_AXE =
            registerSound("tool_axe_select");
    public static final SoundEvent TOOL_SELECT_SWORD =
            registerSound("tool_sword_select");
    public static final SoundEvent TOOL_SELECT_MACE =
            registerSound("tool_mace_select");
    public static final SoundEvent TOOL_SELECT_SPEAR =
            registerSound("tool_spear_select");
    public static final SoundEvent TOOL_SELECT_SHIELD =
            registerSound("tool_shield_select");

    private static SoundEvent registerSound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    private static void playSound(
            Minecraft client,
            SoundEvent sound,
            SoundSource source
    ) {
        if (client.level == null || client.player == null) {
            return;
        }

        client.level.playPlayerSound(sound, source, 1.0F, 1.0F);
    }

    @Override
    public void onInitializeClient() {
        // Inventory open and close
        ScreenEvents.AFTER_INIT.register(
                (client, screen, scaledWidth, scaledHeight) -> {
                    if (!(screen instanceof InventoryScreen)) {
                        return;
                    }

                    playSound(client, INVENTORY_OPEN, SoundSource.UI);

                    ScreenEvents.remove(screen).register(
                            removedScreen -> playSound(
                                    client,
                                    INVENTORY_CLOSE,
                                    SoundSource.UI
                            )
                    );
                }
        );

        // Detect actual hotbar slot changes
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                lastSelectedSlot = -1;
                return;
            }

            int selectedSlot =
                    client.player.getInventory().getSelectedSlot();

            if (lastSelectedSlot == -1) {
                lastSelectedSlot = selectedSlot;
                return;
            }

            if (selectedSlot == lastSelectedSlot) {
                return;
            }

            lastSelectedSlot = selectedSlot;

            ItemStack stack =
                    client.player.getInventory().getSelectedItem();

            if (stack.is(ItemTags.AXES)) {
                playSound(client, TOOL_SELECT_AXE, SoundSource.UI);
            } else if (stack.is(ItemTags.SWORDS)) {
                playSound(client, TOOL_SELECT_SWORD, SoundSource.UI);
            } else if (stack.is(Items.MACE)) {
                playSound(client, TOOL_SELECT_MACE, SoundSource.UI);
            } else if (stack.is(ItemTags.SPEARS)) {
                playSound(client, TOOL_SELECT_SPEAR, SoundSource.UI);
            } else if (stack.is(Items.SHIELD)) {
                playSound(client, TOOL_SELECT_SHIELD, SoundSource.UI);
            }
        });

        System.out.println(
                "[Soundify Remake] Inventory and hotbar sounds initialized!"
        );
    }
}