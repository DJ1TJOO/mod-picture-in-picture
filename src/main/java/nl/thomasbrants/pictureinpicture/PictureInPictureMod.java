/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PictureInPictureMod implements ModInitializer {
    public static final String MOD_ID = "picture-in-picture";
    public static final Logger PIP_LOGGER = LoggerFactory.getLogger("Picture in Picture");

    @Override
    public void onInitialize() {
        PIP_LOGGER.info("Picture in Picture: Initialized");
    }
}