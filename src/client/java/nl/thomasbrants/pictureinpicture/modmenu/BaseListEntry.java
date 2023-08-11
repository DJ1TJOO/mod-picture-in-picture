package nl.thomasbrants.pictureinpicture.modmenu;


import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.clothconfig2.api.ReferenceProvider;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public abstract class BaseListEntry<T, C extends BaseListCell, SELF extends BaseListEntry<T, C, SELF>>
    extends
    TooltipListEntry<List<T>> implements Expandable {
    protected static final Identifier
        CONFIG_TEX = new Identifier("cloth-config2", "textures/gui/cloth_config.png");
    protected final @NotNull List<C> cells;
    protected final @NotNull List<Object> widgets;
    protected boolean expanded;
    protected boolean insertButtonEnabled;
    protected boolean deleteButtonEnabled;
    protected boolean insertInFront;
    protected ListLabelWidget labelWidget;
    protected ClickableWidget resetWidget;
    protected @NotNull Function<SELF, C> createNewInstance;
    protected @NotNull Supplier<List<T>> defaultValue;
    protected @Nullable Text addTooltip;
    protected @Nullable Text removeTooltip;

    @ApiStatus.Internal
    public BaseListEntry(@NotNull Text fieldName,
                         @Nullable Supplier<Optional<Text[]>> tooltipSupplier,
                         @Nullable Supplier<List<T>> defaultValue,
                         @NotNull Function<SELF, C> createNewInstance,
                         @Nullable Consumer<List<T>> saveConsumer, Text resetButtonKey) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer,
            resetButtonKey, false);
    }

    @ApiStatus.Internal
    public BaseListEntry(@NotNull Text fieldName,
                         @Nullable Supplier<Optional<Text[]>> tooltipSupplier,
                         @Nullable Supplier<List<T>> defaultValue,
                         @NotNull Function<SELF, C> createNewInstance,
                         @Nullable Consumer<List<T>> saveConsumer, Text resetButtonKey,
                         boolean requiresRestart) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer,
            resetButtonKey, requiresRestart, true, true);
    }

    @ApiStatus.Internal
    public BaseListEntry(@NotNull Text fieldName,
                         @Nullable Supplier<Optional<Text[]>> tooltipSupplier,
                         @Nullable Supplier<List<T>> defaultValue,
                         @NotNull Function<SELF, C> createNewInstance,
                         @Nullable Consumer<List<T>> saveConsumer, Text resetButtonKey,
                         boolean requiresRestart, boolean deleteButtonEnabled,
                         boolean insertInFront) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.insertButtonEnabled = true;
        this.addTooltip = Text.translatable("text.cloth-config.list.add");
        this.removeTooltip = Text.translatable("text.cloth-config.list.remove");
        this.deleteButtonEnabled = deleteButtonEnabled;
        this.insertInFront = insertInFront;
        this.cells = new ArrayList<>();
        this.labelWidget = new BaseListEntry.ListLabelWidget();
        this.widgets = new ArrayList<>(List.of(new Object[] {this.labelWidget}));
        this.resetWidget = ButtonWidget.builder(resetButtonKey, (widget) -> {
            this.widgets.removeAll(this.cells);
            Iterator<C> var3 = this.cells.iterator();

            BaseListCell cell;
            while (var3.hasNext()) {
                cell = var3.next();
                cell.onDelete();
            }

            this.cells.clear();
            Stream<C> var10000 = (defaultValue.get()).stream().map(this::getFromValue);
            List<C> var10001 = this.cells;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
            var3 = this.cells.iterator();

            while (var3.hasNext()) {
                cell = var3.next();
                cell.onAdd();
            }

            this.widgets.addAll(this.cells);
        }).dimensions(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6,
            20).build();
        this.widgets.add(this.resetWidget);
        this.saveCallback = saveConsumer;
        this.createNewInstance = createNewInstance;
        this.defaultValue = defaultValue;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isEdited() {
        return super.isEdited() || this.cells.stream().anyMatch(BaseListCell::isEdited);
    }

    public boolean isMatchDefault() {
        Optional<List<T>> defaultValueOptional = this.getDefaultValue();
        if (defaultValueOptional.isPresent()) {
            List<T> value = this.getValue();
            List<T> defaultValue = defaultValueOptional.get();
            if (value.size() != defaultValue.size()) {
                return false;
            } else {
                for (int i = 0; i < value.size(); ++i) {
                    if (!Objects.equals(value.get(i), defaultValue.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean isRequiresRestart() {
        return this.cells.stream().anyMatch(BaseListCell::isRequiresRestart);
    }

    public void setRequiresRestart(boolean requiresRestart) {
    }

    public abstract SELF self();

    public boolean isDeleteButtonEnabled() {
        return this.deleteButtonEnabled;
    }

    public boolean isInsertButtonEnabled() {
        return this.insertButtonEnabled;
    }

    public void setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
    }

    public void setInsertButtonEnabled(boolean insertButtonEnabled) {
        this.insertButtonEnabled = insertButtonEnabled;
    }

    protected abstract C getFromValue(T var1);

    public @NotNull Function<SELF, C> getCreateNewInstance() {
        return this.createNewInstance;
    }

    public void setCreateNewInstance(@NotNull Function<SELF, C> createNewInstance) {
        this.createNewInstance = createNewInstance;
    }

    public @Nullable Text getAddTooltip() {
        return this.addTooltip;
    }

    public void setAddTooltip(@Nullable Text addTooltip) {
        this.addTooltip = addTooltip;
    }

    public @Nullable Text getRemoveTooltip() {
        return this.removeTooltip;
    }

    public void setRemoveTooltip(@Nullable Text removeTooltip) {
        this.removeTooltip = removeTooltip;
    }

    public Optional<List<T>> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue.get());
    }

    public int getItemHeight() {
        if (!this.expanded) {
            return 24;
        } else {
            int i = 24;

            BaseListCell entry;
            for (Iterator<C> var2 = this.cells.iterator(); var2.hasNext();
                 i += entry.getCellHeight()) {
                entry = var2.next();
            }

            return i;
        }
    }

    public List<? extends Element> children() {
        List<Element> elements = new ArrayList<>(this.widgets.stream()
            .filter(x -> x instanceof Element)
            .map(x -> (Element) x).toList());

        if (!this.expanded) {
            elements.removeAll(this.cells);
        }

        return elements;
    }

    public List<? extends Selectable> narratables() {
        return this.widgets.stream()
            .filter(x -> x instanceof Selectable)
            .map(x -> (Selectable) x).toList();
    }

    public Optional<Text> getError() {
        List<Text> errors =
            this.cells.stream().map(BaseListCell::getConfigError).filter(Optional::isPresent)
                .map(Optional::get).toList();
        return errors.size() > 1 ? Optional.of(Text.translatable("text.cloth-config.multi_error")) :
            errors.stream().findFirst();
    }

    public void save() {

        for (C c : this.cells) {
            if (c instanceof ReferenceProvider) {
                ((ReferenceProvider) c).provideReferenceEntry().save();
            }
        }

        super.save();
    }

    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        this.labelWidget.rectangle.x = x - 15;
        this.labelWidget.rectangle.y = y;
        this.labelWidget.rectangle.width = entryWidth + 15;
        this.labelWidget.rectangle.height = 24;
        return new Rectangle(this.getParent().left, y,
            this.getParent().right - this.getParent().left, 20);
    }

    protected boolean isInsideCreateNew(double mouseX, double mouseY) {
        return this.isInsertButtonEnabled() &&
            mouseX >= (double) (this.labelWidget.rectangle.x + 12) &&
            mouseY >= (double) (this.labelWidget.rectangle.y + 3) &&
            mouseX <= (double) (this.labelWidget.rectangle.x + 12 + 11) &&
            mouseY <= (double) (this.labelWidget.rectangle.y + 3 + 11);
    }

    protected boolean isInsideDelete(double mouseX, double mouseY) {
        return this.isDeleteButtonEnabled() && mouseX >=
            (double) (this.labelWidget.rectangle.x + (this.isInsertButtonEnabled() ? 25 : 12)) &&
            mouseY >= (double) (this.labelWidget.rectangle.y + 3) && mouseX <=
            (double) (this.labelWidget.rectangle.x + (this.isInsertButtonEnabled() ? 25 : 12) +
                11) && mouseY <= (double) (this.labelWidget.rectangle.y + 3 + 11);
    }

    public Optional<Text[]> getTooltip(int mouseX, int mouseY) {
        if (this.addTooltip != null && this.isInsideCreateNew(mouseX, mouseY)) {
            return Optional.of(new Text[] {this.addTooltip});
        } else if (this.removeTooltip != null &&
            this.isInsideDelete(mouseX, mouseY)) {
            return Optional.of(new Text[] {this.removeTooltip});
        } else {
            return this.getTooltipSupplier() != null ? this.getTooltipSupplier().get() :
                Optional.empty();
        }
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered,
            delta);
        RenderSystem.setShaderTexture(0, CONFIG_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BaseListCell focused = this.expanded && this.getFocused() != null &&
            this.getFocused() instanceof BaseListCell ? (BaseListCell) this.getFocused() : null;
        boolean insideCreateNew = this.isInsideCreateNew(mouseX, mouseY);
        boolean insideDelete = this.isInsideDelete(mouseX, mouseY);
        this.drawTexture(matrices, x - 15, y + 5, 33,
            (this.labelWidget.rectangle.contains(mouseX, mouseY) && !insideCreateNew &&
                !insideDelete ? 18 : 0) + (this.expanded ? 9 : 0), 9, 9);
        if (this.isInsertButtonEnabled()) {
            this.drawTexture(matrices, x - 15 + 13, y + 5, 42, insideCreateNew ? 9 : 0, 9, 9);
        }

        if (this.isDeleteButtonEnabled()) {
            this.drawTexture(matrices, x - 15 + (this.isInsertButtonEnabled() ? 26 : 13), y + 5, 51,
                focused == null ? 0 : (insideDelete ? 18 : 9), 9, 9);
        }

        this.resetWidget.setX(x + entryWidth - this.resetWidget.getWidth());
        this.resetWidget.setY(y);
        this.resetWidget.active =
            this.isEditable() && this.getDefaultValue().isPresent() && !this.isMatchDefault();
        this.resetWidget.render(matrices, mouseX, mouseY, delta);
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
            this.getDisplayedFieldName().asOrderedText(),
            this.isDeleteButtonEnabled() ? (float) (x + 24) : (float) (x + 24 - 9), (float) (y + 6),
            this.labelWidget.rectangle.contains(mouseX, mouseY) &&
                !this.resetWidget.isMouseOver(mouseX, mouseY) && !insideDelete &&
                !insideCreateNew ? -1638890 : this.getPreferredTextColor());
        if (this.expanded) {
            int yy = y + 24;

            BaseListCell cell;
            for (Iterator<C> var15 = this.cells.iterator(); var15.hasNext();
                 yy += cell.getCellHeight()) {
                cell = var15.next();
                cell.render(matrices, -1, yy, x + 14, entryWidth - 14, cell.getCellHeight(), mouseX,
                    mouseY, this.getParent().getFocused() != null &&
                        this.getParent().getFocused().equals(this) &&
                        this.getFocused() != null && this.getFocused().equals(cell), delta);
            }
        }

    }

    public void updateSelected(boolean isSelected) {
        for (C c : this.cells) {
            c.updateSelected(
                isSelected && this.getFocused() == c && this.expanded);
        }
    }

    public int getInitialReferenceOffset() {
        return 24;
    }

    public boolean insertInFront() {
        return this.insertInFront;
    }

    public class ListLabelWidget implements Element {
        protected Rectangle rectangle = new Rectangle();

        public ListLabelWidget() {
        }

        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            if (BaseListEntry.this.resetWidget.isMouseOver(double_1, double_2)) {
                return false;
            } else if (BaseListEntry.this.isInsideCreateNew(double_1, double_2)) {
                BaseListEntry.this.expanded = true;
                C cell;
                if (BaseListEntry.this.insertInFront()) {
                    BaseListEntry.this.cells.add(0,
                        cell = BaseListEntry.this.createNewInstance.apply(
                            BaseListEntry.this.self()));
                    BaseListEntry.this.widgets.add(0, cell);
                } else {
                    BaseListEntry.this.cells.add(
                        cell = BaseListEntry.this.createNewInstance.apply(
                            BaseListEntry.this.self()));
                    BaseListEntry.this.widgets.add(cell);
                }

                cell.onAdd();
                MinecraftClient.getInstance().getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (BaseListEntry.this.isDeleteButtonEnabled() &&
                BaseListEntry.this.isInsideDelete(double_1, double_2)) {
                Element focused = BaseListEntry.this.getFocused();
                if (BaseListEntry.this.expanded && focused instanceof BaseListCell) {
                    ((BaseListCell) focused).onDelete();
                    BaseListEntry.this.cells.remove((C) focused);
                    BaseListEntry.this.widgets.remove(focused);
                    MinecraftClient.getInstance().getSoundManager()
                        .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                return true;
            } else if (this.rectangle.contains(double_1, double_2)) {
                BaseListEntry.this.expanded = !BaseListEntry.this.expanded;
                MinecraftClient.getInstance().getSoundManager()
                    .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else {
                return false;
            }
        }
    }
}
