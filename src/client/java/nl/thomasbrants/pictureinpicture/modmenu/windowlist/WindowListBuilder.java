package nl.thomasbrants.pictureinpicture.modmenu.windowlist;

import me.shedaniel.clothconfig2.impl.builders.AbstractListBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class WindowListBuilder extends
    AbstractListBuilder<WindowEntry, WindowList, WindowListBuilder> {
    private Function<WindowList, WindowListEntry>
        createNewInstance;
    private final BiConsumer<List<WindowEntry>, List<WindowEntry>> saveConsumer;

    public WindowListBuilder(Text resetButtonKey, Text fieldNameKey, List<WindowEntry> value,
                             BiConsumer<List<WindowEntry>, List<WindowEntry>> saveConsumer) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.saveConsumer = saveConsumer;
    }

    public WindowListBuilder setCreateNewInstance(
        Function<WindowList, WindowListEntry> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }

    public @NotNull WindowList build() {
        WindowList entry =
            new WindowList(this.value, this.defaultValue,
                this.getSaveConsumer().andThen((List<WindowEntry> newValue) -> {
                    this.saveConsumer.accept(this.value, newValue);
                }), null, this.getFieldNameKey(), this.getResetButtonKey(), this.isInsertInFront(),
                this.isDeleteButtonEnabled(), this.isExpanded(),
                this.isRequireRestart()
            );

        if (this.createNewInstance != null) {
            entry.setCreateNewInstance(this.createNewInstance);
        }

        entry.setInsertButtonEnabled(this.isInsertButtonEnabled());
        entry.setEntryErrorSupplier(this.cellErrorSupplier);
        entry.setTooltipSupplier(() -> this.getTooltipSupplier().apply(entry.getValue()));
        entry.setAddTooltip(this.getAddTooltip());
        entry.setRemoveTooltip(this.getRemoveTooltip());

        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }

        return entry;
    }

}
