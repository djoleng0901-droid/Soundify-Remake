package dev.febri.soundify;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SoundifyConfigScreen extends Screen {

    private final Screen parent;

    public SoundifyConfigScreen(Screen parent) {
        super(Component.literal("Soundify Remake"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Kembali"),
                        button -> this.onClose()
                ).bounds(centerX - 100, this.height - 40, 200, 20).build()
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.text(
                this.font,
                "SOUNDIFY REMAKE",
                this.width / 2,
                45,
                0xFFFFFFFF,
                true
        );

        graphics.text(
                this.font,
                "Pengaturan suara akan tersedia di sini.",
                this.width / 2,
                75,
                0xFFAAAAAA,
                true
        );
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}