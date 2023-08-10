package nl.thomasbrants.pictureinpicture;

import org.joml.Vector2d;
import org.joml.Vector2f;

public class MathUtils {
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static Vector2d clamp(Vector2d val, double min, double max) {
        return new Vector2d(clamp(val.x, min, max), clamp(val.y, min, max));
    }

    public static Vector2f clamp(Vector2f val, float min, float max) {
        return new Vector2f(clamp(val.x, min, max), clamp(val.y, min, max));
    }
}
