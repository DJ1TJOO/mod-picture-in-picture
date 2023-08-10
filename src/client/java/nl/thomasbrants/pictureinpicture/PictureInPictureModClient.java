/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import nl.thomasbrants.pictureinpicture.config.ModConfig;
import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import nl.thomasbrants.pictureinpicture.window.addons.*;

import java.util.ArrayList;
import java.util.List;

import static nl.thomasbrants.pictureinpicture.PictureInPictureMod.PIP_LOGGER;

public class PictureInPictureModClient implements ClientModInitializer {
    private static PictureInPictureModClient instance;

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

    private List<PictureInPictureWindow> pictureInPictureWindows;

    @Override
    public void onInitializeClient() {
        instance = this;
        this.pictureInPictureWindows = new ArrayList<>();

        PIP_LOGGER.info("Registering config");
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

        PIP_LOGGER.info("Registering screen events");
        ScreenEvents.AFTER_INIT.register(ScreenHandler::afterInitScreen);
    }

    public void createPictureInPictureWindow() {
        PictureInPictureWindow pictureInPictureWindow =
            new PictureInPictureWindow(getConfig().autoFocus, getConfig().openDecorated,
                getConfig().openFloated);

//        TODO: make window overview
        pictureInPictureWindow.registerAddon(DraggableAddon.class);
        pictureInPictureWindow.registerAddon(FloatableToggleAddon.class);
        pictureInPictureWindow.registerAddon(DecoratedToggleAddon.class);
        pictureInPictureWindow.registerAddon(ForceRenderAspectRatioAddon.class);
        pictureInPictureWindow.registerAddon(ForceWindowAspectRatioAddon.class);
        pictureInPictureWindow.registerAddon(ZoomAddon.class);

        pictureInPictureWindow.create();

        pictureInPictureWindows.add(pictureInPictureWindow);
    }

    public void destroyPictureInPictureWindow(PictureInPictureWindow pictureInPictureWindow) {
        pictureInPictureWindow.destroy();
        pictureInPictureWindows.remove(pictureInPictureWindow);
    }

    public void onResolutionChanged() {
        pictureInPictureWindows.forEach(PictureInPictureWindow::onResolutionChanged);
    }

    public void renderWindows() {
        pictureInPictureWindows.removeIf(x -> !x.isOpen());
        pictureInPictureWindows.forEach(PictureInPictureWindow::render);
    }
}