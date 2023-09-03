/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.config.windowlist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.thomasbrants.pictureinpicture.PictureInPictureMod;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import nl.thomasbrants.pictureinpicture.widgets.TexturedToggleButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Supplier;

public class WindowListEntry extends AbstractParentElement
    implements Selectable {
    private static final Identifier DRAGGABLE_BUTTON_ICON =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/draggable.png");
    private static final Identifier FLOATABLE_BUTTON_ICON =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/floatable.png");
    private static final Identifier DECORATED_TOGGLE_BUTTON_ICON =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/decorated_toggle.png");
    private static final Identifier FORCE_RENDER_ASPECT_RATIO_ICON =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/force_render_aspect_ratio.png");
    private static final Identifier FORCE_WINDOW_ASPECT_RATIO_ICON =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/force_window_aspect_ratio.png");
    private static final Identifier ZOOM_BUTTON_ICON =
        new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/zoom.png");
    private static final int WIDGET_SPACING = 4;
    private static final int RIGHT_PADDING = 36;
    private static final String PLACEHOLDER =
        Text.translatable("text.picture-in-picture.window.placeholder")
            .getString();

    private final @Nullable WindowEntry startEntry;
    private final WindowList windowList;
    private Supplier<Optional<Text>> errorSupplier;

    private TextFieldWidget nameWidget;
    private TexturedToggleButtonWidget draggableWidget;
    private TexturedToggleButtonWidget floatableToggleWidget;
    private TexturedToggleButtonWidget decoratedToggleWidget;
    private TexturedToggleButtonWidget forceRenderAspectRatioWidget;
    private TexturedToggleButtonWidget forceWindowAspectRatioWidget;
    private TexturedToggleButtonWidget zoomWidget;
    private boolean isSelected;
    private boolean isHovered;

    public WindowListEntry(@Nullable WindowEntry value,
                           WindowList windowList) {
        this.startEntry = value;
        this.windowList = windowList;

        this.createWidgets(this.substituteDefault(value));

        this.setErrorSupplier(() -> Optional.ofNullable(windowList.getEntryErrorSupplier())
            .flatMap((entryErrorFn) -> entryErrorFn.apply(this.getValue())));
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean isSelected,
                       float delta) {
        List<TexturedToggleButtonWidget> buttons = new ArrayList<>(
            List.of(draggableWidget, floatableToggleWidget, decoratedToggleWidget,
                forceRenderAspectRatioWidget, forceWindowAspectRatioWidget, zoomWidget));

        int buttonsWidth = buttons.stream().map(ClickableWidget::getWidth)
            .reduce(0, (prev, curr) -> prev + curr + WIDGET_SPACING);

        int currentX = x;

        this.nameWidget.setWidth(
            entryWidth - buttonsWidth - RIGHT_PADDING);
        this.nameWidget.setX(currentX);
        this.nameWidget.setY(y + 1);
        this.nameWidget.setEditable(this.windowList.isEditable());
        this.nameWidget.render(matrices, mouseX, mouseY, delta);

        this.isHovered = this.nameWidget.isMouseOver(mouseX, mouseY);
        if (this.nameWidget.isActive()) {
            fill(matrices, x, y + 12, currentX + this.nameWidget.getWidth(), y + 13,
                this.getConfigError().isPresent() ? -43691 : -2039584);
        }

        ListIterator<TexturedToggleButtonWidget> buttonsIterator = buttons.listIterator();
        while (buttonsIterator.hasNext()) {
            ClickableWidget
                previousWidget =
                buttonsIterator.hasPrevious() ? buttons.get(buttonsIterator.previousIndex()) :
                    this.nameWidget;

            TexturedToggleButtonWidget button = buttonsIterator.next();

            currentX += previousWidget.getWidth() + WIDGET_SPACING;
            button.setX(currentX);
            button.setY(y);
            button.active = this.windowList.isEditable();
            button.render(matrices, mouseX, mouseY, delta);
        }
    }

    private void createWidgets(WindowEntry finalValue) {
        this.nameWidget =
            new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18,
                Text.empty());
        this.nameWidget.setSuggestion(finalValue.getName().length() > 0 ? "" : PLACEHOLDER);
        this.nameWidget.setMaxLength(Integer.MAX_VALUE);
        this.nameWidget.setDrawsBackground(false);
        this.nameWidget.setText(finalValue.getName());
        this.nameWidget.setCursorToStart();
        this.nameWidget.setChangedListener(
            (s) -> {
                this.nameWidget.setSuggestion(s.length() > 0 ? "" : PLACEHOLDER);
                this.nameWidget.setEditableColor(this.getPreferredTextColor());
            });

        this.draggableWidget =
            new TexturedToggleButtonWidget(0, 0, 20, 20, 0, 0, 20, 20, DRAGGABLE_BUTTON_ICON,
                40, 40, finalValue.hasDraggable(), (TexturedToggleButtonWidget button) -> {
            });
        this.floatableToggleWidget =
            new TexturedToggleButtonWidget(0, 0, 20, 20, 0, 0, 20, 20, FLOATABLE_BUTTON_ICON,
                40, 40, finalValue.hasFloatableToggle(), (TexturedToggleButtonWidget button) -> {
            });
        this.decoratedToggleWidget =
            new TexturedToggleButtonWidget(0, 0, 20, 20, 0, 0, 20, 20, DECORATED_TOGGLE_BUTTON_ICON,
                40, 40, finalValue.hasDecoratedToggle(), (TexturedToggleButtonWidget button) -> {
            });
        this.forceRenderAspectRatioWidget =
            new TexturedToggleButtonWidget(0, 0, 20, 20, 0, 0, 20, 20,
                FORCE_RENDER_ASPECT_RATIO_ICON,
                40, 40, finalValue.hasForceRenderAspectRatio(),
                (TexturedToggleButtonWidget button) -> {
                });
        this.forceWindowAspectRatioWidget =
            new TexturedToggleButtonWidget(0, 0, 20, 20, 0, 0, 20, 20,
                FORCE_WINDOW_ASPECT_RATIO_ICON,
                40, 40, finalValue.hasForceWindowAspectRatio(),
                (TexturedToggleButtonWidget button) -> {
                });
        this.zoomWidget =
            new TexturedToggleButtonWidget(0, 0, 20, 20, 0, 0, 20, 20, ZOOM_BUTTON_ICON,
                40, 40, finalValue.hasZoom(), (TexturedToggleButtonWidget button) -> {
            });
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.nameWidget.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void appendNarrations(NarrationMessageBuilder narrationElementOutput) {
        this.nameWidget.appendNarrations(narrationElementOutput);
        this.draggableWidget.appendNarrations(narrationElementOutput);
        this.floatableToggleWidget.appendNarrations(narrationElementOutput);
        this.decoratedToggleWidget.appendNarrations(narrationElementOutput);
        this.forceRenderAspectRatioWidget.appendNarrations(narrationElementOutput);
        this.forceWindowAspectRatioWidget.appendNarrations(narrationElementOutput);
        this.zoomWidget.appendNarrations(narrationElementOutput);
    }

    public List<? extends Element> children() {
        return List.of(this.nameWidget, this.draggableWidget, this.floatableToggleWidget,
            this.decoratedToggleWidget,
            this.forceRenderAspectRatioWidget,
            this.forceWindowAspectRatioWidget,
            this.zoomWidget);
    }

    public SelectionType getType() {
        return this.isSelected ? SelectionType.FOCUSED :
            (this.isHovered ? SelectionType.HOVERED : SelectionType.NONE);
    }

    public WindowEntry getValue() {
        return new WindowEntry(this.nameWidget.getText(),
            startEntry != null ? startEntry.getHandle() : 0, this.draggableWidget.isToggled(),
            this.floatableToggleWidget.isToggled(),
            this.decoratedToggleWidget.isToggled(),
            this.forceRenderAspectRatioWidget.isToggled(),
            this.forceWindowAspectRatioWidget.isToggled(),
            this.zoomWidget.isToggled());
    }

    private WindowEntry substituteDefault(@Nullable WindowEntry value) {
        return value == null ? new WindowEntry("", false, false, false, false, false, false) :
            value;
    }

    public void updateSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getEntryHeight() {
        return 20;
    }

    public boolean isRequiresRestart() {
        return false;
    }

    public boolean isEdited() {
        return this.getConfigError().isPresent();
    }

    public void onAdd() {
    }

    public void onDelete() {

    }

    public final int getPreferredTextColor() {
        return this.getConfigError().isPresent() ? 16733525 : 14737632;
    }

    public void setErrorSupplier(Supplier<Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }

    public final Optional<Text> getConfigError() {
        return this.errorSupplier != null && this.errorSupplier.get().isPresent() ?
            this.errorSupplier.get() : this.getError();
    }

    public Optional<Text> getError() {
        WindowEntry value = this.getValue();
        if (value.getName().length() < 1) {
            return Optional.of(
                Text.translatable(
                    "text.picture-in-picture.window.name_too_short"));
        }


        List<WindowEntry> entriesWithName = this.windowList.getValue().stream()
            .filter(x -> x.getName().equals(value.getName())).toList();
        if (entriesWithName.size() > 1) {
            return Optional.of(
                Text.translatable("text.picture-in-picture.window.name_taken"));
        }

        return Optional.empty();
    }
}
