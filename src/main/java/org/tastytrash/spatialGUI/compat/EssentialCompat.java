package org.tastytrash.spatialGUI.compat;

import com.mojang.blaze3d.platform.Window;
import org.tastytrash.spatialGUI.mixin.gui.WindowMixin;

import java.util.Set;

public class EssentialCompat {

    private EssentialCompat() {}

    public static boolean isEssentialCaller() {
        return StackWalker.getInstance(Set.of(StackWalker.Option.RETAIN_CLASS_REFERENCE))
                .walk(s -> s.dropWhile(f -> {
                    Class<?> c = f.getDeclaringClass();
                    return c == EssentialCompat.class || c == WindowMixin.class || c == Window.class;
                }).findFirst())
                .map(f -> f.getDeclaringClass().getName().startsWith("gg.essential."))
                .orElse(false);
    }
}
