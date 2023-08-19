package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.Element;

public class WindowListLabelWidget implements Element {
    private final WindowListHeader windowListHeader;
    protected Rectangle rectangle = new Rectangle();

    public WindowListLabelWidget(WindowListHeader windowListHeader) {
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

        if (this.rectangle.contains(mouseX, mouseY)) {
            windowListHeader.toggleExpanded();
            return true;
        }

        return false;
    }
}
