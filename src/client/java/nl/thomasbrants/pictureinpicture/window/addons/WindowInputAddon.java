/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import org.joml.Vector2d;

public interface WindowInputAddon {

    void onDragStart(Vector2d mousePosition, int button, int mods);

    void onDragEnd(Vector2d mousePosition, int button, int mods);

    void onClick(Vector2d mousePosition, int button, int mods);

    void onDoubleClick(Vector2d mousePosition, int button, int mods);

    void onMouseMove(Vector2d mousePosition);
}
