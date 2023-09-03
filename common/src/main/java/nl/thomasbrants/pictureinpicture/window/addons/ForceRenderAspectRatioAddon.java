/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import nl.thomasbrants.pictureinpicture.window.Window;

import static org.lwjgl.opengl.GL11.*;

public class ForceRenderAspectRatioAddon extends WindowAddon implements WindowRenderAddon {

    public ForceRenderAspectRatioAddon(
        Window window) {
        super("force-render-aspect-ratio", window);
    }

    @Override
    public void render() {
        Framebuffer minecraftFBO = MinecraftClient.getInstance().getFramebuffer();

        glBindTexture(GL_TEXTURE_2D,
            minecraftFBO.getColorAttachment());
        glEnable(GL_TEXTURE_2D);

        float windowAspect = ((float) window.getFrameBufferWidth()) / window.getFrameBufferHeight();
        float minecraftAspect = ((float) minecraftFBO.textureWidth) / minecraftFBO.textureHeight;

        float diffX = 0;
        float diffY = 0;

        if (windowAspect > minecraftAspect) {
            // Width is bigger
            diffX =
                ((windowAspect - minecraftAspect) / windowAspect) * 2;
        } else if (windowAspect < minecraftAspect) {
            // Height is bigger
            diffY =
                ((minecraftAspect - windowAspect) / minecraftAspect) * 2;
        }

        glColor4f(1f, 1f, 1f, 1f);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(-1 + diffX / 2, -1 + diffY / 2);
        glTexCoord2f(0, 1);
        glVertex2f(-1 + diffX / 2, 1 - diffY / 2);
        glTexCoord2f(1, 1);
        glVertex2f(1 - diffX / 2, 1 - diffY / 2);
        glTexCoord2f(1, 0);
        glVertex2f(1 - diffX / 2, -1 + diffY / 2);
        glEnd();
    }

    @Override
    public boolean override() {
        return true;
    }
}
