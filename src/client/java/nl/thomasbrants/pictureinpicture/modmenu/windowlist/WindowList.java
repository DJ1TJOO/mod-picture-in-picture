package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.clothconfig2.api.ReferenceProvider;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WindowList extends AbstractConfigListEntry<List<WindowEntry>> implements
    Expandable {
    protected static final Identifier
        CONFIG_TEX = new Identifier("cloth-config2", "textures/gui/cloth_config.png");

    protected @NotNull Supplier<List<WindowEntry>> defaultValue;
    protected List<WindowEntry> original;

    protected final BiFunction<WindowEntry, WindowList, WindowListEntry>
        createNewEntry;
    protected @NotNull Function<WindowList, WindowListEntry>
        createNewInstance;
    protected Function<WindowEntry, Optional<Text>> entryErrorSupplier;
    private @Nullable Supplier<Optional<Text[]>> tooltipSupplier;

    protected final @NotNull List<WindowListEntry> entries;
    protected final @NotNull List<Object> widgets;

    protected boolean expanded;
    protected boolean insertButtonEnabled;
    protected boolean deleteButtonEnabled;
    protected boolean insertInFront;

    protected ListLabelWidget labelWidget;
    protected ClickableWidget resetWidget;

    protected @Nullable Text addTooltip;
    protected @Nullable Text removeTooltip;

    public WindowList(List<WindowEntry> value, Supplier<List<WindowEntry>> defaultValue,
                      Consumer<List<WindowEntry>> saveConsumer,
                      Supplier<Optional<Text[]>> tooltipSupplier, Text fieldName,
                      Text resetButtonKey, boolean insertInFront,
                      boolean deleteButtonEnabled, boolean defaultExpanded,
                      boolean requiresRestart) {
        this(value, defaultValue, saveConsumer, tooltipSupplier,
            fieldName, resetButtonKey, insertInFront, deleteButtonEnabled, defaultExpanded,
            requiresRestart,
            WindowListEntry::new);
    }

    public WindowList(List<WindowEntry> value, Supplier<List<WindowEntry>> defaultValue,
                      Consumer<List<WindowEntry>> saveConsumer,
                      Supplier<Optional<Text[]>> tooltipSupplier, Text fieldName,
                      Text resetButtonKey, boolean insertInFront,
                      boolean deleteButtonEnabled, boolean defaultExpanded,
                      boolean requiresRestart,
                      BiFunction<WindowEntry, WindowList, WindowListEntry> createNewEntry) {
        this(value, defaultValue, saveConsumer, (abstractListListEntry) -> createNewEntry.apply(
                null, abstractListListEntry), tooltipSupplier, fieldName, resetButtonKey, insertInFront,
            deleteButtonEnabled, defaultExpanded,
            requiresRestart,
            createNewEntry);
    }

    public WindowList(List<WindowEntry> value,
                      @NotNull Supplier<List<WindowEntry>> defaultValue,
                      @Nullable Consumer<List<WindowEntry>> saveConsumer,
                      @NotNull Function<WindowList, WindowListEntry> createNewInstance,
                      @Nullable Supplier<Optional<Text[]>> tooltipSupplier,
                      @NotNull Text fieldName,
                      Text resetButtonKey, boolean insertInFront,
                      boolean deleteButtonEnabled, boolean defaultExpanded,
                      boolean requiresRestart,
                      BiFunction<WindowEntry, WindowList, WindowListEntry> createNewEntry) {
        super(fieldName, requiresRestart);

        this.original = new ArrayList<>(value);
        this.defaultValue = defaultValue;
        this.saveCallback = saveConsumer;
        this.createNewInstance = createNewInstance;

        this.tooltipSupplier = tooltipSupplier;
        this.addTooltip = Text.translatable("text.cloth-config.list.add");
        this.removeTooltip = Text.translatable("text.cloth-config.list.remove");

        this.insertInFront = insertInFront;
        this.insertButtonEnabled = true;
        this.deleteButtonEnabled = deleteButtonEnabled;

        this.setExpanded(defaultExpanded);

        this.widgets = new ArrayList<>();
        this.createWidgets(defaultValue, resetButtonKey);

        this.entries = new ArrayList<>();
        this.createNewEntry = createNewEntry;
        this.createEntries(value);
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered,
            delta);

        // Tooltip
        if (this.isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<Text[]> tooltip = this.getTooltip(mouseX, mouseY);

            if (tooltip.isPresent() && tooltip.get().length > 0) {
                Point mousePosition = new Point(mouseX, mouseY);
                OrderedText[] tooltipText = this.postProcessTooltip(tooltip.get());

                this.addTooltip(Tooltip.of(mousePosition, tooltipText));
            }
        }

        // Textures
        RenderSystem.setShaderTexture(0, CONFIG_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        WindowListEntry active = getActive();

        boolean insideCreateNew = this.isInsideCreateNew(mouseX, mouseY);
        boolean insideDelete = this.isInsideDelete(mouseX, mouseY);

        // Arrow
        this.drawTexture(matrices, x - 15, y + 5, 33,
            (this.labelWidget.rectangle.contains(mouseX, mouseY) && !insideCreateNew &&
                !insideDelete ? 18 : 0) + (this.expanded ? 9 : 0), 9, 9);

        // Add and remove
        if (this.isInsertButtonEnabled()) {
            this.drawTexture(matrices, x - 15 + 13, y + 5, 42, insideCreateNew ? 9 : 0, 9, 9);
        }

        if (this.isDeleteButtonEnabled()) {
            this.drawTexture(matrices, x - 15 + (this.isInsertButtonEnabled() ? 26 : 13), y + 5, 51,
                active == null ? 0 : (insideDelete ? 18 : 9), 9, 9);
        }

        // Label
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
            this.getDisplayedFieldName().asOrderedText(),
            this.isDeleteButtonEnabled() ? (float) (x + 24) : (float) (x + 24 - 9), (float) (y + 6),
            this.labelWidget.rectangle.contains(mouseX, mouseY) &&
                !this.resetWidget.isMouseOver(mouseX, mouseY) && !insideDelete &&
                !insideCreateNew ? -1638890 : this.getPreferredTextColor());

        // Reset
        this.resetWidget.setX(x + entryWidth - this.resetWidget.getWidth());
        this.resetWidget.setY(y);
        this.resetWidget.active =
            this.isEditable() && this.getDefaultValue().isPresent() && !this.isMatchDefault();

        this.resetWidget.render(matrices, mouseX, mouseY, delta);

        // Render entries
        if (!this.expanded) {
            return;
        }

        int yy = y + 24;

        for (WindowListEntry entry : this.entries) {
            boolean isSelected = this.getParent().getFocused() != null &&
                this.getParent().getFocused().equals(this) &&
                this.getFocused() != null && this.getFocused().equals(entry);

            entry.render(matrices, -1, yy, x + 14, entryWidth - 14, entry.getEntryHeight(),
                mouseX, mouseY, isSelected, delta);

            yy += entry.getEntryHeight();
        }
    }

    @Nullable
    private WindowListEntry getActive() {
        if (!this.expanded || this.getFocused() == null ||
            !(this.getFocused() instanceof WindowListEntry)) {
            return null;
        }

        return (WindowListEntry) this.getFocused();
    }

    public void updateSelected(boolean isSelected) {
        for (WindowListEntry c : this.entries) {
            c.updateSelected(isSelected && this.getFocused() == c && this.expanded);
        }
    }

    private void createEntries(List<WindowEntry> value) {
        for (WindowEntry entry : value) {
            this.entries.add(createNewEntry.apply(entry, this));
        }

        this.widgets.addAll(this.entries);
    }

    private void createWidgets(@NotNull Supplier<List<WindowEntry>> defaultValue,
                               Text resetButtonKey) {
        this.labelWidget = new ListLabelWidget(this);
        this.widgets.add(this.labelWidget);

        this.resetWidget = ButtonWidget.builder(resetButtonKey, (widget) -> {
            this.widgets.removeAll(this.entries);

            this.entries.forEach(WindowListEntry::onDelete);
            this.entries.clear();

            this.entries.addAll(defaultValue.get().stream().map(this::getFromValue).toList());
            this.entries.forEach(WindowListEntry::onAdd);

            this.widgets.addAll(this.entries);
        }).dimensions(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6,
            20).build();
        this.widgets.add(this.resetWidget);
    }

    public List<? extends Element> children() {
        List<Element> elements = new ArrayList<>(this.widgets.stream()
            .filter(x -> x instanceof Element)
            .map(x -> (Element) x).toList());

        if (!this.expanded) {
            elements.removeAll(this.entries);
        }

        return elements;
    }

    public List<? extends Selectable> narratables() {
        return this.widgets.stream()
            .filter(x -> x instanceof Selectable)
            .map(x -> (Selectable) x).toList();
    }

    public int getItemHeight() {
        if (!this.expanded) {
            return 24;
        }

        return this.entries.stream().map(WindowListEntry::getEntryHeight)
            .reduce(24, Integer::sum);
    }

    public int getInitialReferenceOffset() {
        return 24;
    }


    public boolean isEdited() {
        if (super.isEdited() || this.entries.stream().anyMatch(WindowListEntry::isEdited)) {
            return true;
        }

        List<WindowEntry> value = this.getValue();
        if (value.size() != this.original.size()) {
            return true;
        }

        for (int i = 0; i < value.size(); i++) {
            WindowEntry oldEntry = original.get(i);
            WindowEntry newEntry = value.get(i);

            if (!oldEntry.isSame(newEntry)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMatchDefault() {
        Optional<List<WindowEntry>> defaultValueOptional = this.getDefaultValue();
        if (defaultValueOptional.isEmpty()) {
            return false;
        }

        List<WindowEntry> value = this.getValue();
        List<WindowEntry> defaultValue = defaultValueOptional.get();
        if (value.size() != defaultValue.size()) {
            return false;
        }

        for (int i = 0; i < value.size(); i++) {
            WindowEntry defaultEntry = defaultValue.get(i);
            WindowEntry currentEntry = value.get(i);

            if (!defaultEntry.isSame(currentEntry)) {
                return false;
            }
        }

        return true;
    }

    public Optional<List<WindowEntry>> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue.get());
    }

    public List<WindowEntry> getValue() {
        return this.entries.stream().map(WindowListEntry::getValue).toList();
    }

    protected WindowListEntry getFromValue(WindowEntry value) {
        return this.createNewEntry.apply(value, this);
    }

    public @NotNull Function<WindowList, WindowListEntry> getCreateNewInstance() {
        return this.createNewInstance;
    }

    public void setCreateNewInstance(
        @NotNull Function<WindowList, WindowListEntry> createNewInstance) {
        this.createNewInstance = createNewInstance;
    }

    public void save() {
        for (WindowListEntry c : this.entries) {
            if (c instanceof ReferenceProvider) {
                ((ReferenceProvider) c).provideReferenceEntry().save();
            }
        }

        super.save();
    }

    public void setEntryErrorSupplier(Function<WindowEntry, Optional<Text>> entryErrorSupplier) {
        this.entryErrorSupplier = entryErrorSupplier;
    }

    public Optional<Text> getError() {
        List<Text> errors = this.entries.stream()
            .map(WindowListEntry::getConfigError)
            .filter(Optional::isPresent)
            .map(Optional::get).toList();

        // TODO: maybe change this, to show first or all errors?
        return errors.size() > 1 ? Optional.of(Text.translatable("text.cloth-config.multi_error")) :
            errors.stream().findFirst();
    }

    public boolean insertInFront() {
        return this.insertInFront;
    }

    public boolean isRequiresRestart() {
        return this.entries.stream().anyMatch(WindowListEntry::isRequiresRestart);
    }

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

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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

    public @Nullable Supplier<Optional<Text[]>> getTooltipSupplier() {
        return this.tooltipSupplier;
    }

    public void setTooltipSupplier(@Nullable Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }

    private OrderedText[] postProcessTooltip(Text[] tooltip) {
        if (this.getConfigScreen() == null) {
            return new OrderedText[] {};
        }

        return Arrays.stream(tooltip)
            .flatMap((component) -> MinecraftClient.getInstance().textRenderer.wrapLines(component,
                this.getConfigScreen().width).stream())
            .toArray(OrderedText[]::new);
    }

    public Optional<Text[]> getTooltip(int mouseX, int mouseY) {
        if (this.addTooltip != null && this.isInsideCreateNew(mouseX, mouseY)) {
            return Optional.of(new Text[] {this.addTooltip});
        }

        if (this.removeTooltip != null &&
            this.isInsideDelete(mouseX, mouseY)) {
            return Optional.of(new Text[] {this.removeTooltip});
        }

        return this.getTooltipSupplier() != null ? this.getTooltipSupplier().get() :
            Optional.empty();
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
}