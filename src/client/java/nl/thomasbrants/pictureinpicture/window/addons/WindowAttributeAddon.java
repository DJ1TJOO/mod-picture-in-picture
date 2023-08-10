/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

public interface WindowAttributeAddon {
    void onWindowMove(int windowX, int windowY);

    void onWindowFocus();

    void onWindowBlur();

    void onWindowResize(int width, int height);

    void onResolutionChanged(double width, double height);

    void onWindowInitialized(double width, double height);
}
