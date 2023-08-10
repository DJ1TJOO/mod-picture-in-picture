/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class DraggableAddon extends WindowAddon
    implements WindowInputAddon, WindowAttributeAddon,
    WindowRenderAddon {

    private boolean dragging = false;

    private final Vector2d draggingStart, windowDestination;

    public DraggableAddon(PictureInPictureWindow window) {
        super("draggable", window);

        draggingStart = new Vector2d();
        windowDestination = new Vector2d();
    }


    @Override
    public void onDragStart(Vector2d mousePosition, int button, int mods) {
        if (dragging || button != GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }

        draggingStart.set(mousePosition);

        if (window.isMaximized()) {
            Vector2i windowPosition = window.getWindowPosition();
            double absoluteMouseX = windowPosition.x + mousePosition.x;
            double absoluteMouseY = windowPosition.y + mousePosition.y;

            window.minimize();

            Vector2i windowSize = window.getWindowSize();
            double newWindowX = (absoluteMouseX - windowSize.x / 2.0);
            double newWindowY = (absoluteMouseY - windowSize.y / 2.0);
            window.setWindowPosition((int) newWindowX, (int) newWindowY);

            Vector2d newMousePosition = window.getMousePosition();
            draggingStart.set(newMousePosition);
        }

        dragging = true;
    }

    @Override
    public void onDragEnd(Vector2d mousePosition, int button, int mods) {
        dragging = false;
    }

    @Override
    public void onMouseMove(Vector2d mousePosition) {
        if (!dragging) {
            return;
        }

        double deltaX = mousePosition.x - draggingStart.x;
        double deltaY = mousePosition.y - draggingStart.y;
        windowDestination.add(deltaX, deltaY);

        draggingStart.set(mousePosition);
    }

    @Override
    public void onWindowMove(int windowX, int windowY) {
        if (dragging) {
            return;
        }

        windowDestination.set(windowX, windowY);
    }

    @Override
    public void render() {
        Vector2i windowPosition = window.getWindowPosition();
        if (windowPosition.equals((int) windowDestination.x, (int) windowDestination.y)) {
            return;
        }

        double deltaX = windowDestination.x - windowPosition.x;
        double deltaY = windowDestination.y - windowPosition.y;

        int newWindowX = windowPosition.x + (int) (deltaX * 0.2);
        int newWindowY = windowPosition.y + (int) (deltaY * 0.2);
        window.setWindowPosition(newWindowX, newWindowY);

        Vector2d mousePosition = window.getMousePosition();
        draggingStart.set(mousePosition);
    }

    @Override
    public boolean override() {
        return false;
    }

    @Override
    public void onClick(Vector2d mousePosition, int button, int mods) {

    }

    @Override
    public void onDoubleClick(Vector2d mousePosition, int button, int mods) {

    }

    @Override
    public void onWindowFocus() {

    }

    @Override
    public void onWindowBlur() {

    }

    @Override
    public void onWindowResize(int width, int height) {

    }

    @Override
    public void onResolutionChanged(double width, double height) {

    }

    @Override
    public void onWindowInitialized(double width, double height) {

    }

    @Override
    public void onKeyAction(int key, int scancode, int action, int mods) {

    }

    @Override
    public void onScroll(double xOffset, double yOffset) {

    }
}
