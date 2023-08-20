/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import net.minecraft.client.gui.Element;

public class WindowListHeaderClickableWidget implements Element {
    private final WindowListHeader windowListHeader;

    public WindowListHeaderClickableWidget(WindowListHeader windowListHeader) {
        this.windowListHeader = windowListHeader;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (windowListHeader.isInsideReset(mouseX, mouseY)) {
            return false;
        }

        if (windowListHeader.isInsideCreateNew(mouseX, mouseY)) {
            windowListHeader.createNewEntry();
            return true;
        }

        if (windowListHeader.isDeleteButtonEnabled() &&
            windowListHeader.isInsideDelete(mouseX, mouseY)) {
            windowListHeader.deleteEntry();
            return true;
        }

        if (windowListHeader.isInsideHeader(mouseX, mouseY)) {
            windowListHeader.toggleExpanded();
            return true;
        }

        return false;
    }
}
