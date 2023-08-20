package nl.thomasbrants.pictureinpicture.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class WindowEntry {
    private String name;
    private long handle = 0;
    private boolean hasDraggable;
    private boolean hasFloatableToggle;
    private boolean hasDecoratedToggle;
    private boolean hasForceRenderAspectRatio;
    private boolean hasForceWindowAspectRatio;
    private boolean hasZoom;

    public WindowEntry(String name, boolean hasDraggable, boolean hasFloatableToggle,
                       boolean hasDecoratedToggle, boolean hasForceRenderAspectRatio,
                       boolean hasForceWindowAspectRatio, boolean hasZoom) {
        this.name = name;
        this.hasDraggable = hasDraggable;
        this.hasFloatableToggle = hasFloatableToggle;
        this.hasDecoratedToggle = hasDecoratedToggle;
        this.hasForceRenderAspectRatio = hasForceRenderAspectRatio;
        this.hasForceWindowAspectRatio = hasForceWindowAspectRatio;
        this.hasZoom = hasZoom;
    }

    public WindowEntry(String name, long handle, boolean hasDraggable, boolean hasFloatableToggle,
                       boolean hasDecoratedToggle, boolean hasForceRenderAspectRatio,
                       boolean hasForceWindowAspectRatio, boolean hasZoom) {
        this(name, hasDraggable, hasFloatableToggle, hasDecoratedToggle, hasForceRenderAspectRatio,
            hasForceWindowAspectRatio, hasZoom);
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasDraggable() {
        return hasDraggable;
    }

    public void setHasDraggable(boolean hasDraggable) {
        this.hasDraggable = hasDraggable;
    }

    public boolean hasFloatableToggle() {
        return hasFloatableToggle;
    }

    public void setHasFloatableToggle(boolean hasFloatableToggle) {
        this.hasFloatableToggle = hasFloatableToggle;
    }

    public boolean hasDecoratedToggle() {
        return hasDecoratedToggle;
    }

    public void setHasDecoratedToggle(boolean hasDecoratedToggle) {
        this.hasDecoratedToggle = hasDecoratedToggle;
    }

    public boolean hasForceRenderAspectRatio() {
        return hasForceRenderAspectRatio;
    }

    public void setHasForceRenderAspectRatio(boolean hasForceRenderAspectRatio) {
        this.hasForceRenderAspectRatio = hasForceRenderAspectRatio;
    }

    public boolean hasForceWindowAspectRatio() {
        return hasForceWindowAspectRatio;
    }

    public void setHasForceWindowAspectRatio(boolean hasForceWindowAspectRatio) {
        this.hasForceWindowAspectRatio = hasForceWindowAspectRatio;
    }

    public boolean hasZoom() {
        return hasZoom;
    }

    public void setHasZoom(boolean hasZoom) {
        this.hasZoom = hasZoom;
    }

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "window entry: " + getName() + ", " + hasDraggable();
    }

    public boolean isSame(WindowEntry x) {
        return x.getName().equals(getName()) && x.hasDraggable() == hasDraggable() &&
            x.hasZoom() == hasZoom() &&
            x.hasDecoratedToggle() == hasDecoratedToggle() &&
            x.hasFloatableToggle() == hasFloatableToggle() &&
            x.hasForceRenderAspectRatio() == hasForceRenderAspectRatio() &&
            x.hasForceWindowAspectRatio() == hasForceWindowAspectRatio();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface WindowConfig {
    }
}
