package nl.thomasbrants.pictureinpicture.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public abstract class AbstractListListEntry<T, C extends AbstractListListEntry.AbstractListCell<T, C, SELF>, SELF extends AbstractListListEntry<T, C, SELF>>
    extends
    BaseListEntry<T, C, SELF> {
    protected final BiFunction<T, SELF, C> createNewCell;
    protected Function<T, Optional<Text>> cellErrorSupplier;
    protected List<T> original;

    @ApiStatus.Internal
    public AbstractListListEntry(Text fieldName, List<T> value, boolean defaultExpanded,
                                 Supplier<Optional<Text[]>> tooltipSupplier,
                                 Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue,
                                 Text resetButtonKey, boolean requiresRestart,
                                 boolean deleteButtonEnabled, boolean insertInFront,
                                 BiFunction<T, SELF, C> createNewCell) {
        super(fieldName, tooltipSupplier, defaultValue,
            (abstractListListEntry) -> createNewCell.apply(
                null, abstractListListEntry), saveConsumer, resetButtonKey, requiresRestart,
            deleteButtonEnabled, insertInFront);
        this.createNewCell = createNewCell;
        this.original = new ArrayList<>(value);

        for (T f : value) {
            this.cells.add(
                createNewCell.apply(
                    f, this.self()));
        }

        this.widgets.addAll(this.cells);
        this.setExpanded(defaultExpanded);
    }

    public Function<T, Optional<Text>> getCellErrorSupplier() {
        return this.cellErrorSupplier;
    }

    public void setCellErrorSupplier(Function<T, Optional<Text>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
    }

    public List<T> getValue() {
        return this.cells.stream().map(AbstractListCell::getValue)
            .collect(
                Collectors.toList());
    }

    protected C getFromValue(T value) {
        return this.createNewCell.apply(
            value, this.self());
    }

    public boolean isEdited() {
        if (super.isEdited()) {
            return true;
        } else {
            List<T> value = this.getValue();
            if (value.size() != this.original.size()) {
                return true;
            } else {
                for (int i = 0; i < value.size(); ++i) {
                    if (!Objects.equals(value.get(i), this.original.get(i))) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    @ApiStatus.Internal
    public abstract static class AbstractListCell<T, SELF extends AbstractListListEntry.AbstractListCell<T, SELF, OUTER_SELF>, OUTER_SELF extends AbstractListListEntry<T, SELF, OUTER_SELF>>
        extends
        BaseListCell {
        protected final OUTER_SELF listListEntry;

        public AbstractListCell(@Nullable T value, OUTER_SELF listListEntry) {
            this.listListEntry = listListEntry;
            this.setErrorSupplier(() -> Optional.ofNullable(listListEntry.cellErrorSupplier)
                .flatMap((cellErrorFn) -> cellErrorFn.apply(this.getValue())));
        }

        public abstract T getValue();
    }
}
