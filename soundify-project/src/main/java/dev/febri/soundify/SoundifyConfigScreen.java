package dev.febri.soundify;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A compact, vanilla-styled configuration layout with category navigation.
 * Settings remain local to this client and independent from Minecraft volume.
 */
public final class SoundifyConfigScreen extends Screen {
    private static final int TAB_GENERAL = 0;
    private static final int TAB_AUDIO = 1;
    private static final int TAB_ABOUT = 2;

    private final Screen parent;
    private final int activeTab;
    private Button toggleButton;

    public SoundifyConfigScreen(Screen parent) {
        this(parent, TAB_GENERAL);
    }

    private SoundifyConfigScreen(Screen parent, int activeTab) {
        super(Component.literal("Soundify"));
        this.parent = parent;
        this.activeTab = activeTab;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int left = cx - 190;
        int top = 28;
        int navX = left + 12;
        int contentX = left + 130;
        int contentWidth = 238;

        addTabButton("General", TAB_GENERAL, navX, top + 52);
        addTabButton("Audio", TAB_AUDIO, navX, top + 80);
        addTabButton("About", TAB_ABOUT, navX, top + 108);

        if (activeTab == TAB_GENERAL) {
            toggleButton = this.addRenderableWidget(Button.builder(
                    toggleLabel(),
                    button -> {
                        SoundifySettings.setEnabled(!SoundifySettings.enabled());
                        button.setMessage(toggleLabel());
                    }).bounds(contentX, top + 82, contentWidth, 20).build());
        } else if (activeTab == TAB_AUDIO) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("−"),
                    button -> SoundifySettings.setVolume(
                            SoundifySettings.rawVolume() - 0.05F
                    )).bounds(contentX, top + 112, 34, 20).build());

            this.addRenderableWidget(Button.builder(
                    Component.literal("+"),
                    button -> SoundifySettings.setVolume(
                            SoundifySettings.rawVolume() + 0.05F
                    )).bounds(contentX + contentWidth - 34, top + 112, 34, 20).build());
        }

        int footerY = this.height - 54;
        this.addRenderableWidget(Button.builder(
                Component.literal("Reset"),
                button -> {
                    SoundifySettings.setVolume(0.65F);
                    SoundifySettings.setEnabled(true);
                    if (toggleButton != null) {
                        toggleButton.setMessage(toggleLabel());
                    }
                }).bounds(contentX, footerY, 112, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> this.onClose()
        ).bounds(contentX + 120, footerY, 118, 20).build());
    }

    private void addTabButton(String label, int tab, int x, int y) {
        this.addRenderableWidget(Button.builder(
                Component.literal((activeTab == tab ? "» " : "") + label),
                button -> this.minecraft.gui.setScreen(
                        new SoundifyConfigScreen(this.parent, tab)
                )).bounds(x, y, 104, 22).build());
    }

    private static Component toggleLabel() {
        return Component.literal(
                SoundifySettings.enabled() ? "Soundify  •  Enabled" : "Soundify  •  Disabled"
        );
    }

    private int sliderLeft() {
        return this.width / 2 - 60;
    }

    private int sliderRight() {
        return this.width / 2 + 60;
    }

    private int sliderY() {
        return 28 + 154;
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int cx = this.width / 2;
        int left = cx - 190;
        int right = cx + 190;
        int top = 28;
        int bottom = this.height - 28;
        int navX = left + 12;
        int contentX = left + 130;

        graphics.fill(left, top, right, bottom, 0xE510141B);
        graphics.fill(left, top, right, top + 3, 0xFF6C9BA4);
        graphics.fill(navX, top + 42, navX + 104, bottom - 42, 0xA51C252D);
        graphics.fill(contentX - 8, top + 42, right - 12, bottom - 42, 0xA51A222A);
        graphics.fill(left + 120, top + 42, left + 121, bottom - 42, 0xFF46545D);

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.text(this.font, "SOUNDIFY", left + 14, top + 14, 0xFFF0F4F6, true);
        graphics.text(this.font, "CONFIGURATION", right - 14, top + 14, 0xFF9FB5BC, true);

        if (activeTab == TAB_GENERAL) {
            graphics.text(this.font, "General", contentX, top + 54, 0xFFE8EEF0, true);
            graphics.text(this.font, "Control the Soundify audio layer.", contentX, top + 68,
                    0xFF9EADB4, false);
            graphics.text(this.font, "MOD STATUS", contentX, top + 116, 0xFF9FB5BC, true);
            graphics.text(this.font,
                    SoundifySettings.enabled() ? "All enabled Soundify cues can play."
                            : "Soundify audio is currently muted.",
                    contentX, top + 130, 0xFFD1DADD, false);
            graphics.text(this.font, "This setting does not change Minecraft's own volume.",
                    contentX, top + 166, 0xFF87969E, false);
        } else if (activeTab == TAB_AUDIO) {
            graphics.text(this.font, "Audio", contentX, top + 54, 0xFFE8EEF0, true);
            graphics.text(this.font, "Independent Soundify master volume", contentX, top + 76,
                    0xFFD1DADD, false);
            graphics.text(this.font, Math.round(SoundifySettings.rawVolume() * 100) + "%",
                    cx, top + 102, 0xFF9FD0C5, true);

            int trackLeft = sliderLeft();
            int trackRight = sliderRight();
            int trackY = sliderY();
            int fillRight = trackLeft + Math.round(
                    (trackRight - trackLeft) * SoundifySettings.rawVolume()
            );
            graphics.fill(trackLeft, trackY, trackRight, trackY + 5, 0xFF39464E);
            if (fillRight > trackLeft) {
                graphics.fill(trackLeft, trackY, fillRight, trackY + 5, 0xFF79B8AD);
            }
            graphics.fill(fillRight - 2, trackY - 3, fillRight + 3, trackY + 8, 0xFFE6F0ED);
            graphics.text(this.font, "0%", trackLeft, trackY + 15, 0xFF87969E, false);
            graphics.text(this.font, "100%", trackRight - 24, trackY + 15,
                    0xFF87969E, false);
            graphics.text(this.font, "Use − / + to adjust the volume.",
                    contentX, top + 190, 0xFF87969E, false);
        } else {
            graphics.text(this.font, "About Soundify Remake", contentX, top + 54,
                    0xFFE8EEF0, true);
            graphics.text(this.font, "A client-side audio enhancement project.",
                    contentX, top + 78, 0xFFD1DADD, false);
            graphics.text(this.font, "Settings are saved locally on this device.",
                    contentX, top + 98, 0xFF9EADB4, false);
            graphics.text(this.font, "Minecraft Java  •  Fabric", contentX, top + 132,
                    0xFF9FB5BC, true);
        }

        graphics.text(this.font, "SOUNDIFY REMAKE", left + 14, bottom - 17,
                0xFF788992, false);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
