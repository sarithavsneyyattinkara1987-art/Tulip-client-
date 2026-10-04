package dev.tulip.client.gui;

import dev.tulip.client.config.ClientConfig;
import dev.tulip.client.module.Module;
import dev.tulip.client.module.ModuleCategory;
import dev.tulip.client.module.ModuleSetting;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class AdinStyleScreen extends Screen {
    private static final int SIDEBAR_WIDTH = 148;
    private static final int HEADER_HEIGHT = 62;
    private static final int MODULE_ROW_HEIGHT = 42;
    private static final int SETTING_ROW_HEIGHT = 44;

    private ModuleCategory selectedCategory = ModuleCategory.COMBAT;
    private Module selectedModule;
    private float moduleScroll;
    private float shownModuleScroll;
    private float settingScroll;
    private float shownSettingScroll;
    private float categoryScroll;
    private float shownCategoryScroll;
    private boolean compactSettingsView;
    private ModuleSetting awaitingKeybind;

    public AdinStyleScreen() {
        super(Text.literal("Adin"));
        selectFirstModule();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        shownModuleScroll += (moduleScroll - shownModuleScroll) * 0.32f;
        shownSettingScroll += (settingScroll - shownSettingScroll) * 0.32f;
        shownCategoryScroll += (categoryScroll - shownCategoryScroll) * 0.32f;

        int panelWidth = panelWidth();
        int panelHeight = panelHeight();
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        int contentTop = panelY + HEADER_HEIGHT;
        int contentBottom = panelY + panelHeight - 16;
        int sidebarWidth = sidebarWidth(panelWidth);
        int mainX = panelX + sidebarWidth + 12;
        int mainWidth = Math.max(1, panelX + panelWidth - 12 - mainX);
        boolean compact = isCompact(panelWidth);
        int moduleWidth = compact ? mainWidth : Math.min(270, Math.max(150, mainWidth * 2 / 5));
        int settingsX = compact ? mainX : mainX + moduleWidth + 12;
        int settingsWidth = compact ? mainWidth : Math.max(1, panelX + panelWidth - 12 - settingsX);

        context.fill(0, 0, width, height, 0xA8080B12);
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF101725);
        context.fill(panelX, panelY, panelX + sidebarWidth, panelY + panelHeight, 0xFF121A2E);
        context.fill(panelX + sidebarWidth, panelY, panelX + sidebarWidth + 1, panelY + panelHeight, 0xFF263047);
        context.drawTextWithShadow(textRenderer, "ADIN", panelX + 18, panelY + 18, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "CLIENT", panelX + 68, panelY + 18, 0xFF8CA6E8);
        context.drawTextWithShadow(textRenderer, "Right Shift to close", panelX + panelWidth - 132, panelY + 18, 0xFF7F899D);

        renderCategories(context, panelX, panelY, panelHeight, sidebarWidth);
        context.drawTextWithShadow(textRenderer, categoryLabel(selectedCategory), mainX, panelY + 39, 0xFFFFFFFF);
        if (compact && compactSettingsView) {
            context.drawTextWithShadow(textRenderer, "< MODULES", mainX, contentTop - 14, 0xFF8492AA);
            renderSettings(context, settingsX, contentTop, settingsWidth, contentBottom);
        } else {
            context.drawTextWithShadow(textRenderer, "MODULES", mainX, contentTop - 14, 0xFF8492AA);
            if (!compact) context.drawTextWithShadow(textRenderer, "SETTINGS", settingsX, contentTop - 14, 0xFF8492AA);
            renderModules(context, mainX, contentTop, moduleWidth, contentBottom);
            if (!compact) renderSettings(context, settingsX, contentTop, settingsWidth, contentBottom);
        }
    }

    private void renderCategories(DrawContext context, int panelX, int panelY, int panelHeight, int sidebarWidth) {
        int y = panelY + HEADER_HEIGHT + 8;
        int bottom = panelY + panelHeight - 8;
        int maxScroll = Math.max(0, ModuleCategory.values().length * 34 - (bottom - y));
        categoryScroll = Math.min(categoryScroll, maxScroll);
        shownCategoryScroll = Math.min(shownCategoryScroll, maxScroll);
        context.enableScissor(panelX, y, panelX + sidebarWidth, bottom);
        int index = 0;
        for (ModuleCategory category : ModuleCategory.values()) {
            boolean selected = category == selectedCategory;
            int color = selected ? 0xFF26365B : 0xFF121A2E;
            int rowY = y + index * 34 - Math.round(shownCategoryScroll);
            context.fill(panelX + 8, rowY, panelX + sidebarWidth - 8, rowY + 28, color);
            if (selected) context.fill(panelX + 8, rowY, panelX + 11, rowY + 28, 0xFF82A4FF);
            context.drawTextWithShadow(textRenderer, categoryLabel(category), panelX + 16, rowY + 10,
                    selected ? 0xFFFFFFFF : 0xFFABB5C7);
            index++;
        }
        context.disableScissor();
    }

    private void renderModules(DrawContext context, int x, int top, int width, int bottom) {
        List<Module> modules = modulesInCategory();
        int visibleHeight = bottom - top;
        int maxScroll = Math.max(0, modules.size() * MODULE_ROW_HEIGHT - visibleHeight);
        moduleScroll = Math.min(moduleScroll, maxScroll);
        shownModuleScroll = Math.min(shownModuleScroll, maxScroll);

        context.enableScissor(x, top, x + width, bottom);
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int y = top + i * MODULE_ROW_HEIGHT - Math.round(shownModuleScroll);
            if (y + MODULE_ROW_HEIGHT <= top || y >= bottom) continue;
            boolean selected = module == selectedModule;
            context.fill(x, y + 2, x + width, y + MODULE_ROW_HEIGHT - 4, selected ? 0xFF202D48 : 0xFF171F30);
            context.drawTextWithShadow(textRenderer, module.getName(), x + 12, y + 10, 0xFFFFFFFF);
            int toggleColor = module.isEnabled() ? 0xFF59C491 : 0xFF8790A0;
            context.fill(x + width - 48, y + 12, x + width - 14, y + 29, toggleColor);
            context.drawCenteredTextWithShadow(textRenderer, module.isEnabled() ? "ON" : "OFF",
                    x + width - 31, y + 17, 0xFF101725);
        }
            context.disableScissor();
    }

    private void renderSettings(DrawContext context, int x, int top, int width, int bottom) {
        if (selectedModule == null || width <= 0) {
            context.drawTextWithShadow(textRenderer, "Select a module to view settings", x, top + 12, 0xFF8994A8);
            return;
        }

        context.fill(x, top, x + width, bottom, 0xFF151D2D);
        context.drawTextWithShadow(textRenderer, selectedModule.getName(), x + 12, top + 12, 0xFFFFFFFF);
        List<ModuleSetting> settings = selectedModule.getSettings().stream().filter(ModuleSetting::isVisible).toList();
        int rowsTop = top + 38;
        int rowsHeight = bottom - rowsTop;
        int maxScroll = Math.max(0, settings.size() * SETTING_ROW_HEIGHT - rowsHeight);
        settingScroll = Math.min(settingScroll, maxScroll);
        shownSettingScroll = Math.min(shownSettingScroll, maxScroll);

        context.enableScissor(x, rowsTop, x + width, bottom);
        for (int i = 0; i < settings.size(); i++) {
            ModuleSetting setting = settings.get(i);
            int y = rowsTop + i * SETTING_ROW_HEIGHT - Math.round(shownSettingScroll);
            if (y + SETTING_ROW_HEIGHT <= rowsTop || y >= bottom) continue;
            context.drawTextWithShadow(textRenderer, setting.getName(), x + 12, y + 6, 0xFFE1E6EF);
            if (setting.isToggle()) {
                boolean enabled = setting.getValue().equals("ON");
                int track = enabled ? 0xFF59C491 : 0xFF4B5568;
                context.fill(x + width - 54, y + 4, x + width - 14, y + 20, track);
                context.drawCenteredTextWithShadow(textRenderer, setting.getValue(), x + width - 34, y + 8, 0xFF101725);
            } else if (setting.isChoice()) {
                context.drawTextWithShadow(textRenderer, "< " + setting.getValue() + " >", x + 12, y + 24, 0xFF9FB5E8);
            } else if (setting.isKeybind()) {
                context.drawTextWithShadow(textRenderer,
                        setting == awaitingKeybind ? "Press a key" : setting.getValue(), x + 12, y + 24, 0xFF9FB5E8);
            } else {
                context.drawTextWithShadow(textRenderer, setting.getValue(), x + width - 100, y + 6, 0xFF9FB5E8);
                int trackX = x + 12;
                int trackWidth = width - 24;
                int barY = y + 29;
                context.fill(trackX, barY, trackX + trackWidth, barY + 3, 0xFF3A455A);
                int knobX = trackX + (int) (trackWidth * setting.getNormalizedValue());
                context.fill(trackX, barY, knobX, barY + 3, 0xFF82A4FF);
                context.fill(knobX - 3, barY - 3, knobX + 4, barY + 6, 0xFFDCE6FF);
            }
        }
        context.disableScissor();
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        if (awaitingKeybind != null) {
            if (click.button() <= 7) {
                awaitingKeybind.setKeyCode(-click.button() - 1);
                ClientConfig.save();
            }
            awaitingKeybind = null;
            return true;
        }
        if (click.button() != 0) return super.mouseClicked(click, doubleClick);
        int panelWidth = panelWidth();
        int panelHeight = panelHeight();
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        int contentTop = panelY + HEADER_HEIGHT;
        int contentBottom = panelY + panelHeight - 16;
        int sidebarWidth = sidebarWidth(panelWidth);
        int mainX = panelX + sidebarWidth + 12;
        int mainWidth = Math.max(1, panelX + panelWidth - 12 - mainX);
        boolean compact = isCompact(panelWidth);
        int moduleWidth = compact ? mainWidth : Math.min(270, Math.max(150, mainWidth * 2 / 5));
        int settingsX = compact ? mainX : mainX + moduleWidth + 12;
        int settingsWidth = compact ? mainWidth : Math.max(1, panelX + panelWidth - 12 - settingsX);
        double mouseX = click.x();
        double mouseY = click.y();

        int categoryY = panelY + HEADER_HEIGHT + 8;
        int categoryBottom = panelY + panelHeight - 8;
        for (int index = 0; index < ModuleCategory.values().length; index++) {
            int rowY = categoryY + index * 34 - Math.round(shownCategoryScroll);
            if (mouseX >= panelX + 8 && mouseX <= panelX + sidebarWidth - 8
                    && mouseY >= categoryY && mouseY <= categoryBottom
                    && mouseY >= rowY && mouseY <= rowY + 28) {
                ModuleCategory category = ModuleCategory.values()[index];
                selectedCategory = category;
                moduleScroll = shownModuleScroll = 0;
                settingScroll = shownSettingScroll = 0;
                compactSettingsView = false;
                selectFirstModule();
                return true;
            }
        }

        if (compact && compactSettingsView && mouseX >= mainX && mouseX <= mainX + mainWidth
                && mouseY >= contentTop - 18 && mouseY < contentTop) {
            compactSettingsView = false;
            return true;
        }

        if ((!compact || !compactSettingsView) && mouseX >= mainX && mouseX <= mainX + moduleWidth
                && mouseY >= contentTop && mouseY <= contentBottom) {
            List<Module> modules = modulesInCategory();
            int row = (int) ((mouseY - contentTop + shownModuleScroll) / MODULE_ROW_HEIGHT);
            if (row >= 0 && row < modules.size()) {
                Module module = modules.get(row);
                if (mouseX >= mainX + moduleWidth - 56) {
                    module.toggle();
                    ClientConfig.save();
                } else {
                    selectedModule = module;
                    settingScroll = shownSettingScroll = 0;
                    if (compact) compactSettingsView = true;
                }
                return true;
            }
        }

        if (selectedModule != null && (!compact || compactSettingsView)
                && mouseX >= settingsX && mouseX <= settingsX + settingsWidth
                && mouseY >= contentTop + 38 && mouseY <= contentBottom) {
            List<ModuleSetting> settings = selectedModule.getSettings().stream().filter(ModuleSetting::isVisible).toList();
            int row = (int) ((mouseY - (contentTop + 38) + shownSettingScroll) / SETTING_ROW_HEIGHT);
            if (row >= 0 && row < settings.size()) {
                ModuleSetting setting = settings.get(row);
                if (setting.isKeybind()) awaitingKeybind = setting;
                else if (setting.isToggle()) setting.toggle();
                else if (setting.isChoice()) setting.cycleChoice();
                else {
                    int trackX = settingsX + 12;
                    int trackWidth = settingsWidth - 24;
                    setting.setNormalizedValue((mouseX - trackX) / trackWidth);
                }
                ClientConfig.save();
                return true;
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int panelWidth = panelWidth();
        int panelHeight = panelHeight();
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        int sidebarWidth = sidebarWidth(panelWidth);
        int mainX = panelX + sidebarWidth + 12;
        int mainWidth = Math.max(1, panelX + panelWidth - 12 - mainX);
        boolean compact = isCompact(panelWidth);
        int moduleWidth = compact ? mainWidth : Math.min(270, Math.max(150, mainWidth * 2 / 5));
        int settingsX = compact ? mainX : mainX + moduleWidth + 12;
        int settingsWidth = compact ? mainWidth : Math.max(1, panelX + panelWidth - 12 - settingsX);
        int contentTop = panelY + HEADER_HEIGHT;
        int contentBottom = panelY + panelHeight - 16;

        int categoriesTop = panelY + HEADER_HEIGHT + 8;
        int categoriesBottom = panelY + panelHeight - 8;
        if (mouseX >= panelX && mouseX <= panelX + sidebarWidth && mouseY >= categoriesTop && mouseY <= categoriesBottom) {
            int maxScroll = Math.max(0, ModuleCategory.values().length * 34 - (categoriesBottom - categoriesTop));
            categoryScroll = Math.clamp(categoryScroll - (float) verticalAmount * 34, 0, maxScroll);
            return true;
        }
        if ((!compact || compactSettingsView) && mouseX >= settingsX && mouseX <= settingsX + settingsWidth
                && mouseY >= contentTop && mouseY <= contentBottom) {
            int rows = selectedModule == null ? 0 : (int) selectedModule.getSettings().stream().filter(ModuleSetting::isVisible).count();
            int maxScroll = Math.max(0, rows * SETTING_ROW_HEIGHT - (contentBottom - contentTop - 38));
            settingScroll = Math.clamp(settingScroll - (float) verticalAmount * SETTING_ROW_HEIGHT, 0, maxScroll);
            return true;
        }
        if ((!compact || !compactSettingsView) && mouseX >= mainX && mouseX <= mainX + moduleWidth
            && mouseY >= contentTop && mouseY <= contentBottom) {
            int maxScroll = Math.max(0, modulesInCategory().size() * MODULE_ROW_HEIGHT - (contentBottom - contentTop));
            moduleScroll = Math.clamp(moduleScroll - (float) verticalAmount * MODULE_ROW_HEIGHT, 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (awaitingKeybind != null) {
            if (input.key() != GLFW.GLFW_KEY_ESCAPE) {
                awaitingKeybind.setKeyCode(input.key());
                ClientConfig.save();
            }
            awaitingKeybind = null;
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_ESCAPE || input.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private List<Module> modulesInCategory() {
        return Module.getModules().stream().filter(module -> module.getCategory() == selectedCategory).toList();
    }

    private void selectFirstModule() {
        selectedModule = modulesInCategory().stream().findFirst().orElse(null);
    }

    private static String categoryLabel(ModuleCategory category) {
        String lower = category.name().toLowerCase(java.util.Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private int panelWidth() {
        return Math.max(180, Math.min(780, width - 16));
    }

    private int panelHeight() {
        return Math.max(180, Math.min(460, height - 16));
    }

    private int sidebarWidth(int panelWidth) {
        return Math.min(SIDEBAR_WIDTH, Math.max(82, panelWidth / 3));
    }

    private boolean isCompact(int panelWidth) {
        return panelWidth < 560;
    }
}
