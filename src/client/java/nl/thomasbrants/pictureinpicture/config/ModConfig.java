/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Deserializer;

import java.util.ArrayList;
import java.util.List;

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

    @WindowEntry.WindowConfig
    public List<WindowEntry> windows = new ArrayList<>();

    @Deserializer
    public static ModConfig fromObject(JsonObject object) {
        ModConfig config = new ModConfig();

        config.autoFocus = object.getBoolean("autoFocus", config.autoFocus);
        config.openDecorated = object.getBoolean("autoFocus", config.openDecorated);
        config.openFloated = object.getBoolean("autoFocus", config.openFloated);

        Object windows = object.get(List.class, "windows");
        config.windows = new ArrayList<>();
        if (windows != null) {
            for (JsonObject window : (List<JsonObject>) windows) {
                WindowEntry entry = new WindowEntry(window.get(String.class, "name"),
                    window.getBoolean("draggable", false));
                config.windows.add(entry);
            }
        }

        return config;
    }
}
