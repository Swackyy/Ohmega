package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class RuRuProvider extends OhmegaLangProvider {
    public RuRuProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "ru_ru", lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);

        // Datapack
        internalHelper.addDataPackDescription("Ресурсы мода Ohmega");

        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Кольцо Ангела",
                "Позволяет носителю летать",
                "Нажмите %s для переключения полета");

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Тип Аксессуара: %s");
        //helper.addType(KEY_ACCESSORY_TYPE_NONE, "");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Общий");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Обычный");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Инструмент");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Особый");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%s %s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Открыть/Закрыть Панель Аксессуаров");

        // Config
        internalHelper.addConfigTitle("Конфигурация Ohmega");

        // Client config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Ohmega Клиент", "Конфигурация Ohmega Клиент");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Режим Совместимости",
                "Отключает некоторые полезные, но малозаметные функции, которые могут улучшить совместимость с другими модами в редких случаях");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BUTTON_STYLE_KEY,
                "Стиль Кнопки",
                """
                        Стиль кнопки, открывающей панель аксессуаров
                        DEFAULT: Обычная кнопка Ohmega
                        LEGACY: Кнопка, вдохновленная Curios/Baubles, которая отображается рядом с моделью инвентаря
                        TAG: Маленькая кнопка-ярлык, выступающая за верхний угол инвентаря
                        HIDDEN: Не отображать. Вместо кнопки использовать горячую клавишу для открытия панели аксессуаров""");
        /*internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Сторона Панели",
                "Сторона, с которой панель аксессуаров будет отображаться");*/
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Отображать Подсказку При Наведении",
                "Если включено, при наведении на аксессуар будет отображаться подсказка с его типом");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Максимум Столбцов",
                "Максимальное количество отображаемых столбцов");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Максимум Ячеек в Столбце",
                """
                        Максимальное количество ячеек в одном столбце
                        Если превышено, будет создана новая колонка, если не превышено максимальное количество столбцов""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Максимум Видимых Ячеек в Столбце",
                "Максимальное количество отображаемых ячеек в одном столбце");

        // Server config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Ohmega Сервер", "Конфигурация Ohmega Сервер");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SLOT_TYPES_KEY,
                "Типы Ячеек",
                "Определяет типы и количество ячеек для аксессуаров");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.SLOT_TYPES_KEY, "Изменить");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Типы Слотов с Горячей Клавишей",
                "Определяет типы аксессуаров, которые могут быть использованы с помощью горячей клавиши");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Изменить");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Поведение Аксессуаров При Смерти",
                """
                        Определяет поведение аксессуаров в случае смерти игрока
                        DEFAULT: Использовать стандартное игровое правило "keepInventory"
                        ALWAYS_ON: Никогда не выпадать при смерти
                        ALWAYS_OFF: Всегда выпадать при смерти""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Не Использовать Типы Аксессуаров",
                "Если включено, типы аксессуаров не будут использоваться, и все они будут перезаписаны с заменой на \"ohmega:generic\"");

        // ConfigurationScreen Forge port UI translations
        // Titles
        internalHelper.addConfigPort("title", "Настройки %s");
        internalHelper.addConfigPortTitle("client", "Клиентские настройки %s");
        internalHelper.addConfigPortTitle("server", "Серверные настройки %s");
        internalHelper.addConfigPortTitle("common", "Общие настройки %s");
        // Types
        internalHelper.addConfigPortType("client", "Настройки клиента");
        internalHelper.addConfigPortType("server", "Настройки сервера");
        internalHelper.addConfigPortType("common", "Общие настройки");
        // Misc
        internalHelper.addConfigPort("notonline", "Настройки здесь определяются сервером и не могут быть изменены в режиме онлайн.");
        internalHelper.addConfigPort("notlan", "Настройки в этом разделе нельзя редактировать, пока игра открыта для LAN. Пожалуйста, вернитесь в главное меню и загрузите мир заново.");
        internalHelper.addConfigPort("notloaded", "Эти настройки становятся доступны только после генерации мира.");
        internalHelper.addConfigPort("unsupportedelement", "Это значение нельзя редактировать в пользовательском интерфейсе. Пожалуйста, свяжитесь с автором мода для создания своего пользовательского интерфейса.");
        internalHelper.addConfigPort("longstring", "Это значение слишком длинное, чтобы его можно было редактировать в пользовательском интерфейсе. Пожалуйста, отредактируйте его в файле конфигурации.");
        internalHelper.addConfigPort("section", "%s...");
        internalHelper.addConfigPort("sectiontext", "Редактировать");
        internalHelper.addConfigPort("breadcrumb.order", "%1$s %2$s %3$s");
        internalHelper.addConfigPort("breadcrumb.separator", ">");
        internalHelper.addConfigPort("listelement", "%s:");
        internalHelper.addConfigPort("undo", "Отменить");
        internalHelper.addConfigPort("undo.tooltip", "Возвращает изменения только на этом экране.");
        internalHelper.addConfigPort("reset", "Сброс");
        internalHelper.addConfigPort("reset.tooltip", "Возвращает всё на этом экране к значениям по умолчанию.");
        internalHelper.addConfigPort("newlistelement", "+");
        internalHelper.addConfigPort("listelementup", "\u23f6");
        internalHelper.addConfigPort("listelementdown", "\u23f7");
        internalHelper.addConfigPort("listelementremove", "\u274c");
        internalHelper.addConfigPort("rangetooltip", "Диапазон: %s");
        internalHelper.addConfigPort("filenametooltip", "Файл: «%s»");
        internalHelper.addConfigPort("common", "Общие настройки");
        internalHelper.addConfigPort("client", "Настройки клиента");
        internalHelper.addConfigPort("server", "Настройки сервера");
        internalHelper.addConfigPort("restart.game.title", "Требуется перезапуск Minecraft");
        internalHelper.addConfigPort("restart.game.text", "Один или несколько измененных параметров конфигурации вступят в силу только после перезапуска игры.");
        internalHelper.addConfigPort("restart.server.title", "Мир должен быть перезагружен");
        internalHelper.addConfigPort("restart.server.text", "Один или несколько измененных параметров конфигурации вступят в силу только после перезагрузки мира.");
        internalHelper.addConfigPort("restart.return", "Потом перезапущу");
        internalHelper.addConfigPort("restart.return.tooltip", "Ваши изменения не вступят в силу до перезапуска!");
    }
}
