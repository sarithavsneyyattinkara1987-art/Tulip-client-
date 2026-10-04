package dev.tulip.client.module;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import dev.tulip.client.TulipClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;

public class WatermarkModule extends Module {
    private static final List<String> POSITIONS = List.of("Top Center", "Top Left", "Top Right");
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String position = "Top Center";
    private double transparency = 200.0;
    private boolean showTitle = true;
    private boolean showUsername = true;
    private boolean showFps = true;
    private boolean showTime = true;
    private boolean showCoords = true;
    private boolean showPing = true;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.choice("Position", () -> position, POSITIONS, value -> position = value),
            ModuleSetting.slider("Transparency", () -> String.format(java.util.Locale.ROOT, "%.0f", transparency),
                    () -> transparency / 255.0, value -> transparency = value * 255.0),
            ModuleSetting.toggle("Show Title", () -> showTitle, () -> showTitle = !showTitle),
            ModuleSetting.toggle("Show Username", () -> showUsername, () -> showUsername = !showUsername),
            ModuleSetting.toggle("Show FPS", () -> showFps, () -> showFps = !showFps),
            ModuleSetting.toggle("Show Time", () -> showTime, () -> showTime = !showTime),
            ModuleSetting.toggle("Show Coords", () -> showCoords, () -> showCoords = !showCoords),
            ModuleSetting.toggle("Show Ping", () -> showPing, () -> showPing = !showPing)
    );

    public WatermarkModule() {
        super("Watermark", ModuleCategory.HUD);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null || client.world == null
                || client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)) return;

        List<String> lines = wrapSegments(client, segments(client), Math.max(1, client.getWindow().getScaledWidth() - 28));
        if (lines.isEmpty()) return;
        int screenWidth = client.getWindow().getScaledWidth();
        int textWidth = lines.stream().mapToInt(client.textRenderer::getWidth).max().orElse(0);
        int boxWidth = Math.min(screenWidth - 8, textWidth + 16);
        int boxHeight = lines.size() * 19;
        int x = switch (position) {
            case "Top Left" -> 4;
            case "Top Right" -> screenWidth - boxWidth - 4;
            default -> (screenWidth - boxWidth) / 2;
        };
        int alpha = (int) transparency;
        context.fill(x, 6, x + boxWidth, 6 + boxHeight, alpha << 24 | 0x00141419);
        context.fill(x, 6, x + boxWidth, 7, 0xFF82A4FF);
        for (int index = 0; index < lines.size(); index++) {
            context.drawTextWithShadow(client.textRenderer, lines.get(index), x + 8, 12 + index * 19, 0xFFFFFFFF);
        }
    }

    public int reservedHeight(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return 0;
        return wrapSegments(client, segments(client), Math.max(1, client.getWindow().getScaledWidth() - 28)).size() * 19 + 6;
    }

    private List<String> segments(MinecraftClient client) {
        List<String> segments = new ArrayList<>();
        if (showTitle) segments.add(TulipClient.MOD_NAME);
        if (showUsername) segments.add(client.player.getName().getString());
        if (showFps) segments.add(client.getCurrentFps() + " FPS");
        if (showTime) segments.add(LocalTime.now().format(CLOCK));
        if (showCoords) segments.add(String.format(java.util.Locale.ROOT, "%d %d %d",
                (int) client.player.getX(), (int) client.player.getY(), (int) client.player.getZ()));
        if (showPing && client.player.networkHandler != null) {
            var entry = client.player.networkHandler.getPlayerListEntry(client.player.getUuid());
            segments.add((entry == null ? 0 : entry.getLatency()) + " ms");
        }
        return segments;
    }

    private List<String> wrapSegments(MinecraftClient client, List<String> segments, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String current = "";
        for (String segment : segments) {
            String candidate = current.isEmpty() ? segment : current + "  |  " + segment;
            if (!current.isEmpty() && client.textRenderer.getWidth(candidate) > maxWidth) {
                lines.add(current);
                current = segment;
            } else {
                current = candidate;
            }
        }
        if (!current.isEmpty()) lines.add(current);
        return lines;
    }
}