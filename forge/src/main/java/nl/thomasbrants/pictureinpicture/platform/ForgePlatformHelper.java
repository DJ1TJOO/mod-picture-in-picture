package nl.thomasbrants.pictureinpicture.platform;

import nl.thomasbrants.pictureinpicture.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public Loader getPlatformName() {
        return Loader.FORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}