/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class DraggableAddon extends WindowAddon
    implements WindowInputAddon, WindowAttributeAddon,
    WindowRenderAddon {

    private boolean dragging = false;

    private double dragStartX, dragStartY;
    private double windowXDestination, windowYDestination;

    public DraggableAddon(PictureInPictureWindow window) {
        super("draggable", window);
    }


    @Override
    public void onDragStart(double mouseX, double mouseY, int button, int mods) {
        if (dragging || button != GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        dragging = true;

        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    @Override
    public void onDragEnd(double mouseX, double mouseY, int button, int mods) {
        dragging = false;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button, int mods) {

    }

    @Override
    public void onDoubleClick(double mouseX, double mouseY, int button, int mods) {

    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        if (!dragging) {
            return;
        }

        double deltaX = mouseX - dragStartX;
        double deltaY = mouseY - dragStartY;

        windowXDestination += deltaX;
        windowYDestination += deltaY;

        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    @Override
    public void onWindowMove(int windowX, int windowY) {
        if (dragging) {
            return;
        }

        windowXDestination = windowX;
        windowYDestination = windowY;
    }

    @Override
    public void onWindowFocus() {

    }

    @Override
    public void onWindowBlur() {

    }

    @Override
    public void render() {
        try (MemoryStack windowStack = MemoryStack.stackPush()) {
            IntBuffer windowXBuffer = windowStack.mallocInt(1);
            IntBuffer windowYBuffer = windowStack.mallocInt(1);
            glfwGetWindowPos(window.getHandle(), windowXBuffer, windowYBuffer);

            int windowX = windowXBuffer.get();
            int windowY = windowYBuffer.get();

            if (windowX == windowXDestination && windowY == windowYDestination) {
                return;
            }

            double deltaX = windowXDestination - windowX;
            double deltaY = windowYDestination - windowY;

            int newWindowX = windowX + (int) (deltaX * 0.2);
            int newWindowY = windowY + (int) (deltaY * 0.2);

            glfwSetWindowPos(window.getHandle(), newWindowX, newWindowY);

            try (MemoryStack mouseStack = MemoryStack.stackPush()) {
                DoubleBuffer x = mouseStack.mallocDouble(1);
                DoubleBuffer y = mouseStack.mallocDouble(1);
                glfwGetCursorPos(window.getHandle(), x, y);
                dragStartX = x.get();
                dragStartY = y.get();
            }

        }
    }
}
