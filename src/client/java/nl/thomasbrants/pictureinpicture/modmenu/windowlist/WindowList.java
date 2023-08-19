package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.clothconfig2.api.ReferenceProvider;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
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
import java.util.stream.Stream;

public class WindowList extends AbstractConfigListEntry<List<WindowEntry>> implements
    Expandable {

    private final @NotNull Supplier<List<WindowEntry>> defaultValue;
    private final List<WindowEntry> original;

    private final BiFunction<WindowEntry, WindowList, WindowListEntry>
        createNewEntry;
    private @NotNull Function<WindowList, WindowListEntry>
        createNewInstance;
    private Function<WindowEntry, Optional<Text>> entryErrorSupplier;
    private @Nullable Supplier<Optional<Text[]>> tooltipSupplier;

    private final @NotNull List<WindowListEntry> entries;
    private final WindowListHeader header;

    private boolean expanded;
    private boolean insertInFront;

    private @Nullable Text addTooltip;
    private @Nullable Text removeTooltip;

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

        this.setExpanded(defaultExpanded);

        this.entries = new ArrayList<>();
        this.createNewEntry = createNewEntry;
        this.createEntries(value);

        this.header =
            new WindowListHeader(this, true, deleteButtonEnabled, resetButtonKey, (widget) -> {
                this.entries.forEach(WindowListEntry::onDelete);
                this.entries.clear();

                this.entries.addAll(defaultValue.get().stream().map(this::getFromValue).toList());
                this.entries.forEach(WindowListEntry::onAdd);
            });
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

        this.header.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY,
            isHovered,
            delta);

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
    public WindowListEntry getActive() {
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
    }

    public List<? extends Element> children() {
        List<Element> elements = this.header.getWidgets();

        if (this.expanded) {
            elements.addAll(this.entries);
        }

        return elements;
    }

    public List<? extends Selectable> narratables() {
        return Stream.of(this.header.getWidgets(), this.entries)
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

    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        this.header.updateLabelWidget(x, y, entryWidth);
        return new Rectangle(this.getParent().left, y,
            this.getParent().right - this.getParent().left, 20);
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

    public boolean isRequiresRestart() {
        return this.entries.stream().anyMatch(WindowListEntry::isRequiresRestart);
    }

    public void setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
    }

    public void setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.header.setDeleteButtonEnabled(deleteButtonEnabled);
    }

    public void setInsertButtonEnabled(boolean insertButtonEnabled) {
        this.header.setInsertButtonEnabled(insertButtonEnabled);
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
        Optional<Text[]> headerTooltip = header.getTooltip(mouseX, mouseY);
        if (headerTooltip.isPresent()) {
            return headerTooltip;
        }

        return this.getTooltipSupplier() != null ? this.getTooltipSupplier().get() :
            Optional.empty();
    }

    public void addEntry(WindowListEntry entry) {
        if (this.insertInFront) {
            this.entries.add(0, entry);
        } else {
            this.entries.add(entry);
        }

        entry.onAdd();
    }

    public void createNewEntry() {
        WindowListEntry entry = this.getCreateNewInstance().apply(this);
        this.addEntry(entry);
    }

    public void removeEntry(WindowListEntry focused) {
        focused.onDelete();
        this.entries.remove(focused);
    }

    public Function<WindowEntry, Optional<Text>> getEntryErrorSupplier() {
        return entryErrorSupplier;
    }
}