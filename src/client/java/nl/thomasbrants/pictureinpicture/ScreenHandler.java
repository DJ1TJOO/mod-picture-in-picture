/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.thomasbrants.pictureinpicture.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

import static nl.thomasbrants.pictureinpicture.PictureInPictureMod.PIP_LOGGER;

public class ScreenHandler {
    private static final Identifier POP_OUT_ICON_TEXTURE =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/pop_out.png");

    public static void afterInitScreen(MinecraftClient client, Screen screen, int windowWidth,
                                       int windowHeight) {

        // Create first windows when title screen is loaded for the first time
        if (PictureInPictureModClient.getInstance().isReadyToCreateWindows()) {
            PictureInPictureModClient.getInstance().setReadyToCreateWindows(false);
            PictureInPictureModClient.getInstance()
                .updateWindows(new ArrayList<>(), PictureInPictureModClient.getConfig().windows);
            PictureInPictureModClient.getInstance().onCreatedWindows();
        }

        if (screen instanceof TitleScreen) {
            PIP_LOGGER.info("Initializing {}", screen.getClass().getName());
            initTitleScreen(client, screen, windowWidth, windowHeight);
        }
    }

    private static void initTitleScreen(MinecraftClient client, Screen screen, int windowWidth,
                                        int windowHeight) {


        final List<ClickableWidget> buttons = Screens.getButtons(screen);

        buttons.add(
            new TexturedButtonWidget((screen.width / 2) + 20 + 108,
                ((screen.height / 4) + 48) + 72 + 12, 20, 20,
                0,
                0, 20, POP_OUT_ICON_TEXTURE, 20, 40,
                button -> {
                    MinecraftClient.getInstance().setScreen(
                        AutoConfig.getConfigScreen(ModConfig.class, screen).get());
//                    PictureInPictureModClient.getInstance().createPictureInPictureWindow();

                }, Text.translatable("narrator.picture-in-picture.pop_out")));
    }
}
