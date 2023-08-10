/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import nl.thomasbrants.pictureinpicture.window.addons.DraggableAddon;
import nl.thomasbrants.pictureinpicture.window.addons.FloatableAddon;

import java.util.ArrayList;
import java.util.List;

public class PictureInPictureModClient implements ClientModInitializer {
    private static PictureInPictureModClient instance;

    public static PictureInPictureModClient getInstance() {
        return instance;
    }

    private List<PictureInPictureWindow> pictureInPictureWindows;

    @Override
    public void onInitializeClient() {
        instance = this;

        this.pictureInPictureWindows = new ArrayList<>();

        ScreenEvents.AFTER_INIT.register(ScreenHandler::afterInitScreen);
    }

    public void createPictureInPictureWindow() {
        PictureInPictureWindow pictureInPictureWindow =
            new PictureInPictureWindow(false, true, false);
        pictureInPictureWindow.create();

        pictureInPictureWindow.registerAddon(DraggableAddon.class);
        pictureInPictureWindow.registerAddon(FloatableAddon.class);

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