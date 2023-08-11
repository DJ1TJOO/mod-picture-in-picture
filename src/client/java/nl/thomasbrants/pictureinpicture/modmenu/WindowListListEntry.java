package nl.thomasbrants.pictureinpicture.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WindowListListEntry extends
    AbstractWindowFieldListListEntry<WindowListListEntry.WindowListCell, WindowListListEntry> {
    /**
     * @deprecated
     */
    @Deprecated
    @ApiStatus.Internal
    public WindowListListEntry(Text fieldName, List<WindowEntry> value, boolean defaultExpanded,
                               Supplier<Optional<Text[]>> tooltipSupplier,
                               Consumer<List<WindowEntry>> saveConsumer,
                               Supplier<List<WindowEntry>> defaultValue, Text resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue,
            resetButtonKey, false);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @ApiStatus.Internal
    public WindowListListEntry(Text fieldName, List<WindowEntry> value, boolean defaultExpanded,
                               Supplier<Optional<Text[]>> tooltipSupplier,
                               Consumer<List<WindowEntry>> saveConsumer,
                               Supplier<List<WindowEntry>> defaultValue, Text resetButtonKey,
                               boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue,
            resetButtonKey, requiresRestart, true, true);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @ApiStatus.Internal
    public WindowListListEntry(Text fieldName, List<WindowEntry> value, boolean defaultExpanded,
                               Supplier<Optional<Text[]>> tooltipSupplier,
                               Consumer<List<WindowEntry>> saveConsumer,
                               Supplier<List<WindowEntry>> defaultValue, Text resetButtonKey,
                               boolean requiresRestart, boolean deleteButtonEnabled,
                               boolean insertInFront) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue,
            resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront,
            WindowListCell::new);
    }

    public WindowListListEntry self() {
        return this;
    }

    public static class WindowListCell extends
        AbstractWindowFieldListCell<WindowListCell, WindowListListEntry> {
        private final @Nullable WindowEntry startEntry;

        public WindowListCell(@Nullable WindowEntry value, WindowListListEntry listListEntry) {
            super(value, listListEntry);
            this.startEntry = value;
        }

        protected @Nullable WindowEntry substituteDefault(@Nullable WindowEntry value) {
            return value == null ? new WindowEntry("", false) : value;
        }

        protected boolean isValidText(@NotNull String text) {
            return true;
        }

        public WindowEntry getValue() {
            WindowEntry entry =
                new WindowEntry(this.nameWidget.getText(), this.draggableWidget.isToggled());
            
            if (startEntry != null) {
                entry.setHandle(startEntry.getHandle());
            }

            return entry;
        }

        public Optional<Text> getError() {
            WindowEntry value = this.getValue();
            if (value.getName().length() < 1) {
                return Optional.of(
                    Text.translatable(
                        "text.picture-in-picture.window.name_too_short"));
            }


            List<WindowEntry> entriesWithName = this.listListEntry.getValue().stream()
                .filter(x -> x.getName().equals(value.getName())).toList();

            if ((this.listListEntry.cells.stream().noneMatch(x -> x.nameWidget.isActive()) ||
                this.nameWidget.isActive()) && entriesWithName.size() > 1) {
                return Optional.of(
                    Text.translatable("text.picture-in-picture.window.name_taken"));
            }

            return Optional.empty();

        }
    }

}
