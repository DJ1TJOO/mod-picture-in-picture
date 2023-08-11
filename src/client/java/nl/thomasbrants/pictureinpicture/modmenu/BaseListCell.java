package nl.thomasbrants.pictureinpicture.modmenu;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class BaseListCell extends AbstractParentElement implements Selectable {
    private Supplier<Optional<Text>> errorSupplier;

    public BaseListCell() {
    }

    public final int getPreferredTextColor() {
        return this.getConfigError().isPresent() ? 16733525 : 14737632;
    }

    public final Optional<Text> getConfigError() {
        return this.errorSupplier != null && this.errorSupplier.get().isPresent() ?
            this.errorSupplier.get() : this.getError();
    }

    public void setErrorSupplier(Supplier<Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
    }

    public abstract Optional<Text> getError();

    public abstract int getCellHeight();

    public abstract void render(MatrixStack var1, int var2, int var3, int var4, int var5, int var6,
                                int var7, int var8, boolean var9, float var10);

    public void updateSelected(boolean isSelected) {
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
}
