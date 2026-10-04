package dev.tulip.client.module;

import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;

public class ArrayListModule extends Module {
    private static final List<String> POSITIONS = List.of("Top Left", "Top Right");
    private String position = "Top Right";
    private double fontSize = 14.0;
    private double backgroundAlpha = 150.0;
    private boolean rounded = true;
    private double barWidth = 2.0;
    private boolean textShadow = true;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.choice("Position", () -> position, POSITIONS, value -> position = value),
            ModuleSetting.slider("Font Size", () -> String.format(java.util.Locale.ROOT, "%.0f", fontSize),
                    () -> (fontSize - 8.0) / 16.0, value -> fontSize = 8.0 + value * 16.0),
            ModuleSetting.slider("BG Alpha", () -> String.format(java.util.Locale.ROOT, "%.0f", backgroundAlpha),
                    () -> backgroundAlpha / 255.0, value -> backgroundAlpha = value * 255.0),
            ModuleSetting.toggle("Rounded", () -> rounded, () -> rounded = !rounded),
            ModuleSetting.slider("Bar Width", () -> String.format(java.util.Locale.ROOT, "%.1f", barWidth),
                    () -> (barWidth - 1.0) / 4.0, value -> barWidth = 1.0 + value * 4.0),
            ModuleSetting.toggle("Text Shadow", () -> textShadow, () -> textShadow = !textShadow)
    );

    public ArrayListModule() {
        super("ArrayList", ModuleCategory.HUD);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null || client.world == null
                || client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)) return;

        float scale = (float) fontSize / 9.0F;
        int screenWidth = client.getWindow().getScaledWidth();
        List<Module> enabled = Module.getModules().stream()
                .filter(module -> module != this && module.isEnabled())
                .sorted(Comparator.comparingInt((Module module) -> client.textRenderer.getWidth(module.getName())).reversed())
                .toList();
        int y = Module.getModule(WatermarkModule.class).map(module -> module.reservedHeight(client) + 4).orElse(5);
        for (Module module : enabled) {
            String label = module.getName();
            int textWidth = Math.round(client.textRenderer.getWidth(label) * scale);
            int rowHeight = Math.max(10, Math.round(9.0F * scale));
            int x = position.equals("Top Right") ? screenWidth - textWidth - 12 : 5;
            int boxX = position.equals("Top Right") ? x - 5 - (int) barWidth : x;
            int boxWidth = textWidth + 10 + (int) barWidth;
            int alpha = (int) backgroundAlpha;
            int background = alpha << 24 | 0x00141419;
            drawBackground(context, boxX, y, boxWidth, rowHeight + 2, background);
            int accentX = position.equals("Top Right") ? x - 2 - (int) barWidth : x;
            context.fill(accentX, y, accentX + (int) barWidth, y + rowHeight + 2, 0xFF82A4FF);
            drawLabel(context, label, x + (position.equals("Top Right") ? 0 : 6 + (int) barWidth), y + 2, scale);
            y += rowHeight + 3;
        }
    }

    private void drawLabel(DrawContext context, String label, int x, int y, float scale) {
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        if (textShadow) context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, label, 0, 0, 0xFFFFFFFF);
        else context.drawText(MinecraftClient.getInstance().textRenderer, label, 0, 0, 0xFFFFFFFF, false);
        matrices.popMatrix();
    }

    private void drawBackground(DrawContext context, int x, int y, int width, int height, int color) {
        if (!rounded) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }
        context.fill(x + 1, y, x + width - 1, y + height, color);
        context.fill(x, y + 1, x + width, y + height - 1, color);
    }
}