package dev.febri.soundify;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Compact, low-contrast settings screen for the client-only sound layer. */
public final class SoundifyConfigScreen extends Screen {
    private final Screen parent;
    private Button toggleButton;

    public SoundifyConfigScreen(Screen parent) {
        super(Component.literal("Soundify Remake"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 38;

        toggleButton = this.addRenderableWidget(Button.builder(
                toggleLabel(),
                button -> {
                    SoundifySettings.setEnabled(!SoundifySettings.enabled());
                    button.setMessage(toggleLabel());
                }).bounds(cx - 100, y, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Volume −"),
                button -> SoundifySettings.setVolume(
                        SoundifySettings.rawVolume() - 0.05F
                )).bounds(cx - 100, y + 28, 96, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Volume +"),
                button -> SoundifySettings.setVolume(
                        SoundifySettings.rawVolume() + 0.05F
                )).bounds(cx + 4, y + 28, 96, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Reset"),
                button -> {
                    SoundifySettings.setVolume(0.65F);
                    SoundifySettings.setEnabled(true);
                    toggleButton.setMessage(toggleLabel());
                }).bounds(cx - 100, y + 56, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Kembali"),
                button -> this.onClose()
        ).bounds(cx - 100, this.height - 38, 200, 20).build());
    }

    private static Component toggleLabel() {
        return Component.literal(
                SoundifySettings.enabled() ? "Soundify: ON" : "Soundify: OFF"
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int cx = this.width / 2;
        graphics.fill(cx - 118, 24, cx + 118, this.height - 24, 0xE51B2028);
        graphics.fill(cx - 118, 24, cx + 118, 26, 0xFF526C75);
        graphics.text(this.font, "SOUNDIFY REMAKE", cx, 42, 0xFFE7EDF0, true);
        graphics.text(this.font, "Volume Soundify", cx,
                this.height / 2 - 62, 0xFFB8C5CC, true);
        graphics.text(this.font,
                Math.round(SoundifySettings.rawVolume() * 100) + "%",
                cx, this.height / 2 + 22, 0xFF91B9B5, true);
        graphics.text(this.font, "Terpisah dari volume Minecraft", cx,
                this.height / 2 + 92, 0xFF8D9AA3, true);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
