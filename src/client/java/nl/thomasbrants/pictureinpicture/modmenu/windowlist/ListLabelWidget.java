package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ListLabelWidget implements Element {
    private final WindowList windowList;
    protected Rectangle rectangle = new Rectangle();

    public ListLabelWidget(WindowList windowList) {
        this.windowList = windowList;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (windowList.resetWidget.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (windowList.isInsideCreateNew(mouseX, mouseY)) {
            createNewEntry();
            return true;
        }

        if (windowList.isDeleteButtonEnabled() &&
            windowList.isInsideDelete(mouseX, mouseY)) {
            deleteEntry();
            return true;
        }

        if (this.rectangle.contains(mouseX, mouseY)) {
            toggleExpanded();
            return true;
        }

        return false;
    }

    private void createNewEntry() {
        windowList.expanded = true;

        WindowListEntry entry = windowList.createNewInstance.apply(windowList);

        if (windowList.insertInFront()) {
            windowList.entries.add(0, entry);
            windowList.widgets.add(0, entry);
        } else {
            windowList.entries.add(entry);
            windowList.widgets.add(entry);
        }

        entry.onAdd();
        playClickSound();
    }

    private void deleteEntry() {
        Element focusedElement = windowList.getFocused();
        if (!windowList.expanded || !(focusedElement instanceof WindowListEntry focused)) {
            return;
        }

        focused.onDelete();
        windowList.entries.remove(focused);
        windowList.widgets.remove(focused);

        playClickSound();
    }

    private void toggleExpanded() {
        windowList.expanded = !windowList.expanded;
        playClickSound();
    }

    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager()
            .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
