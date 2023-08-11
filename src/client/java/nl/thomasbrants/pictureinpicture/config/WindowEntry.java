package nl.thomasbrants.pictureinpicture.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class WindowEntry {
    private String name;
    private long handle = 0;
    private boolean draggable;

    public WindowEntry(String name, boolean draggable) {
        this.name = name;
        this.draggable = draggable;
    }

    public WindowEntry(String name, boolean draggable, long handle) {
        this(name, draggable);
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "window entry: " + getName() + ", " + isDraggable();
    }

    public boolean isSame(WindowEntry x) {
        return x.getName().equals(getName()) && x.isDraggable() == isDraggable();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface WindowConfig {
    }
}
