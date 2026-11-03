package dev.febri.soundify.mixin;

import dev.febri.soundify.SoundifyRemakeClient;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Plays the item-drop sound only after the local player successfully drops an item.
 * This avoids polling the key state, which can miss short key presses.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerDropMixin {
    @Inject(method = "drop(Z)Z", at = @At("RETURN"))
    private void soundify$afterSuccessfulDrop(boolean dropAll, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue())) {
            SoundifyRemakeClient.playItemDropSound();
        }
    }
}
