package de.flohkiste.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.flohkiste.StoneCrusher;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StoneCrusherScreen extends HandledScreen<StoneCrusherScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("stonecrusher", "textures/gui/stone_crusher.png");

    public StoneCrusherScreen(StoneCrusherScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderProgressArrow(context, x, y);
        renderProgressFuel(context, x, y);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderProgressArrow(DrawContext context, int x, int y){
        int width = Math.round(handler.getCrushProgress() * 22);
        context.drawTexture(TEXTURE, x + 80, y + 34, 176, 0, width, 15);
    }

    private void renderProgressFuel(DrawContext context, int x, int y) {
        int height = Math.round(handler.getFuelProgress() * 12);
        context.drawTexture(TEXTURE, x + 57, y + 37 + 12 - height, 176, 15 + 12 - height, 13, height);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawSlot(DrawContext context, Slot slot) {
        super.drawSlot(context, slot);
    }
}
