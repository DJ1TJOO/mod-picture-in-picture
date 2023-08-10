/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

/**
 * Config.
 */
@Config(name = "picture-in-picture")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean autoFocus = true;
    @ConfigEntry.Gui.Tooltip
    public boolean openDecorated = true;
    @ConfigEntry.Gui.Tooltip
    public boolean openFloated = false;

//    @WindowConfig
//    public List<String> windows = new ArrayList<>();
}
