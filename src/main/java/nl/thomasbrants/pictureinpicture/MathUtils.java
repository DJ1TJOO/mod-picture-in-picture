/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

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
