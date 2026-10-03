package org.tastytrash.spatialGUI.mixin.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;

/**
 * Mods rendering custom effects inside GUI screens sometimes rebind the main
 * framebuffer mid-render (e.g. Accessories' hover-highlight PostEffectBuffer).
 * While we are capturing a screen into our isolated target, such a rebind
 * hijacks every draw issued after it, sending parts of the screen to the main
 * framebuffer in 2D. Block main-target binds during the capture window; our
 * extractor restores the main target itself once the capture is finished.
 */
@Mixin(RenderTarget.class)
public abstract class RenderTargetMixin {
    //? if <=1.21.1 {
    /*@Inject(method = "bindWrite", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$blockMainBindDuringCapture(boolean setViewport, CallbackInfo ci) {
        if (SpatialGUIRenderer.isExtractingScreen && (Object) this == Minecraft.getInstance().getMainRenderTarget()) {
            ci.cancel();
        }
    }
    *///?}
}
