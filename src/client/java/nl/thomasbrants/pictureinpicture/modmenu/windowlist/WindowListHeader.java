package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WindowListHeader extends DrawableHelper implements Element {
    protected static final Identifier
        CONFIG_TEX = new Identifier("cloth-config2", "textures/gui/cloth_config.png");

    private WindowListLabelWidget labelWidget;
    private ClickableWidget resetWidget;

    private final WindowList list;

    private boolean insertButtonEnabled;
    private boolean deleteButtonEnabled;

    public WindowListHeader(WindowList list, boolean insertButtonEnabled,
                            boolean deleteButtonEnabled, Text resetButtonKey,
                            ButtonWidget.PressAction resetAction) {
        this.list = list;
        this.insertButtonEnabled = insertButtonEnabled;
        this.deleteButtonEnabled = deleteButtonEnabled;

        this.createWidgets(resetButtonKey, resetAction);
    }

    private void createWidgets(Text resetButtonKey, ButtonWidget.PressAction resetAction) {
        this.labelWidget = new WindowListLabelWidget(this);

        this.resetWidget = ButtonWidget.builder(resetButtonKey, resetAction).dimensions(0, 0,
            MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6,
            20).build();
    }

    public List<Element> getWidgets() {
        return new ArrayList<>(List.of(labelWidget, resetWidget));
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        // Textures
        RenderSystem.setShaderTexture(0, CONFIG_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        WindowListEntry active = this.list.getActive();

        boolean insideCreateNew = this.isInsideCreateNew(mouseX, mouseY);
        boolean insideDelete = this.isInsideDelete(mouseX, mouseY);

        // Arrow
        this.drawTexture(matrices, x - 15, y + 5, 33,
            (this.labelWidget.rectangle.contains(mouseX, mouseY) && !insideCreateNew &&
                !insideDelete ? 18 : 0) + (this.list.isExpanded() ? 9 : 0), 9, 9);

        // Add and remove
        if (this.insertButtonEnabled) {
            this.drawTexture(matrices, x - 15 + 13, y + 5, 42, insideCreateNew ? 9 : 0, 9, 9);
        }

        if (this.deleteButtonEnabled) {
            this.drawTexture(matrices, x - 15 + (this.insertButtonEnabled ? 26 : 13),
                y + 5, 51,
                active == null ? 0 : (insideDelete ? 18 : 9), 9, 9);
        }

        // Label
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
            this.list.getDisplayedFieldName().asOrderedText(),
            this.deleteButtonEnabled ? (float) (x + 24) : (float) (x + 24 - 9),
            (float) (y + 6),
            this.labelWidget.rectangle.contains(mouseX, mouseY) &&
                !this.resetWidget.isMouseOver(mouseX, mouseY) && !insideDelete &&
                !insideCreateNew ? -1638890 : this.list.getPreferredTextColor());

        // Reset
        this.resetWidget.setX(x + entryWidth - this.resetWidget.getWidth());
        this.resetWidget.setY(y);
        this.resetWidget.active =
            this.list.isEditable() && this.list.getDefaultValue().isPresent() &&
                !this.list.isMatchDefault();

        this.resetWidget.render(matrices, mouseX, mouseY, delta);

    }

    public Optional<Text[]> getTooltip(int mouseX, int mouseY) {
        if (this.list.getAddTooltip() != null && this.isInsideCreateNew(mouseX, mouseY)) {
            return Optional.of(new Text[] {this.list.getAddTooltip()});
        }

        if (this.list.getRemoveTooltip() != null &&
            this.isInsideDelete(mouseX, mouseY)) {
            return Optional.of(new Text[] {this.list.getRemoveTooltip()});
        }

        return Optional.empty();
    }

    public void updateLabelWidget(int x, int y, int entryWidth) {
        this.labelWidget.rectangle.x = x - 15;
        this.labelWidget.rectangle.y = y;
        this.labelWidget.rectangle.width = entryWidth + 15;
        this.labelWidget.rectangle.height = 24;
    }

    boolean isInsideCreateNew(double mouseX, double mouseY) {
        return this.insertButtonEnabled &&
            mouseX >= (double) (this.labelWidget.rectangle.x + 12) &&
            mouseY >= (double) (this.labelWidget.rectangle.y + 3) &&
            mouseX <= (double) (this.labelWidget.rectangle.x + 12 + 11) &&
            mouseY <= (double) (this.labelWidget.rectangle.y + 3 + 11);
    }

    boolean isInsideDelete(double mouseX, double mouseY) {
        return this.deleteButtonEnabled && mouseX >=
            (double) (this.labelWidget.rectangle.x +
                (this.insertButtonEnabled ? 25 : 12)) &&
            mouseY >= (double) (this.labelWidget.rectangle.y + 3) && mouseX <=
            (double) (this.labelWidget.rectangle.x + (this.insertButtonEnabled ? 25 : 12) +
                11) && mouseY <= (double) (this.labelWidget.rectangle.y + 3 + 11);
    }

    boolean isInsideReset(double mouseX, double mouseY) {
        return this.resetWidget.isMouseOver(mouseX, mouseY);
    }

    public boolean isDeleteButtonEnabled() {
        return this.deleteButtonEnabled;
    }

    public void createNewEntry() {
        this.list.setExpanded(true);
        this.list.createNewEntry();
        playClickSound();
    }

    public void deleteEntry() {
        Element focusedElement = this.list.getFocused();
        if (!this.list.isExpanded() || !(focusedElement instanceof WindowListEntry focused)) {
            return;
        }

        this.list.removeEntry(focused);
        playClickSound();
    }

    public void toggleExpanded() {
        this.list.setExpanded(!this.list.isExpanded());
        playClickSound();
    }

    public void playClickSound() {
        MinecraftClient.getInstance().getSoundManager()
            .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void setInsertButtonEnabled(boolean insertButtonEnabled) {
        this.insertButtonEnabled = insertButtonEnabled;
    }

    public void setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
    }
}
