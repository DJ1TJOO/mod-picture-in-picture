/**
 * Picture in Picture © 2023 by Thomas (DJ1TJOO) is licensed under CC BY-NC 4.0. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc/4.0/
 */

package nl.thomasbrants.pictureinpicture.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TexturedToggleButtonWidget
    extends Button {
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset, pressedUOffset;
    private final int textureWidth;
    private final int textureHeight;

    private boolean toggled;

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      ResourceLocation texture, boolean toggled,
                                      PressAction pressAction) {
        this(x, y, width, height, u, v, pressedUOffset, height, texture, 256, 256, toggled,
            pressAction);
    }

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      int hoveredVOffset, ResourceLocation texture, boolean toggled,
                                      PressAction pressAction) {
        this(x, y, width, height, u, v, pressedUOffset, hoveredVOffset, texture, 256, 256, toggled,
            pressAction);
    }

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      int hoveredVOffset, ResourceLocation texture, int textureWidth,
                                      int textureHeight, boolean toggled,
                                      PressAction pressAction) {
        this(x, y, width, height, u, v, pressedUOffset, hoveredVOffset, texture, textureWidth,
            textureHeight, toggled,
            pressAction, Component.empty());
    }

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      int hoveredVOffset, ResourceLocation texture, int textureWidth,
                                      int textureHeight, boolean toggled,
                                      PressAction pressAction,
                                      Component message) {
        super(x, y, width, height, message, (Button widget) -> {
            TexturedToggleButtonWidget button = (TexturedToggleButtonWidget) widget;
            button.setToggled(!button.isToggled());
            pressAction.onPress(button);
        }, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.pressedUOffset = pressedUOffset;
        this.texture = texture;
        this.toggled = toggled;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
//        TODO: check how renderTexture is different

        int vOffset = this.v;
        if (this.isHovered()) {
            vOffset += this.hoveredVOffset;
        }

        int uOffset = this.u;
        if (this.toggled) {
            uOffset += this.pressedUOffset;
        }

        renderTexture(graphics, texture, this.getX(),
            this.getY(), uOffset, vOffset, 0, this.width, this.height, this.textureWidth,
            this.textureHeight);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public interface PressAction {
        void onPress(TexturedToggleButtonWidget button);
    }
}