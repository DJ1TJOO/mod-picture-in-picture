/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window.addons;

import nl.thomasbrants.pictureinpicture.window.Window;

public abstract class WindowAddon {
    protected final Window window;
    protected final String id;

    public WindowAddon(Window window) {
        this("no-id", window);
    }

    protected WindowAddon(String id, Window window) {
        this.id = id;
        this.window = window;
    }

    public String getId() {
        return id;
    }

    public boolean add() {
        return true;
    }

    public boolean remove() {
        return true;
    }
}
