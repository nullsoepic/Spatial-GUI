package org.tastytrash.spatialGUI.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//? if >=26.3 {
/*import org.lwjgl.sdl.SDLMouse;
*///?} else {
import org.lwjgl.glfw.GLFW;
 //?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.client.SpatialGUIClient;

@Mixin(InputConstants.class)
public class InputConstantsMixin {

    @Unique
    private static boolean shouldBeFirstPerson(Screen screen) {
        var cfg = SpatialGUI.config;
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            return cfg.firstPersonModeInventory;
        }
        return cfg.firstPersonModeContainers;
    }

    @Unique
    private static boolean spatialGUI$shouldKeepGrab() {
        if (!SpatialGUIClient.isEnabled() || !SpatialGUI.config.useCrosshairForFirstPerson) return false;

        Minecraft mc = Minecraft.getInstance();
        //? if <26.2 {
        Screen screen = mc.screen;
         //?} else {
        /*Screen screen = mc.gui.screen();
        *///?}
        if (mc.level == null || screen == null) return false;
        if (!SpatialGUIClient.shouldHookScreen(screen)) return false;
        return shouldBeFirstPerson(screen);
    }

    //? if >=26.3 {
    /*@Inject(method = "releaseMouse", at = @At("HEAD"), cancellable = true)
    private static void spatialGUI$cancelReleaseMouse(Window window, double xpos, double ypos, CallbackInfo ci) {
        if (!SDLMouse.SDL_GetWindowRelativeMouseMode(window.handle())) return;
        if (!spatialGUI$shouldKeepGrab()) return;
        ci.cancel();
    }
    *///?} else {
    @Inject(method = "grabOrReleaseMouse", at = @At("HEAD"), cancellable = true)
    //? if >=26.1.2 {
    private static void spatialGUI$cancelGrabMouse(Window window, int cursorMode, double xpos, double ypos, CallbackInfo ci) {
    //?} else {
    /*private static void spatialGUI$cancelGrabMouse(long window, int cursorMode, double x, double y, CallbackInfo ci) {
    *///?}
        if (cursorMode != GLFW.GLFW_CURSOR_NORMAL) return;
        //? if >=26.1.2 {
        if (GLFW.glfwGetInputMode(window.handle(), GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) return;
        //?} else {
        /*if (GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) return;
         *///?}
        if (!spatialGUI$shouldKeepGrab()) return;
        ci.cancel();
    }
    //?}
}