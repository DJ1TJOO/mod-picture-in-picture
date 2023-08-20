/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
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
import nl.thomasbrants.pictureinpicture.window.WindowManager;

import java.util.List;

import static nl.thomasbrants.pictureinpicture.PictureInPictureMod.PIP_LOGGER;

public class PictureInPictureModClient implements ClientModInitializer {
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

    @Override
    public void onInitializeClient() {
        PIP_LOGGER.info("Registering config");
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getGuiRegistry(ModConfig.class)
            .registerAnnotationProvider(registerWindowList(), WindowEntry.WindowConfig.class);

        PIP_LOGGER.info("Registering screen events");
        ScreenEvents.AFTER_INIT.register(ScreenHandler::afterInitScreen);

        PIP_LOGGER.info("Registering lifecycle events");
        ClientLifecycleEvents.CLIENT_STARTED.register(
            (MinecraftClient client) -> WindowManager.onReadyToCreateWindows());
    }

    private static GuiProvider registerWindowList() {
        Text resetButtonKey = Text.translatable("text.cloth-config.reset_value");

        return (i18n, field, config, defaults, registry1) ->
            List.of(
                new WindowListBuilder
                    (resetButtonKey, Text.translatable(i18n), Utils.getUnsafely(field, config),
                        WindowManager::updateWindows)
                    .setDefaultValue(() -> Utils.getUnsafely(field, defaults))
                    .setSaveConsumer((newValue) -> Utils.setUnsafely(field, config, newValue))
                    .build()
            );
    }
}