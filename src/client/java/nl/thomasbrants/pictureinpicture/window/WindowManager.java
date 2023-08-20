/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.window;

import nl.thomasbrants.pictureinpicture.config.ModConfig;
import nl.thomasbrants.pictureinpicture.config.WindowEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.thomasbrants.pictureinpicture.PictureInPictureModClient.getConfig;
import static nl.thomasbrants.pictureinpicture.PictureInPictureModClient.saveConfig;

public class WindowManager {
    private static final List<Window> WINDOWS = new ArrayList<>();
    private static boolean READY_TO_CREATE_WINDOWS = false;
    private static boolean CREATED_WINDOWS = false;

    public static void createWindow(WindowEntry windowSettings) {
        Window window = Window.fromEntry(windowSettings);
        window.create();

        windowSettings.setHandle(window.getHandle());
        WINDOWS.add(window);
    }

    private static Optional<Window> getWindow(long handle) {
        return WINDOWS.stream().filter(x -> x.getHandle() == handle).findFirst();
    }

    public static void destroyWindows(List<Long> handles) {
        WINDOWS.stream().filter(window -> handles.contains(window.getHandle()))
            .forEach(Window::destroy);
    }

    public static void destroyWindow(Long handle) {
        getWindow(handle).ifPresent(Window::destroy);
    }

    public static void updateWindows(List<WindowEntry> oldWindows, List<WindowEntry> newWindows) {
        // Close removed windows
        oldWindows.stream()
            .map(WindowEntry::getHandle)
            .filter(handle -> newWindows.stream().noneMatch(y -> handle == y.getHandle()))
            .forEach(WindowManager::destroyWindow);

        // Update existing windows
        newWindows.stream()
            .filter(entry -> entry.getHandle() != 0)
            .filter(entry -> oldWindows.stream()
                .anyMatch(oldEntry -> oldEntry.getHandle() == entry.getHandle() &&
                    !oldEntry.isSame(entry)))
            .forEach(entry -> getWindow(entry.getHandle()).ifPresent(
                window -> window.updateFromEntry(entry)));

        // Open new windows
        newWindows.stream()
            .filter(oldEntry -> oldEntry.getHandle() == 0)
            .forEach(entry -> {
                createWindow(entry);
                getConfig().windows.get(newWindows.indexOf(entry))
                    .setHandle(entry.getHandle());
            });

        saveConfig();
    }

    public static void onResolutionChanged() {
        WINDOWS.forEach(Window::onResolutionChanged);
    }

    public static void renderWindows() {
        if (!CREATED_WINDOWS) {
            return;
        }

        WINDOWS.removeIf(window -> !window.isOpen());
        updateWindowsConfig();

        WINDOWS.forEach(Window::render);
    }

    public static List<Window> getWindows() {
        return WINDOWS;
    }

    public static boolean isReadyToCreateWindows() {
        return READY_TO_CREATE_WINDOWS;
    }

    public static void onReadyToCreateWindows() {
        READY_TO_CREATE_WINDOWS = true;
    }

    public static void onCreatedWindows() {
        READY_TO_CREATE_WINDOWS = false;
        CREATED_WINDOWS = true;
    }

    private static void updateWindowsConfig() {
        ModConfig config = getConfig();

        List<WindowEntry> newWindows = new ArrayList<>(config.windows);
        newWindows.removeIf(windowEntry -> WINDOWS.stream()
            .noneMatch(window -> window.getHandle() == windowEntry.getHandle()));
        config.windows = newWindows;

        saveConfig();
    }
}
