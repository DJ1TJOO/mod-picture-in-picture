/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import nl.thomasbrants.pictureinpicture.config.ModConfig;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import nl.thomasbrants.pictureinpicture.modmenu.windowlist.WindowListBuilder;
import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import nl.thomasbrants.pictureinpicture.window.addons.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.thomasbrants.pictureinpicture.PictureInPictureMod.PIP_LOGGER;

public class PictureInPictureModClient implements ClientModInitializer {
    private static PictureInPictureModClient instance;
    private boolean readyToCreateWindows = false;
    private boolean createdWindows = false;

    public static PictureInPictureModClient getInstance() {
        return instance;
    }

    private static ModConfig _modConfig;

    public static ModConfig getConfig() {
        if (_modConfig != null) {
            return _modConfig;
        }

        ConfigHolder<ModConfig> configHolder = AutoConfig.getConfigHolder(ModConfig.class);
        return (_modConfig = configHolder.getConfig());
    }

    public static void saveConfig() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    private List<PictureInPictureWindow> pictureInPictureWindows;

    @Override
    public void onInitializeClient() {
        instance = this;
        this.pictureInPictureWindows = new ArrayList<>();

        PIP_LOGGER.info("Registering config");
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

        Text resetButtonKey = Text.translatable("text.cloth-config.reset_value");
        AutoConfig.getGuiRegistry(ModConfig.class)
            .registerAnnotationProvider(
                (i18n, field, config, defaults, registry1) ->
                    Collections.singletonList(
                        new WindowListBuilder(resetButtonKey, Text.translatable(i18n),
                            Utils.getUnsafely(field, config), this::updateWindows)
                            .setDefaultValue(() -> Utils.getUnsafely(field, defaults))
                            .setSaveConsumer(
                                (newValue) -> Utils.setUnsafely(field, config, newValue))
                            .build())
                , WindowEntry.WindowConfig.class);

        PIP_LOGGER.info("Registering screen events");
        ScreenEvents.AFTER_INIT.register(ScreenHandler::afterInitScreen);

        PIP_LOGGER.info("Registering lifecycle events");
        ClientLifecycleEvents.CLIENT_STARTED.register(
            (MinecraftClient client) -> readyToCreateWindows = true);
    }

    void updateWindows(List<WindowEntry> oldWindows, List<WindowEntry> newWindows) {
        // Close removed newWindows
        List<Long> windowsToDestroy =
            oldWindows.stream()
                .map(WindowEntry::getHandle)
                .filter(handle ->
                    newWindows.stream().noneMatch(y -> handle == y.getHandle()))
                .toList();
        destroyPictureInPictureWindows(windowsToDestroy);

        // Update existing
        List<WindowEntry> windowsToRename =
            newWindows.stream()
                .filter(x -> x.getHandle() != 0)
                .filter(x -> oldWindows.stream()
                    .anyMatch(
                        y -> y.getHandle() == x.getHandle() && !y.isSame(x)))
                .toList();

        for (WindowEntry entry : windowsToRename) {
            PictureInPictureWindow window = getWindow(entry.getHandle());
            if (window == null) {
                continue;
            }

            window.setName(entry.getName());
            window.toggleAddon(DraggableAddon.class, entry.hasDraggable());
            window.toggleAddon(FloatableToggleAddon.class, entry.hasFloatableToggle());
            window.toggleAddon(DecoratedToggleAddon.class, entry.hasDecoratedToggle());
            window.toggleAddon(ForceRenderAspectRatioAddon.class,
                entry.hasForceRenderAspectRatio());
            window.toggleAddon(ForceWindowAspectRatioAddon.class,
                entry.hasForceWindowAspectRatio());
            window.toggleAddon(ZoomAddon.class, entry.hasZoom());
        }

        // Open new newWindows
        List<WindowEntry> windowsToOpen =
            newWindows.stream()
                .filter(x -> x.getHandle() == 0)
                .toList();
        for (WindowEntry entry : windowsToOpen) {
            createPictureInPictureWindow(entry);
            getConfig().windows.get(newWindows.indexOf(entry))
                .setHandle(entry.getHandle());
        }

        saveConfig();
    }

    public void createPictureInPictureWindow(WindowEntry entry) {
        PictureInPictureWindow window =
            new PictureInPictureWindow(getConfig().autoFocus, getConfig().openDecorated,
                getConfig().openFloated, entry.getName());

        //        TODO: create hide addon
        window.toggleAddon(DraggableAddon.class, entry.hasDraggable());
        window.toggleAddon(FloatableToggleAddon.class, entry.hasFloatableToggle());
        window.toggleAddon(DecoratedToggleAddon.class, entry.hasDecoratedToggle());
        window.toggleAddon(ForceRenderAspectRatioAddon.class, entry.hasForceRenderAspectRatio());
        window.toggleAddon(ForceWindowAspectRatioAddon.class, entry.hasForceWindowAspectRatio());
        window.toggleAddon(ZoomAddon.class, entry.hasZoom());

        window.create();
        entry.setHandle(window.getHandle());
        pictureInPictureWindows.add(window);
    }

    private @Nullable PictureInPictureWindow getWindow(long handle) {
        return pictureInPictureWindows.stream().filter(x -> x.getHandle() == handle).findFirst()
            .orElse(null);
    }

    public void destroyPictureInPictureWindows(List<Long> handles) {
        for (PictureInPictureWindow window : pictureInPictureWindows) {
            if (handles.contains(window.getHandle())) {
                window.destroy();
            }
        }
    }

    public void destroyPictureInPictureWindow(PictureInPictureWindow pictureInPictureWindow) {
        pictureInPictureWindow.destroy();
    }

    public void onResolutionChanged() {
        pictureInPictureWindows.forEach(PictureInPictureWindow::onResolutionChanged);
    }

    public void renderWindows() {
        if (!createdWindows) {
            return;
        }

        pictureInPictureWindows.removeIf(window -> !window.isOpen());

        ModConfig config = getConfig();
        List<WindowEntry> newWindows = new ArrayList<>(config.windows);
        newWindows.removeIf(windowEntry -> pictureInPictureWindows.stream()
            .noneMatch(window -> window.getHandle() == windowEntry.getHandle()));
        config.windows = newWindows;
        saveConfig();

        pictureInPictureWindows.forEach(PictureInPictureWindow::render);
    }

    public List<PictureInPictureWindow> getPictureInPictureWindows() {
        return pictureInPictureWindows;
    }

    public boolean isReadyToCreateWindows() {
        return readyToCreateWindows;
    }

    void setReadyToCreateWindows(boolean readyToCreateWindows) {
        this.readyToCreateWindows = readyToCreateWindows;
    }

    void onCreatedWindows() {
        this.createdWindows = true;
    }
}