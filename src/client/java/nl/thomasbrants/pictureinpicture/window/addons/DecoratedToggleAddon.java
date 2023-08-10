/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.PictureInPictureWindow;

public class DecoratedToggleAddon extends WindowAddon implements WindowAttributeAddon {
    public DecoratedToggleAddon(PictureInPictureWindow window) {
        super("decorated", window);
    }

    @Override
    public void onWindowFocus() {
        window.setDecorated(true);
    }

    @Override
    public void onWindowBlur() {
        window.setDecorated(false);
    }

    @Override
    public void onWindowResize(int width, int height) {

    }

    @Override
    public void onResolutionChanged(double width, double height) {

    }

    @Override
    public void onWindowInitialized(double width, double height) {
        
    }

    @Override
    public void onWindowMove(int windowX, int windowY) {

    }
}
