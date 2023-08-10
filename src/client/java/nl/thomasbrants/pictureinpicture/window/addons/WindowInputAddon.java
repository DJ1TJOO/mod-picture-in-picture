/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

public interface WindowInputAddon {

    void onDragStart(double mouseX, double mouseY, int button, int mods);

    void onDragEnd(double mouseX, double mouseY, int button, int mods);

    void onClick(double mouseX, double mouseY, int button, int mods);

    void onDoubleClick(double mouseX, double mouseY, int button, int mods);

    void onMouseMove(double mouseX, double mouseY);
}
