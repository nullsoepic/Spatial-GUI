package org.tastytrash.spatialGUI.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;

//? if >=26.1.2 {
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;
//?} else {
/*import net.minecraft.client.gui.Gui;
*///?}

//? if >=26.1.2 {
@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {
    @Inject(method = "addGuiElement", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$redirectToIsolatedState(GuiElementRenderState element, CallbackInfo ci) {
        if (!SpatialGUIRenderer.isExtractingScreen || !SpatialGUI.config.enabled) {
            return;
        }

        SpatialGUIRenderer renderer = SpatialGUIClient.renderer();
        if (renderer == null) {
            return;
        }

        GuiRenderState isolatedState = renderer.getScreenRenderState();
        if ((Object) this == isolatedState) {
            return;
        }

        isolatedState.addGuiElement(spatialGUI$adaptForTransparentTarget(element));
        ci.cancel();
    }

    @Unique
    private static GuiElementRenderState spatialGUI$adaptForTransparentTarget(GuiElementRenderState element) {
        if (element instanceof BlitRenderState blit
                && blit.pipeline() != null
                && !"minecraft".equals(blit.pipeline().getLocation().getNamespace())) {
            return new BlitRenderState(
                    RenderPipelines.GUI_TEXTURED,
                    blit.textureSetup(),
                    new Matrix3x2f(blit.pose()),
                    blit.x0(), blit.y0(), blit.x1(), blit.y1(),
                    blit.u0(), blit.u1(), blit.v0(), blit.v1(),
                    blit.color(),
                    blit.scissorArea()
            );
        }
        return element;
    }
}
//?} else {
/*@Mixin(Gui.class)
public class GuiRenderStateMixin {
}
*///?}
