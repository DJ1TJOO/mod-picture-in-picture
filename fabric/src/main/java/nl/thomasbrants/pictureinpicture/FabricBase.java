package nl.thomasbrants.pictureinpicture;

import net.fabricmc.api.ModInitializer;

public class FabricBase implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("Picture in Picture: Fabric");
        Base.init();
    }
}
