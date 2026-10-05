    package org.tastytrash.spatialGUI.mixin.input;

    import com.mojang.blaze3d.platform.InputConstants;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.gui.screens.Screen;
    import org.lwjgl.glfw.GLFW;
    import org.spongepowered.asm.mixin.Mixin;
    import org.spongepowered.asm.mixin.injection.At;
    import org.spongepowered.asm.mixin.injection.Inject;
    import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
    import org.tastytrash.spatialGUI.SpatialGUI;
    import org.tastytrash.spatialGUI.client.SpatialGUIClient;

    @Mixin(InputConstants.class)
    public class InputConstantsMixin {
        @Inject(method = "grabOrReleaseMouse", at = @At("HEAD"), cancellable = true)
        private static void spatialGUI$cancelGrabMouse(long window, int cursorValue, double x, double y, CallbackInfo ci) {
            if (cursorValue != GLFW.GLFW_CURSOR_NORMAL) return;
            if (GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) return;
            if (!SpatialGUIClient.isEnabled() || !SpatialGUI.config.useCrosshairForFirstPerson) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            if (!mc.options.getCameraType().isFirstPerson()) return;

            Screen screen = mc.screen;
            if (screen != null && SpatialGUIClient.shouldHookScreen(screen)) {
                ci.cancel();
            }
        }
    }
