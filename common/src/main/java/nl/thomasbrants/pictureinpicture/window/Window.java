/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.resource.language.I18n;
import nl.thomasbrants.pictureinpicture.config.ModConfig;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import nl.thomasbrants.pictureinpicture.window.addons.*;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static nl.thomasbrants.pictureinpicture.PictureInPictureMod.PIP_LOGGER;
import static nl.thomasbrants.pictureinpicture.PictureInPictureModClient.getConfig;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window extends AbstractWindow {
    private static final List<String> RENDER_ORDER =
        new ArrayList<>(List.of("draggable", "zoom", "force-render-aspect-ratio"));

    private final List<WindowAddon> addons;
    private final boolean startFocused, startDecorated, startFloated;

    private String name;

    private boolean initialized = false;


    public static Window fromEntry(WindowEntry entry) {
        ModConfig config = getConfig();
        Window window =
            new Window(config.autoFocus, config.openDecorated, config.openFloated, entry.getName());
        window.updateFromEntry(entry);
        return window;
    }

    public void updateFromEntry(WindowEntry entry) {
        if (this.handle != NULL) {
            this.setName(entry.getName());
        }

        this.toggleAddon(DraggableAddon.class, entry.hasDraggable());
        this.toggleAddon(FloatableToggleAddon.class, entry.hasFloatableToggle());
        this.toggleAddon(DecoratedToggleAddon.class, entry.hasDecoratedToggle());
        this.toggleAddon(ForceRenderAspectRatioAddon.class, entry.hasForceRenderAspectRatio());
        this.toggleAddon(ForceWindowAspectRatioAddon.class, entry.hasForceWindowAspectRatio());
        this.toggleAddon(ZoomAddon.class, entry.hasZoom());
    }

    public Window(boolean startFocused, boolean startDecorated,
                  boolean startFloated, String name) {
        super(I18n.translate("text.picture-in-picture.title") + ": " + name);
        this.startFocused = startFocused;
        this.startDecorated = startDecorated;
        this.startFloated = startFloated;
        this.name = name;

        this.addons = new ArrayList<>();
    }

    /**
     * Opens the window.
     */
    public void create() {
        super.create();

        Vector2i size = getWindowSize();
        initialized = true;
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowAttributeAddon)
            .toList()) {
            ((WindowAttributeAddon) addon).onWindowInitialized(size.x, size.y);
        }
    }

    @Override
    protected void setHints() {
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, booleanToGLFW(startFocused));
        glfwWindowHint(GLFW_FLOATING, booleanToGLFW(startFloated));
        glfwWindowHint(GLFW_DECORATED, booleanToGLFW(startDecorated));
    }

    /**
     * Renders the window.
     */
    protected void renderWindow() {
        GL11.glMatrixMode(GL_MODELVIEW);
        glPushMatrix();

        List<WindowAddon> renderAddons =
            addons.stream().filter(addon -> addon instanceof WindowRenderAddon)
                .sorted(Comparator.comparingInt(a -> RENDER_ORDER.indexOf(a.getId())))
                .toList();

        // Render addons
        for (WindowAddon addon : renderAddons) {
            ((WindowRenderAddon) addon).render();
        }

        if (renderAddons.stream().noneMatch(addon -> ((WindowRenderAddon) addon).override())) {
            renderMinecraftFBO();
        }

        glPopMatrix();
    }

    private void renderMinecraftFBO() {
        Framebuffer minecraftFBO = MinecraftClient.getInstance().getFramebuffer();

        glBindTexture(GL_TEXTURE_2D,
            minecraftFBO.getColorAttachment());
        glEnable(GL_TEXTURE_2D);

        glColor4f(1f, 1f, 1f, 1f);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(-1, -1);
        glTexCoord2f(0, 1);
        glVertex2f(-1, 1);
        glTexCoord2f(1, 1);
        glVertex2f(1, 1);
        glTexCoord2f(1, 0);
        glVertex2f(1, -1);
        glEnd();
    }

    protected void onWindowMove(int windowX, int windowY) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowAttributeAddon)
            .toList()) {
            ((WindowAttributeAddon) addon).onWindowMove(windowX, windowY);
        }
    }

    protected void onFocus() {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowAttributeAddon)
            .toList()) {
            ((WindowAttributeAddon) addon).onWindowFocus();
        }
    }

    protected void onBlur() {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowAttributeAddon)
            .toList()) {
            ((WindowAttributeAddon) addon).onWindowBlur();
        }
    }

    protected void onKeyAction(int key, int scancode, int action, int mods) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onKeyAction(key, scancode, action, mods);
        }
    }

    protected void onScroll(double xOffset, double yOffset) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onScroll(xOffset, yOffset);
        }
    }

    protected void onDragStart(Vector2d mousePosition, int button, int mods) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onDragStart(mousePosition, button, mods);
        }
    }

    protected void onDragEnd(Vector2d mousePosition, int button, int mods) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onDragEnd(mousePosition, button, mods);
        }
    }

    protected void onClick(Vector2d mousePosition, int button, int mods) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onClick(mousePosition, button, mods);
        }
    }

    protected void onDoubleClick(Vector2d mousePosition, int button, int mods) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onDoubleClick(mousePosition, button, mods);
        }
    }

    protected void onMouseMove(Vector2d mousePosition) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowInputAddon)
            .toList()) {
            ((WindowInputAddon) addon).onMouseMove(mousePosition);
        }
    }

    protected void onWindowResize(int width, int height) {
        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowAttributeAddon)
            .toList()) {
            ((WindowAttributeAddon) addon).onWindowResize(width, height);
        }
    }

    public void onResolutionChanged() {
        net.minecraft.client.util.Window minecraftWindow =
            MinecraftClient.getInstance().getWindow();
        double windowWidth = minecraftWindow.getFramebufferWidth();
        double windowHeight = minecraftWindow.getFramebufferHeight();

        for (WindowAddon addon : addons.stream().filter(x -> x instanceof WindowAttributeAddon)
            .toList()) {
            ((WindowAttributeAddon) addon).onResolutionChanged(windowWidth, windowHeight);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setTitle(I18n.translate("text.picture-in-picture.title") + ": " + name);
    }

    public <T extends WindowAddon> boolean registerAddon(Class<T> addonClass) {
        try {
            Class[] parameterType = new Class[1];
            parameterType[0] = Window.class;

            WindowAddon addon = addonClass.getDeclaredConstructor(parameterType).newInstance(this);
            if (addons.stream().anyMatch(x -> x.getId().equals(addon.getId()))) {
                return true;
            }

            if (initialized && addon instanceof WindowAttributeAddon) {
                net.minecraft.client.util.Window minecraftWindow =
                    MinecraftClient.getInstance().getWindow();
                double windowWidth = minecraftWindow.getFramebufferWidth();
                double windowHeight = minecraftWindow.getFramebufferHeight();
                ((WindowAttributeAddon) addon).onWindowInitialized(windowWidth, windowHeight);
            }

            if (addon.add()) {
                addons.add(addon);
            }

            return true;
        } catch (Exception e) {
            PIP_LOGGER.warn("Error while adding addon: " + e);
            return false;
        }
    }

    public void removeAddon(String id) {
        addons.removeIf(x -> x.getId().equals(id) && x.remove());
    }

    public <T extends WindowAddon> boolean toggleAddon(Class<T> addonClass) {
        return toggleAddon(addonClass,
            addons.stream().filter(addonClass::isInstance).map(WindowAddon::getId)
                .findFirst().isEmpty());
    }

    public <T extends WindowAddon> boolean toggleAddon(Class<T> addonClass, boolean enable) {
        if (enable) {
            return registerAddon(addonClass);
        }

        removeAddon(
            addons.stream().filter(addonClass::isInstance).map(WindowAddon::getId)
                .findFirst()
                .orElse(""));
        return true;
    }
}
