package nl.thomasbrants.pictureinpicture;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ForgeBase {
    
    public ForgeBase() {
        Constants.LOG.info("Picture in Picture: Forge");
        Base.init();
    }
}