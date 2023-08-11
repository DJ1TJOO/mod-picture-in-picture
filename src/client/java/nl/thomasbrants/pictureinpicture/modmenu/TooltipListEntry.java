package nl.thomasbrants.pictureinpicture.modmenu;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class TooltipListEntry<T> extends AbstractConfigListEntry<T> {
    private @Nullable Supplier<Optional<Text[]>> tooltipSupplier;

    /**
     * @deprecated
     */
    @Deprecated
    @ApiStatus.Internal
    public TooltipListEntry(Text fieldName, @Nullable Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, tooltipSupplier, false);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @ApiStatus.Internal
    public TooltipListEntry(Text fieldName, @Nullable Supplier<Optional<Text[]>> tooltipSupplier,
                            boolean requiresRestart) {
        super(fieldName, requiresRestart);
        this.tooltipSupplier = tooltipSupplier;
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered,
            delta);
        if (this.isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<Text[]> tooltip = this.getTooltip(mouseX, mouseY);
            if (tooltip.isPresent() && tooltip.get().length > 0) {
                this.addTooltip(Tooltip.of(new Point(mouseX, mouseY),
                    this.postProcessTooltip(tooltip.get())));
            }
        }

    }

    private OrderedText[] postProcessTooltip(Text[] tooltip) {
        return Arrays.stream(tooltip).flatMap((component) -> {
            return MinecraftClient.getInstance().textRenderer.wrapLines(component,
                this.getConfigScreen().width).stream();
        }).toArray((x$0) -> {
            return new OrderedText[x$0];
        });
    }

    public Optional<Text[]> getTooltip() {
        return this.tooltipSupplier != null ? this.tooltipSupplier.get() :
            Optional.empty();
    }

    public Optional<Text[]> getTooltip(int mouseX, int mouseY) {
        return this.getTooltip();
    }

    public @Nullable Supplier<Optional<Text[]>> getTooltipSupplier() {
        return this.tooltipSupplier;
    }

    public void setTooltipSupplier(@Nullable Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }
}