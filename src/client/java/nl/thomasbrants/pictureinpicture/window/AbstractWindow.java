/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.InputSupplier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static nl.thomasbrants.pictureinpicture.PictureInPictureMod.PIP_LOGGER;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class AbstractWindow {

    protected long handle;
    private String title;

    private long lastMouseRelease;
    private int lastMouseMods, lastMouseButton;
    private int mouseCount;
    private boolean isMouseDown = false;
    private boolean isDragging = false;
    private final Vector2d mouseDownPosition, lastMousePosition;
    private int frameBufferWidth, frameBufferHeight;

    protected AbstractWindow(String title) {
        this.title = title;

        this.mouseDownPosition = new Vector2d();
        this.lastMousePosition = new Vector2d();
    }

    public void render() {
        // Check for destroy
        if (glfwWindowShouldClose(handle)) {
            destroy();
            return;
        }

        // Update mouse click addon
        long current = System.currentTimeMillis();
        long delta = current - lastMouseRelease;
        if (mouseCount > 1) {
            mouseCount = 0;
            onDoubleClick(lastMousePosition, lastMouseButton, lastMouseMods);
        } else if (mouseCount > 0 && delta > 500) {
            mouseCount = 0;
            onClick(lastMousePosition, lastMouseButton, lastMouseMods);
        }

        glfwMakeContextCurrent(handle);
        GlStateManager._clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT,
            MinecraftClient.IS_SYSTEM_MAC);
        GlStateManager._viewport(0, 0, frameBufferWidth, frameBufferHeight);

        this.renderWindow();

        glfwSwapBuffers(handle);
        glfwMakeContextCurrent(MinecraftClient.getInstance().getWindow().getHandle());
    }

    /**
     * Opens the window.
     */
    public void create() {
        if (handle != NULL) {
            PIP_LOGGER.error("Window has already been created");
            return;
        }

        // Set window hints
        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.setHints();

        // Create window
        net.minecraft.client.util.Window minecraftWindow =
            MinecraftClient.getInstance().getWindow();
        double windowWidth = minecraftWindow.getWidth();
        double windowHeight = minecraftWindow.getHeight();

        handle = glfwCreateWindow((int) windowWidth, (int) windowHeight,
            this.title,
            NULL,
            minecraftWindow.getHandle());

        if (handle == NULL) {
            throw new RuntimeException("Failed to create Chat Window");
        }

        setWindowIcon();

        // Set callbacks
        InputUtil.setMouseCallbacks(handle, this::handleMouseMove, this::handleMouseAction,
            this::handleScroll, null);
        InputUtil.setKeyboardCallbacks(handle, this::handleKeyAction, null);
        glfwSetFramebufferSizeCallback(handle, this::handleWindowResize);
        glfwSetWindowFocusCallback(handle, this::handleWindowFocus);
        glfwSetWindowPosCallback(handle, this::handleWindowMove);

        // Reposition window
        if (!minecraftWindow.isFullscreen()) {
            Vector2i minecraftWindowPosition = getWindowPosition(minecraftWindow.getHandle());
            setWindowPosition(
                (int) (minecraftWindowPosition.x +
                    (minecraftWindow.getWidth() - windowWidth) / 2.0),
                (int) (minecraftWindowPosition.y +
                    (minecraftWindow.getHeight() - windowHeight) / 2.0));
        }

        // Set capabilities
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        RenderSystem.setupDefaultState(0, 0, frameBufferWidth, frameBufferHeight);
        glfwMakeContextCurrent(minecraftWindow.getHandle());

        // Update size
        handleWindowResize(handle, (int) windowWidth, (int) windowHeight);

        glfwShowWindow(handle);
    }

    /**
     * Destroys the chat window and all resources associated with it.
     */
    public void destroy() {
        if (handle == 0) {
            return;
        }

        glfwDestroyWindow(handle);
        handle = 0;
    }

    protected abstract void setHints();

    protected abstract void renderWindow();

    private void handleWindowFocus(long window, boolean focused) {
        if (focused) {
            onFocus();
        } else {
            onBlur();
        }
    }

    private void handleMouseAction(long window, int button, int action, int mods) {
        Vector2d mousePosition = getMousePosition();

        if (action == GLFW_RELEASE) {
            if (isDragging) {
                isDragging = false;
                onDragEnd(mousePosition, button, mods);
            } else {
                mouseCount++;
                lastMouseRelease = System.currentTimeMillis();
            }
        } else if (action == GLFW_PRESS) {
            mouseDownPosition.set(mousePosition);
        }

        isMouseDown = action == GLFW_PRESS;
        lastMouseButton = button;
        lastMouseMods = mods;
        lastMousePosition.set(mousePosition);
    }

    private void handleMouseMove(long window, double mouseX, double mouseY) {
        Vector2d mousePosition = new Vector2d(mouseX, mouseY);

        if (isMouseDown && !isDragging &&
            (Math.abs(mousePosition.x - mouseDownPosition.x) > 6 ||
                Math.abs(mousePosition.y - mouseDownPosition.y) > 6)) {
            isDragging = true;
            onDragStart(mousePosition, lastMouseButton, lastMouseMods);
        }

        this.onMouseMove(mousePosition);

        lastMousePosition.set(mousePosition);
    }

    public void handleWindowResize(long window, int width, int height) {
        frameBufferWidth = width;
        frameBufferHeight = height;

        this.onWindowResize(width, height);
    }

    private void handleScroll(long window, double xOffset, double yOffset) {
        this.onScroll(xOffset, yOffset);
    }

    private void handleKeyAction(long window, int key, int scancode, int action, int mods) {
        this.onKeyAction(key, scancode, action, mods);
    }

    private void handleWindowMove(long window, int windowX, int windowY) {
        this.onWindowMove(windowX, windowY);
    }

    protected abstract void onWindowResize(int width, int height);

    protected abstract void onWindowMove(int windowX, int windowY);

    protected abstract void onFocus();

    protected abstract void onBlur();

    protected abstract void onScroll(double xOffset, double yOffset);

    protected abstract void onMouseMove(Vector2d mousePosition);

    protected abstract void onKeyAction(int key, int scancode, int action, int mods);

    protected abstract void onClick(Vector2d lastMousePosition, int lastMouseButton,
                                    int lastMouseMods);

    protected abstract void onDoubleClick(Vector2d lastMousePosition, int lastMouseButton,
                                          int lastMouseMods);

    protected abstract void onDragStart(Vector2d mousePosition, int lastMouseButton,
                                        int lastMouseMods);

    protected abstract void onDragEnd(Vector2d mousePosition, int button, int mods);

    protected void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(handle, title);
    }

    public Vector2d getMousePosition() {
        try (MemoryStack mouseStack = MemoryStack.stackPush()) {
            DoubleBuffer mouseXBuffer = mouseStack.mallocDouble(1);
            DoubleBuffer mouseYBuffer = mouseStack.mallocDouble(1);
            glfwGetCursorPos(handle, mouseXBuffer, mouseYBuffer);
            return new Vector2d(mouseXBuffer.get(), mouseYBuffer.get());
        }
    }

    public Vector2i getWindowPosition() {
        return getWindowPosition(handle);
    }

    private Vector2i getWindowPosition(long handle) {
        try (MemoryStack windowPosStack = MemoryStack.stackPush()) {
            IntBuffer windowXBuffer = windowPosStack.callocInt(1);
            IntBuffer windowYBuffer = windowPosStack.callocInt(1);
            glfwGetWindowPos(handle, windowXBuffer, windowYBuffer);
            return new Vector2i(windowXBuffer.get(), windowYBuffer.get());
        }
    }

    public Vector2i getWindowSize() {
        try (MemoryStack windowPosStack = MemoryStack.stackPush()) {
            IntBuffer windowWidthBuffer = windowPosStack.callocInt(1);
            IntBuffer windowHeightBuffer = windowPosStack.callocInt(1);
            glfwGetWindowSize(handle, windowWidthBuffer, windowHeightBuffer);
            return new Vector2i(windowWidthBuffer.get(), windowHeightBuffer.get());
        }
    }

    public void setWindowPosition(int windowX, int windowY) {
        glfwSetWindowPos(handle, windowX, windowY);
    }

    public int getFrameBufferWidth() {
        return frameBufferWidth;
    }

    public int getFrameBufferHeight() {
        return frameBufferHeight;
    }

    /**
     * @return Whether the window is maximized
     */
    public boolean isMaximized() {
        return GLFWTOBoolean(glfwGetWindowAttrib(handle, GLFW_MAXIMIZED));
    }

    /**
     * Set window to be minimized
     */
    public void maximize() {
        glfwMaximizeWindow(handle);
    }

    /**
     * Set window to be minimized
     */
    public void minimize() {
        glfwRestoreWindow(handle);
    }

    /**
     * @return Whether the window is focused
     */
    public boolean isFocused() {
        return GLFWTOBoolean(glfwGetWindowAttrib(handle, GLFW_FOCUSED));
    }

    /**
     * Force window to be focused
     */
    public void setFocused(boolean state) {
        glfwSetWindowAttrib(handle, GLFW_FOCUSED, state ? GLFW_TRUE : GLFW_FALSE);
    }

    /**
     * Toggle window focus
     */
    public void toggleFocused() {
        setFocused(!isFocused());
    }

    /**
     * @return Whether the window is decorated
     */
    public boolean isDecorated() {
        return GLFWTOBoolean(glfwGetWindowAttrib(handle, GLFW_DECORATED));
    }

    /**
     * Set window to be decorated
     */
    public void setDecorated(boolean state) {
        glfwSetWindowAttrib(handle, GLFW_DECORATED, state ? GLFW_TRUE : GLFW_FALSE);
    }

    /**
     * Toggle window decorated
     */
    public void toggleDecorated() {
        setDecorated(!isDecorated());
    }

    /**
     * @return Whether the window is floating
     */
    public boolean isFloating() {
        return GLFWTOBoolean(glfwGetWindowAttrib(handle, GLFW_FLOATING));
    }

    /**
     * Set window to be floating
     */
    public void setFloating(boolean state) {
        glfwSetWindowAttrib(handle, GLFW_FLOATING, state ? GLFW_TRUE : GLFW_FALSE);
    }

    /**
     * Toggle window floating
     */
    public void toggleFloating() {
        setFloating(!isFloating());
    }


    /**
     * @return The window id
     */
    public long getHandle() {
        return handle;
    }

    /**
     * @return Whether the window is opened
     */
    public boolean isOpen() {
        return handle != 0;
    }

    protected int booleanToGLFW(boolean value) {
        return value ? GLFW_TRUE : GLFW_FALSE;
    }

    protected boolean GLFWTOBoolean(int value) {
        return value > 0;
    }

    private void setWindowIcon() {
        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            InputSupplier<InputStream>
                iconStreamSupplier16x = MinecraftClient.getInstance().getDefaultResourcePack()
                .openRoot("icons", "icon_16x16.png");
            InputSupplier<InputStream>
                iconStreamSupplier32x = MinecraftClient.getInstance().getDefaultResourcePack()
                .openRoot("icons", "icon_32x32.png");

            // Setup buffers
            GLFWImage.Buffer buffer = GLFWImage.malloc(2, memorystack);

            IntBuffer xBuffer = memorystack.mallocInt(1);
            IntBuffer yBuffer = memorystack.mallocInt(1);
            IntBuffer channelBuffer = memorystack.mallocInt(1);

            // Load 16x into buffer
            if (iconStreamSupplier16x == null) {
                throw new IllegalStateException(
                    "Could not find icon 16x");
            }

            InputStream iconStream16x = iconStreamSupplier16x.get();
            ByteBuffer byteBuffer16 = loadIcon(iconStream16x, xBuffer, yBuffer, channelBuffer);
            if (byteBuffer16 == null) {
                throw new IllegalStateException(
                    "Could not load icon: " + STBImage.stbi_failure_reason());
            }

            buffer.position(0);
            buffer.width(xBuffer.get(0));
            buffer.height(yBuffer.get(0));
            buffer.pixels(byteBuffer16);

            // Load 32x into buffer
            if (iconStreamSupplier32x == null) {
                throw new IllegalStateException(
                    "Could not find icon 32x");
            }

            InputStream iconStream32x = iconStreamSupplier32x.get();
            ByteBuffer byteBuffer32 = loadIcon(iconStream32x, xBuffer, yBuffer, channelBuffer);
            if (byteBuffer32 == null) {
                throw new IllegalStateException(
                    "Could not load icon: " + STBImage.stbi_failure_reason());
            }

            buffer.position(1);
            buffer.width(xBuffer.get(0));
            buffer.height(yBuffer.get(0));
            buffer.pixels(byteBuffer32);

            // Set icon
            buffer.position(0);
            glfwSetWindowIcon(handle, buffer);

            // Free byte buffers
            STBImage.stbi_image_free(byteBuffer16);
            STBImage.stbi_image_free(byteBuffer32);
        } catch (IOException e) {
            PIP_LOGGER.error("Failed to set icon", e);
        }
    }

    @Nullable
    private ByteBuffer loadIcon(InputStream textureStream, IntBuffer x, IntBuffer y,
                                IntBuffer channelInFile) throws IOException {
        RenderSystem.assertInInitPhase();

        ByteBuffer memoryByteBuffer = null;
        ByteBuffer imageByteBuffer;

        try {
            memoryByteBuffer = TextureUtil.readResource(textureStream);
            memoryByteBuffer.rewind();

            imageByteBuffer =
                STBImage.stbi_load_from_memory(memoryByteBuffer, x, y, channelInFile, 0);
        } finally {
            if (memoryByteBuffer != null) {
                MemoryUtil.memFree(memoryByteBuffer);
            }
        }

        return imageByteBuffer;
    }
}
