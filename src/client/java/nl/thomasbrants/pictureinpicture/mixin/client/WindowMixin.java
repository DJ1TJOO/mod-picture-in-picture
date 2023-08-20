/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.mixin.client;

import net.minecraft.client.util.Window;
import nl.thomasbrants.pictureinpicture.window.WindowManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {
    @Inject(at = @At("RETURN"), method = "onFramebufferSizeChanged")
    private void onFramebufferSizeChanged(CallbackInfo ci) {
        WindowManager.onResolutionChanged();
    }

    @Inject(at = @At("RETURN"), method = "updateFramebufferSize")
    private void onUpdateFramebufferSize(CallbackInfo ci) {
        WindowManager.onResolutionChanged();
    }

    @Inject(at = @At("RETURN"), method = "swapBuffers")
    private void swapBuffers(CallbackInfo ci) {
        WindowManager.renderWindows();
    }
}