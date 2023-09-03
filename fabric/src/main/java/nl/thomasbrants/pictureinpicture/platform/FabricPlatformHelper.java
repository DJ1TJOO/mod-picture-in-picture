package nl.thomasbrants.pictureinpicture.platform;

import nl.thomasbrants.pictureinpicture.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Loader getPlatformName() {
        return Loader.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
