/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;

public class FloatableAddon extends WindowAddon implements WindowInputAddon {
    public FloatableAddon(PictureInPictureWindow window) {
        super("floatable", window);
    }

    @Override
    public void onDoubleClick(double mouseX, double mouseY, int button, int mods) {
        window.toggleFloating();
    }

    @Override
    public void onDragStart(double mouseX, double mouseY, int button, int mods) {

    }

    @Override
    public void onDragEnd(double mouseX, double mouseY, int button, int mods) {

    }

    @Override
    public void onClick(double mouseX, double mouseY, int button, int mods) {

    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {

    }
}
