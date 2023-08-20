/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;

import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;

public class ForceWindowAspectRatioAddon extends WindowAddon implements WindowAttributeAddon {

    public ForceWindowAspectRatioAddon(PictureInPictureWindow window) {
        super("force-window-aspect-ratio", window);
    }

    @Override
    public void onWindowInitialized(double width, double height) {
        glfwSetWindowAspectRatio(window.getHandle(), (int) width, (int) height);
    }

    @Override
    public void onResolutionChanged(double width, double height) {
        glfwSetWindowAspectRatio(window.getHandle(), (int) width, (int) height);
    }

    @Override
    public boolean remove() {
        glfwSetWindowAspectRatio(window.getHandle(), GLFW_DONT_CARE, GLFW_DONT_CARE);
        return super.remove();
    }

    @Override
    public void onWindowResize(int width, int height) {

    }

    @Override
    public void onWindowMove(int windowX, int windowY) {

    }

    @Override
    public void onWindowFocus() {

    }

    @Override
    public void onWindowBlur() {

    }
}
