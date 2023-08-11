package nl.thomasbrants.pictureinpicture.modmenu;

import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ListLabelWidget implements Element {
    private final WindowListListEntry windowListListEntry;
    protected Rectangle rectangle = new Rectangle();

    public ListLabelWidget(WindowListListEntry windowListListEntry) {
        this.windowListListEntry = windowListListEntry;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (windowListListEntry.resetWidget.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (windowListListEntry.isInsideCreateNew(mouseX, mouseY)) {
            createNewCell();
            return true;
        }

        if (windowListListEntry.isDeleteButtonEnabled() &&
            windowListListEntry.isInsideDelete(mouseX, mouseY)) {
            deleteCell();
            return true;
        }

        if (this.rectangle.contains(mouseX, mouseY)) {
            toggleExpanded();
            return true;
        }

        return false;
    }

    private void createNewCell() {
        windowListListEntry.expanded = true;

        WindowListCell cell = windowListListEntry.createNewInstance.apply(windowListListEntry);

        if (windowListListEntry.insertInFront()) {
            windowListListEntry.cells.add(0, cell);
            windowListListEntry.widgets.add(0, cell);
        } else {
            windowListListEntry.cells.add(cell);
            windowListListEntry.widgets.add(cell);
        }

        cell.onAdd();
        playClickSound();
    }

    private void deleteCell() {
        Element focusedElement = windowListListEntry.getFocused();
        if (!windowListListEntry.expanded || !(focusedElement instanceof WindowListCell focused)) {
            return;
        }

        focused.onDelete();
        windowListListEntry.cells.remove(focused);
        windowListListEntry.widgets.remove(focused);

        playClickSound();
    }

    private void toggleExpanded() {
        windowListListEntry.expanded = !windowListListEntry.expanded;
        playClickSound();
    }

    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager()
            .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
