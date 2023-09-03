/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.mixin.client;

import com.mojang.blaze3d.platform.Window;
import nl.thomasbrants.pictureinpicture.window.WindowManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Inject(at = @At("RETURN"), method = "onFramebufferResize")
    private void onFramebufferResize(CallbackInfo ci) {
        WindowManager.onResolutionChanged();
    }

    @Inject(at = @At("RETURN"), method = "refreshFramebufferSize")
    private void refreshFramebufferSize(CallbackInfo ci) {
        WindowManager.onResolutionChanged();
    }

    @Inject(at = @At("RETURN"), method = "updateDisplay")
    private void updateDisplay(CallbackInfo ci) {
        WindowManager.renderWindows();
    }
}