package nl.thomasbrants.pictureinpicture;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import nl.thomasbrants.pictureinpicture.config.ModConfig;
import nl.thomasbrants.pictureinpicture.platform.Services;

public class Base {
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

    public static void init() {
        Constants.LOG.info("Picture in Picture: Initialized");

        if (Services.PLATFORM.isModLoaded("minecraft")) {
            Constants.LOG.info("Hello to minecraft");
        }
    }
}