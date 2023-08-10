/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.MathUtils;
import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ZoomAddon extends WindowAddon
    implements WindowInputAddon, WindowRenderAddon {
    private final Vector2d draggingStart, zoomOffsetDestination, zoomOffset;
    private boolean dragging = false;

    private double zoomDestination = 0;
    private double currentZoom = 0;

    private final long draggingCursor;

    public ZoomAddon(PictureInPictureWindow window) {
        super("zoom", window);

        draggingStart = new Vector2d();
        zoomOffsetDestination = new Vector2d();
        zoomOffset = new Vector2d();

        draggingCursor = glfwCreateStandardCursor(GLFW_RESIZE_ALL_CURSOR);
    }

    @Override
    public void onScroll(double xOffset, double yOffset) {
        zoomDestination += yOffset > 0 ? 0.1f : -0.1f;

        if (zoomDestination > 2f) {
            zoomDestination = 2f;
        } else if (zoomDestination < 0) {
            zoomDestination = 0f;
        }
    }

    @Override
    public void onDragStart(Vector2d mousePosition, int button, int mods) {
        if (dragging || button != GLFW_MOUSE_BUTTON_MIDDLE) {
            return;
        }

        draggingStart.set(mousePosition);
        dragging = true;

        glfwSetCursor(window.getHandle(), draggingCursor);
    }

    @Override
    public void onDragEnd(Vector2d mousePosition, int button, int mods) {
        dragging = false;
        glfwSetCursor(window.getHandle(), NULL);
    }

    @Override
    public void onMouseMove(Vector2d mousePosition) {
        if (!dragging) {
            return;
        }

        double deltaX = (mousePosition.x - draggingStart.x) / window.getFrameBufferWidth();
        double deltaY = (mousePosition.y - draggingStart.y) / window.getFrameBufferHeight();
        zoomOffsetDestination.add(deltaX * 2, -deltaY * 2);
        zoomOffsetDestination.set(
            MathUtils.clamp(zoomOffsetDestination, -1 * (currentZoom), 1 * (currentZoom)));

        draggingStart.set(mousePosition);
    }

    @Override
    public void render() {
        // Translate
        if (!zoomOffset.equals(zoomOffsetDestination)) {
            double deltaX = zoomOffsetDestination.x - zoomOffset.x;
            double deltaY = zoomOffsetDestination.y - zoomOffset.y;

            zoomOffset.add(deltaX * 0.2, deltaY * 0.2);
        }

        // Zoom
        double delta = zoomDestination - currentZoom;
        currentZoom += delta * 0.2;

        zoomOffsetDestination.set(
            MathUtils.clamp(zoomOffsetDestination, -1 * currentZoom, 1 * currentZoom));

        // Apply
        glTranslated(zoomOffset.x, zoomOffset.y, 0);
        glScaled(1 + currentZoom, 1 + currentZoom, 1);
    }

    @Override
    public boolean override() {
        return false;
    }

    @Override
    public void onKeyAction(int key, int scancode, int action, int mods) {

    }

    @Override
    public void onClick(Vector2d mousePosition, int button, int mods) {

    }

    @Override
    public void onDoubleClick(Vector2d mousePosition, int button, int mods) {

    }
}
