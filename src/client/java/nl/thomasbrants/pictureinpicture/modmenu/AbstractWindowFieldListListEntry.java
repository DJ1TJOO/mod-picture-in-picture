package nl.thomasbrants.pictureinpicture.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.thomasbrants.pictureinpicture.PictureInPictureMod;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class AbstractWindowFieldListListEntry<C extends AbstractWindowFieldListListEntry.AbstractWindowFieldListCell<C, SELF>, SELF extends AbstractWindowFieldListListEntry<C, SELF>>
    extends
    AbstractListListEntry<WindowEntry, C, SELF> {
    @ApiStatus.Internal
    public AbstractWindowFieldListListEntry(Text fieldName, List<WindowEntry> value,
                                            boolean defaultExpanded,
                                            Supplier<Optional<Text[]>> tooltipSupplier,
                                            Consumer<List<WindowEntry>> saveConsumer,
                                            Supplier<List<WindowEntry>> defaultValue,
                                            Text resetButtonKey,
                                            boolean requiresRestart, boolean deleteButtonEnabled,
                                            boolean insertInFront,
                                            BiFunction<WindowEntry, SELF, C> createNewCell) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue,
            resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, createNewCell);
    }

    @ApiStatus.Internal
    public abstract static class AbstractWindowFieldListCell<SELF extends AbstractWindowFieldListCell<SELF, OUTER_SELF>, OUTER_SELF extends AbstractWindowFieldListListEntry<SELF, OUTER_SELF>>
        extends AbstractListListEntry.AbstractListCell<WindowEntry, SELF, OUTER_SELF> {
        private static final Identifier DRAGGABLE_BUTTON_ICON =
            new Identifier(PictureInPictureMod.MOD_ID, "textures/gui/draggable.png");
        private static final int WIDGET_SPACING = 4;
        private static final int RIGHT_PADDING = 36;
        private static final String PLACEHOLDER =
            Text.translatable("text.picture-in-picture.window.placeholder")
                .getString();

        protected TextFieldWidget nameWidget;
        protected TexturedToggleButtonWidget draggableWidget;
        private boolean isSelected;
        private boolean isHovered;

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.nameWidget.mouseClicked(mouseX, mouseY, button);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public AbstractWindowFieldListCell(@Nullable WindowEntry value, OUTER_SELF listListEntry) {
            super(value, listListEntry);
            WindowEntry finalValue = this.substituteDefault(value);
            this.nameWidget =
                new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 18,
                    Text.empty());
            this.nameWidget.setSuggestion(finalValue.getName().length() > 0 ? "" : PLACEHOLDER);
            this.nameWidget.setTextPredicate(this::isValidText);
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
                    40, 40, finalValue.isDraggable(), (TexturedToggleButtonWidget button) -> {
                });
        }

        public void updateSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        protected abstract WindowEntry substituteDefault(@Nullable WindowEntry var1);

        protected abstract boolean isValidText(@NotNull String var1);

        public int getCellHeight() {
            return 20;
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
                           int entryHeight, int mouseX, int mouseY, boolean isSelected,
                           float delta) {
            int currentX = x;
            this.nameWidget.setWidth(
                entryWidth - WIDGET_SPACING - this.draggableWidget.getWidth() - RIGHT_PADDING);
            this.nameWidget.setX(currentX);
            this.nameWidget.setY(y + 1);
            this.nameWidget.setEditable(this.listListEntry.isEditable());
            this.nameWidget.render(matrices, mouseX, mouseY, delta);

            this.isHovered = this.nameWidget.isMouseOver(mouseX, mouseY);
            if (this.nameWidget.isActive()) {
                fill(matrices, x, y + 12, currentX + this.nameWidget.getWidth(), y + 13,
                    this.getConfigError().isPresent() ? -43691 : -2039584);
            }

            currentX += this.nameWidget.getWidth() + WIDGET_SPACING;
            this.draggableWidget.setX(currentX);
            this.draggableWidget.setY(y);
            this.draggableWidget.active = this.listListEntry.isEditable();
            this.draggableWidget.render(matrices, mouseX, mouseY, delta);

        }

        public List<? extends Element> children() {
            return List.of(this.nameWidget, this.draggableWidget);
        }

        public Selectable.SelectionType getType() {
            return this.isSelected ? SelectionType.FOCUSED :
                (this.isHovered ? SelectionType.HOVERED : SelectionType.NONE);
        }

        public void appendNarrations(NarrationMessageBuilder narrationElementOutput) {
            this.nameWidget.appendNarrations(narrationElementOutput);
            this.draggableWidget.appendNarrations(narrationElementOutput);
        }
    }
}