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

public final class OhmegaUkUaProvider extends OhmegaLangProvider {
    public OhmegaUkUaProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "uk_ua", lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);

        // Datapack
        internalHelper.addDataPackDescription("Ресурси мода для Ohmega");

        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Ангельське кільце",
                "Надає носію політ",
                "Натисніть %s, щоб літати");

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Тип аксесуару: %s");
        //helper.addType(KEY_ACCESSORY_TYPE_NONE, "");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Загальний");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Звичайний");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Приладдя");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Спеціальний");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%1$s %2$s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Відкрити/закрити інвентар аксесуарів");

        // Config
        internalHelper.addConfigTitle("Налаштування Ohmega");

        // Client config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Клієнт Ohmega", "Клієнтські налаштування Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Режим сумісности",
                "Вимикає деякі корисні, але здебільшого непомітні функції, які можуть покращити сумісність модів у рідкісних випадках");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Кнопка стилю",
                """
                        Стиль кнопки інвентарю аксесуарів.
                        DEFAULT: звичайний стиль кнопки Ohmega.
                        LEGACY: кнопка, натхненна Curios/Baubles, яку буде видно поруч із моделлю гравця в інвентарі.
                        TAG: маленька кнопка, схожа на теґ, що з’являється біля верхнього кута інвентарю.
                        HIDDEN: не буде видно, скористайтеся спеціальним призначенням клавіш, щоб відкрити інвентар аксесуарів""");
        /*internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Сторона інвентарю",
                "Напрямок, у якому будуть заповнюватися слоти для аксесуарів");*/
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Показати спливну підказку наведення",
                "Якщо ввімкнено, буде видно спливну підказку типу слота для аксесуарів, коли на нього наведено курсор");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Макс. стовпців",
                "Максимальна кількість стовпців для промальовування");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Макс. слотів для стовпців",
                """
                        Максимальна кількість слотів на стовпець.
                        Якщо перевищено, буде створено новий стовпець, якщо він не перевищує «maxColumns»""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Макс. слотів для промальовування стовпців",
                "Максимальна кількість слотів для промальовування в стовпцю");

        // Server config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Сервер Ohmega", "Серверні налаштування Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Типи слотів",
                "Керує типами та кількістю слотів в інвентарі аксесуарів");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Ключові типи слотів",
                "Керує типами аксесуарів, які можна прив'язати");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Зберігати поведінку аксесуарів",
                """
                        Керує тим, як упоратися зі смертю гравця з точки зору викидання аксесуарів.
                        DEFAULT: Використовує стандартне ігрове правило «keepInventory».
                        ALWAYS_ON: ніколи не викидає аксесуари після смерти.
                        ALWAYS_OFF: завжди викидає аксесуари після смерти""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Вимкнути типи аксесуарів",
                "Якщо ввімкнено, фактично жодні типи аксесуарів не використовуватимуться, і всі вони будуть перевизначені, змінюючи їх усі на «ohmega:generic»");

        // ConfigurationScreen Forge port UI translations
        // Titles
        internalHelper.addConfigPort("title", "Налаштування %s");
        internalHelper.addConfigPortTitle("client", "Налаштування клієнта %s");
        internalHelper.addConfigPortTitle("server", "Налаштування сервера %s");
        internalHelper.addConfigPortTitle("common", "Загальні налаштування %s");
        // Types
        internalHelper.addConfigPortType("client", "Налаштування клієнта");
        internalHelper.addConfigPortType("server", "Налаштування сервера");
        internalHelper.addConfigPortType("common", "Загальні налаштування");
        // Misc
        internalHelper.addConfigPort("notonline", "Налаштування тут визначаються сервером і не можуть бути змінені доки ви в мережі.");
        internalHelper.addConfigPort("notlan", "Налаштування тут не можуть бути змінені, поки ваша гра відкрита для локальної мережі. Будь ласка, поверніться до головного меню і зайдіть у світ ще раз.");
        internalHelper.addConfigPort("notloaded", "Налаштування тут доступні після завантаження світу.");
        internalHelper.addConfigPort("unsupportedelement", "Це значення не можна змінити в інтерфейсі. Будь ласка, зв’яжіться з автором мода про надання користувацького інтерфейсу для нього.");
        internalHelper.addConfigPort("longstring", "Це значення задовге для зміни в інтерфейсі. Будь ласка, змініть його у файлі налаштувань.");
        internalHelper.addConfigPort("section", "%s…");
        internalHelper.addConfigPort("sectiontext", "Змінити");
        internalHelper.addConfigPort("breadcrumb.order", "%1$s %2$s %3$s");
        internalHelper.addConfigPort("breadcrumb.separator", ">");
        internalHelper.addConfigPort("listelement", "%s:");
        internalHelper.addConfigPort("undo", "Скасувати");
        internalHelper.addConfigPort("undo.tooltip", "Повертає зміни тільки на цьому екрані.");
        internalHelper.addConfigPort("reset", "Скинути");
        internalHelper.addConfigPort("reset.tooltip", "Повертає все на цьому екрані до усталених значень.");
        internalHelper.addConfigPort("newlistelement", "+");
        internalHelper.addConfigPort("listelementup", "\u23f6");
        internalHelper.addConfigPort("listelementdown", "\u23f7");
        internalHelper.addConfigPort("listelementremove", "\u274c");
        internalHelper.addConfigPort("rangetooltip", "Діапазон: %s");
        internalHelper.addConfigPort("filenametooltip", "Файл: \"%s\"");
        internalHelper.addConfigPort("common", "Загальні параметри");
        internalHelper.addConfigPort("client", "Параметри клієнта");
        internalHelper.addConfigPort("server", "Параметри сервера");
        internalHelper.addConfigPort("restart.game.title", "Minecraft потрібно перезавантажити");
        internalHelper.addConfigPort("restart.game.text", "Один чи кілька параметри налаштувань, які були змінені, набудуть чинности лише після запуску гри.");
        internalHelper.addConfigPort("restart.server.title", "У світ потрібно повторно зайти");
        internalHelper.addConfigPort("restart.server.text", "Один чи кілька параметрів налаштувань, які були змінені, набудуть чинности лише після перезавантаження світу.");
        internalHelper.addConfigPort("restart.return", "Ігнорувати");
        internalHelper.addConfigPort("restart.return.tooltip", "Зміни не матимуть ніякого ефекту, поки ви не перезавантажите!");
    }
}
