package nl.thomasbrants.pictureinpicture.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class TexturedToggleButtonWidget
    extends ButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset, pressedUOffset;
    private final int textureWidth;
    private final int textureHeight;

    private boolean toggled;

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      Identifier texture, boolean toggled,
                                      PressAction pressAction) {
        this(x, y, width, height, u, v, pressedUOffset, height, texture, 256, 256, toggled,
            pressAction);
    }

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      int hoveredVOffset, Identifier texture, boolean toggled,
                                      PressAction pressAction) {
        this(x, y, width, height, u, v, pressedUOffset, hoveredVOffset, texture, 256, 256, toggled,
            pressAction);
    }

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      int hoveredVOffset, Identifier texture, int textureWidth,
                                      int textureHeight, boolean toggled,
                                      PressAction pressAction) {
        this(x, y, width, height, u, v, pressedUOffset, hoveredVOffset, texture, textureWidth,
            textureHeight, toggled,
            pressAction, ScreenTexts.EMPTY);
    }

    public TexturedToggleButtonWidget(int x, int y, int width, int height, int u, int v,
                                      int pressedUOffset,
                                      int hoveredVOffset, Identifier texture, int textureWidth,
                                      int textureHeight, boolean toggled,
                                      PressAction pressAction,
                                      Text message) {
        super(x, y, width, height, message, (ButtonWidget widget) -> {
            TexturedToggleButtonWidget button = (TexturedToggleButtonWidget) widget;
            button.setToggled(!button.isToggled());
            pressAction.onPress(button);
        }, DEFAULT_NARRATION_SUPPLIER);
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
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, this.texture);
        int vOffset = this.v;
        if (!this.isNarratable()) {
            vOffset += this.hoveredVOffset * 2;
        } else if (this.isHovered()) {
            vOffset += this.hoveredVOffset;
        }

        int uOffset = this.u;
        if (this.toggled) {
            uOffset += this.pressedUOffset;
        }

        RenderSystem.enableDepthTest();
        net.minecraft.client.gui.widget.TexturedButtonWidget.drawTexture(matrices, this.getX(),
            this.getY(), uOffset, vOffset, this.width, this.height, this.textureWidth,
            this.textureHeight);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Environment(value = EnvType.CLIENT)
    public interface PressAction {
        void onPress(TexturedToggleButtonWidget button);
    }
}