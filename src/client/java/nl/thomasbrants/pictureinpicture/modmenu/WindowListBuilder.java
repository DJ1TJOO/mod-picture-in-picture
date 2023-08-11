package nl.thomasbrants.pictureinpicture.modmenu;

import me.shedaniel.clothconfig2.impl.builders.AbstractListBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WindowListBuilder extends
    AbstractListBuilder<WindowEntry, WindowListListEntry, WindowListBuilder> {
    private Function<WindowListListEntry, WindowListListEntry.WindowListCell> createNewInstance;
    private final BiConsumer<List<WindowEntry>, List<WindowEntry>> saveConsumer;

    public WindowListBuilder(Text resetButtonKey, Text fieldNameKey, List<WindowEntry> value,
                             BiConsumer<List<WindowEntry>, List<WindowEntry>> saveConsumer) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.saveConsumer = saveConsumer;
    }

    public Function<WindowEntry, Optional<Text>> getCellErrorSupplier() {
        return super.getCellErrorSupplier();
    }

    public WindowListBuilder setCellErrorSupplier(
        Function<WindowEntry, Optional<Text>> cellErrorSupplier) {
        return super.setCellErrorSupplier(
            cellErrorSupplier);
    }

    public WindowListBuilder setErrorSupplier(
        Function<List<WindowEntry>, Optional<Text>> errorSupplier) {
        return super.setErrorSupplier(
            errorSupplier);
    }

    public WindowListBuilder setDeleteButtonEnabled(
        boolean deleteButtonEnabled) {
        return super.setDeleteButtonEnabled(
            deleteButtonEnabled);
    }

    public WindowListBuilder setInsertInFront(
        boolean insertInFront) {
        return super.setInsertInFront(
            insertInFront);
    }

    public WindowListBuilder setAddButtonTooltip(
        Text addTooltip) {
        return super.setAddButtonTooltip(
            addTooltip);
    }

    public WindowListBuilder setRemoveButtonTooltip(
        Text removeTooltip) {
        return super.setRemoveButtonTooltip(
            removeTooltip);
    }

    public WindowListBuilder requireRestart() {
        return super.requireRestart();
    }

    public WindowListBuilder setCreateNewInstance(
        Function<WindowListListEntry, WindowListListEntry.WindowListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }

    public WindowListBuilder setExpanded(boolean expanded) {
        return super.setExpanded(
            expanded);
    }

    public WindowListBuilder setSaveConsumer(
        Consumer<List<WindowEntry>> saveConsumer) {
        return super.setSaveConsumer(
            saveConsumer);
    }

    public WindowListBuilder setDefaultValue(
        Supplier<List<WindowEntry>> defaultValue) {
        return super.setDefaultValue(
            defaultValue);
    }

    public WindowListBuilder setDefaultValue(
        List<WindowEntry> defaultValue) {
        return super.setDefaultValue(
            defaultValue);
    }

    public WindowListBuilder setTooltipSupplier(
        Function<List<WindowEntry>, Optional<Text[]>> tooltipSupplier) {
        return super.setTooltipSupplier(
            tooltipSupplier);
    }

    public WindowListBuilder setTooltipSupplier(
        Supplier<Optional<Text[]>> tooltipSupplier) {
        return super.setTooltipSupplier(
            tooltipSupplier);
    }

    public WindowListBuilder setTooltip(
        Optional<Text[]> tooltip) {
        return super.setTooltip(
            tooltip);
    }

    public WindowListBuilder setTooltip(Text... tooltip) {
        return super.setTooltip(
            tooltip);
    }

    public @NotNull WindowListListEntry build() {
        WindowListListEntry entry =
            new WindowListListEntry(this.getFieldNameKey(), this.value, this.isExpanded(),
                null, this.getSaveConsumer().andThen((List<WindowEntry> newValue) -> {
                this.saveConsumer.accept(this.value, newValue);
            }), this.defaultValue,
                this.getResetButtonKey(), this.isRequireRestart(), this.isDeleteButtonEnabled(),
                this.isInsertInFront());
        if (this.createNewInstance != null) {
            entry.setCreateNewInstance(this.createNewInstance);
        }

        entry.setInsertButtonEnabled(this.isInsertButtonEnabled());
        entry.setCellErrorSupplier(this.cellErrorSupplier);
        entry.setTooltipSupplier(() -> this.getTooltipSupplier().apply(entry.getValue()));
        entry.setAddTooltip(this.getAddTooltip());
        entry.setRemoveTooltip(this.getRemoveTooltip());
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }

        return entry;
    }

}
