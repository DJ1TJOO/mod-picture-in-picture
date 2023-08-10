/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;
import org.joml.Vector2d;

public class FloatableToggleAddon extends WindowAddon implements WindowInputAddon {
    public FloatableToggleAddon(PictureInPictureWindow window) {
        super("floatable", window);
    }

    @Override
    public void onDoubleClick(Vector2d mousePosition, int button, int mods) {
        window.toggleFloating();
    }

    @Override
    public void onDragStart(Vector2d mousePosition, int button, int mods) {

    }

    @Override
    public void onDragEnd(Vector2d mousePosition, int button, int mods) {

    }

    @Override
    public void onClick(Vector2d mousePosition, int button, int mods) {

    }

    @Override
    public void onMouseMove(Vector2d mousePosition) {

    }
}
