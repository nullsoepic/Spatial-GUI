package org.tastytrash.spatialGUI.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.joml.Vector2d;
import org.tastytrash.spatialGUI.SpatialGUI;
import org.tastytrash.spatialGUI.mixin.gui.MouseHandlerAccessor;
import org.tastytrash.spatialGUI.render.SpatialGUIRenderer;
import org.tastytrash.spatialGUI.util.RenderUtil.QuadBasis;
import org.tastytrash.spatialGUI.util.RenderUtil.CylinderBasis;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

public class MouseHandlerUtil {
    private static boolean weGrabbedMouse = false;
    private static double lastPosX = Double.NaN;
    private static double lastPosY = Double.NaN;
    private static double cachedSrcX = Double.NaN;
    private static double cachedSrcY = Double.NaN;
    private static Vector2d cachedMouse = null;

    private static double physicalX = Double.NaN;
    private static double physicalY = Double.NaN;

    public static void resetMouseState() {
        Minecraft mc = Minecraft.getInstance();
        lastPosX = Double.NaN;
        lastPosY = Double.NaN;
        cachedSrcX = Double.NaN;
        cachedSrcY = Double.NaN;
        cachedMouse = null;
        freeLookDeltaX = 0;
        freeLookDeltaY = 0;

        if (((MouseHandlerAccessor) mc.mouseHandler).getMouseGrabbed()) {
            physicalX = mc.getWindow().getScreenWidth() / 2.0;
            physicalY = mc.getWindow().getScreenHeight() / 2.0;
        } else {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                DoubleBuffer x = stack.mallocDouble(1);
                DoubleBuffer y = stack.mallocDouble(1);
                GLFW.glfwGetCursorPos(mc.getWindow().handle(), x, y);
                physicalX = x.get(0);
                physicalY = y.get(0);
            }
        }
    }

    public static double getFallback(boolean isX) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() != null) {
            return isX ? mc.getWindow().getGuiScaledWidth() / 2.0 : mc.getWindow().getGuiScaledHeight() / 2.0;
        }
        return 0.0;
    }

    public static Vector2d getOrComputeMousePosition(double srcX, double srcY, QuadBasis quadBasis, CylinderBasis cylinderBasis, double guiScale, com.mojang.blaze3d.pipeline.TextureTarget target) {
        if (!SpatialGUIRenderer.isCrosshairModeActive() && cachedMouse != null && srcX == cachedSrcX && srcY == cachedSrcY) {
            return cachedMouse;
        }
        Vector2d mouse = cylinderBasis != null
                ? RenderUtil.getInventoryMousePositionRayCurved(srcX, srcY, cylinderBasis)
                : RenderUtil.getInventoryMousePositionRay(srcX, srcY, quadBasis, target);
        if (mouse == null) {
            return null;
        }
        cachedSrcX = srcX;
        cachedSrcY = srcY;
        cachedMouse = mouse;
        lastPosX = mouse.x / guiScale;
        lastPosY = mouse.y / guiScale;
        return mouse;
    }

    public static double getLastPos(boolean isX, double fallback) {
        double val = isX ? lastPosX : lastPosY;
        if (Double.isNaN(val) || val <= -1000.0) {
            return (Double.isNaN(fallback) || fallback <= -1000.0) ? getFallback(isX) : fallback;
        }
        return val;
    }

    public static double getLastPos(boolean isX) {
        return getLastPos(isX, getFallback(isX));
    }

    public static void setPhysicalPos(double x, double y, boolean mouseGrabbed) {
        if (mouseGrabbed) return;
        physicalX = x;
        physicalY = y;
    }

    public static void clearCachedMousePosition() {
        cachedSrcX = Double.NaN;
        cachedSrcY = Double.NaN;
        cachedMouse = null;
    }

    public static double getPhysicalX(double fallback) {
        return Double.isNaN(physicalX) ? fallback : physicalX;
    }

    public static double getPhysicalY(double fallback) {
        return Double.isNaN(physicalY) ? fallback : physicalY;
    }

    private static double freeLookDeltaX = 0;
    private static double freeLookDeltaY = 0;

    public static void addFreeLookDelta(double xrel, double yrel) {
        freeLookDeltaX += xrel;
        freeLookDeltaY += yrel;
    }

    public static double[] resetFreeLookDelta() {
        double[] result = {freeLookDeltaX, freeLookDeltaY};
        freeLookDeltaX = 0;
        freeLookDeltaY = 0;
        return result;
    }

    public static void grabMouseForFirstPerson() {
        Minecraft mc = Minecraft.getInstance();
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;
        if (!accessor.getMouseGrabbed()) {
            accessor.setMouseGrabbed(true);
            double centerX = mc.getWindow().getScreenWidth() / 2.0;
            double centerY = mc.getWindow().getScreenHeight() / 2.0;
            //? if >26.2 {
            /*InputConstants.grabMouse(mc.getWindow(), centerX, centerY);
            *///?} else {
             InputConstants.grabOrReleaseMouse(mc.getWindow(), InputConstants.CURSOR_DISABLED, centerX, centerY);
            //?}
            mc.mouseHandler.setIgnoreFirstMove();
        }
        weGrabbedMouse = true;
    }

    public static void releaseMouseFromFirstPerson() {
        if (!weGrabbedMouse) return;
        Minecraft mc = Minecraft.getInstance();
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;
        if (accessor.getMouseGrabbed()) {
            accessor.setMouseGrabbed(false);
            double centerX = mc.getWindow().getScreenWidth() / 2.0;
            double centerY = mc.getWindow().getScreenHeight() / 2.0;
            //? if >26.2 {
            /*InputConstants.releaseMouse(mc.getWindow(), centerX, centerY);
            *///?} else {
             InputConstants.grabOrReleaseMouse(mc.getWindow(), InputConstants.CURSOR_NORMAL, centerX, centerY);
            //?}
        }
        weGrabbedMouse = false;
    }

    public static void updateMouseGrabForFirstPerson(boolean isFirstPerson) {
        if (!SpatialGUI.config.useCrosshairForFirstPerson || !isFirstPerson) {
            releaseMouseFromFirstPerson();
        } else {
            grabMouseForFirstPerson();
        }
    }
}
