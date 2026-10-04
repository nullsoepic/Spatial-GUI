package org.tastytrash.spatialGUI.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;
import org.tastytrash.spatialGUI.util.MouseHandlerUtil;
import org.tastytrash.spatialGUI.util.RenderUtil.QuadBasis;

@Mixin(value = MouseHandler.class, priority = 1100)
public class MouseHandlerMixin {

    @Unique private static double lastPhysicalX;
    @Unique private static double lastPhysicalY;

    @Unique
    private static boolean shouldApplyMouseOverride() {
        if (!SpatialGUIClient.isEnabled()) return false;
        Minecraft client = Minecraft.getInstance();
        //? if >=26.2 {
        /*Screen screen = client.screen;
         *///?} else {
        Screen screen = client.screen;
        //?}
        if (SpatialGUIClient.shouldHookScreen(screen)) return true;
        var renderer = SpatialGUIClient.renderer();
        return renderer != null && SpatialGUIClient.shouldHookScreen(renderer.getHookedScreen());
    }

    @Inject(method = "onMove(JDD)V", at = @At("HEAD"), cancellable = true)
    private void spatialGUI$onMove(long handle, double x, double y, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (handle != mc.getWindow().handle()) {
            ci.cancel();
            return;
        }

        MouseHandlerUtil.setPhysicalPos(x, y);

        if (!shouldApplyMouseOverride()) return;

        var renderer = SpatialGUIClient.renderer();
        if (renderer == null) return;

        QuadBasis quad = renderer.getInventoryRenderer().getQuadBasis();
        if (quad == null) return;

        double guiScale = SpatialGUI.config.getEffectiveGuiScale(
                mc.getWindow().getWidth(), mc.getWindow().getHeight());

        double sourceX = SpatialGUIRenderer.isCrosshairModeActive()
                ? mc.getWindow().getScreenWidth() / 2.0
                : x;

        double sourceY = SpatialGUIRenderer.isCrosshairModeActive()
                ? mc.getWindow().getScreenHeight() / 2.0
                : y;

        Vector2d mouse = MouseHandlerUtil.getOrComputeMousePosition(
                sourceX, sourceY, quad, renderer.getInventoryRenderer().getCylinderBasis(), guiScale, renderer.getTargetManager().getInventoryTarget()
        );

        if (mouse == null) return;

        double mappedX = mouse.x / guiScale;
        double mappedY = mouse.y / guiScale;

        mappedX *= (double) mc.getWindow().getScreenWidth() / mc.getWindow().getGuiScaledWidth();
        mappedY *= (double) mc.getWindow().getScreenHeight() / mc.getWindow().getGuiScaledHeight();

        MouseHandlerAccessor mouseHandler = (MouseHandlerAccessor) this;

        double oldX = mouseHandler.getRawXpos();
        double oldY = mouseHandler.getRawYpos();

        double deltaX = x - lastPhysicalX;
        double deltaY = y - lastPhysicalY;

        lastPhysicalX = x;
        lastPhysicalY = y;

        if (SpatialGUIRenderer.isCrosshairModeActive()) {
            MouseHandlerUtil.addFreeLookDelta(deltaX, deltaY);
        }

        mouseHandler.setRawXpos(mappedX);
        mouseHandler.setRawYpos(mappedY);

        mouseHandler.setAccumulatedDX(mouseHandler.getAccumulatedDX() + mappedX - oldX);
        mouseHandler.setAccumulatedDY(mouseHandler.getAccumulatedDY() + mappedY - oldY);

        ci.cancel();
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    //? if >1.20.1 {
    private void spatialGUI$cancelPlayerRotation(double mousea, CallbackInfo ci) {
    //?} else {
    /*private void spatialGUI$cancelPlayerRotation(CallbackInfo ci) {
    *///?}
        if (SpatialGUIRenderer.isCrosshairModeActive()) {
            //? if <=1.21.1 {
            /*MouseHandlerUtil.addFreeLookDelta(this.accumulatedDX, this.accumulatedDY);
            this.accumulatedDX = 0.0;
            this.accumulatedDY = 0.0;
            *///?}
            ci.cancel();
        }
    }

    @Unique
    private double spatialGUI$preReleaseX;

    @Unique
    private double spatialGUI$preReleaseY;

    @Inject(method = "releaseMouse", at = @At("HEAD"))
    private void spatialGUI$capturePreRelease(CallbackInfo ci) {
        var spatialGUI$acc = (MouseHandlerAccessor) (Object) this;
        spatialGUI$preReleaseX = spatialGUI$acc.getRawXpos();
        spatialGUI$preReleaseY = spatialGUI$acc.getRawYpos();
    }

    @Unique
    private static boolean spatialGUI$crosshairIncoming() {
        return SpatialGUI.config.useCrosshairForFirstPerson && SpatialGUIClient.getEffectiveFirstPersonMode();
    }

    @Unique
    private static Screen spatialGUI$currentScreen(Minecraft spatialGUI$mc) {
        //? if >=26.2 {
        /*return spatialGUI$mc.gui.screen();
         *///?} else {
        return spatialGUI$mc.screen;
        //?}
    }

    @Inject(method = "releaseMouse", at = @At("TAIL"))
    private void spatialGUI$restoreCursorFields(CallbackInfo ci) {
        Minecraft spatialGUI$mc = Minecraft.getInstance();
        Screen spatialGUI$screen = spatialGUI$currentScreen(spatialGUI$mc);
        if (spatialGUI$crosshairIncoming() || spatialGUI$screen == null || !SpatialGUIClient.shouldHookScreen(spatialGUI$screen)) return;
        var spatialGUI$acc = (MouseHandlerAccessor) (Object) this;
        spatialGUI$acc.setRawXpos(spatialGUI$preReleaseX);
        spatialGUI$acc.setRawYpos(spatialGUI$preReleaseY);
    }
}
