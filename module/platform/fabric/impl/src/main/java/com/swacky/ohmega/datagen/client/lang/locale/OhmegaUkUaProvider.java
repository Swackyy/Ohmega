package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.client.screen.widget.FlipEntityButton;
import com.swacky.ohmega.api.client.screen.widget.ToggleExtensionButton;
import com.swacky.ohmega.api.client.screen.widget.ToggleVisibilityButton;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.client.command.node.ExtensionsCommand;
import com.swacky.ohmega.client.command.node.InfoCommand;
import com.swacky.ohmega.client.screen.widget.CrowdinButton;
import com.swacky.ohmega.common.command.node.ClearCommand;
import com.swacky.ohmega.common.command.node.ItemCommand;
import com.swacky.ohmega.common.command.node.ItemsCommand;
import com.swacky.ohmega.common.command.node.SlotsCommand;
import com.swacky.ohmega.common.command.node.TypesCommand;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.api.config.OhmegaConfig;
import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class OhmegaUkUaProvider extends OhmegaLangProvider {
    private static final String SURVIVAL_INVENTORY = "інвентар виживання";
    private static final String CREATIVE_INVENTORY = "інвентар творчости";
    private static final String X_COORDINATE = "dat way";
    private static final String Y_COORDINATE = "dis way";
    private static final String EXTENSION_DESCRIPTION_TEMPLATE = """
            {0}-координата розширення аксесуара в меню {1} відносно основного сегмента поточного екрана""";
    private static final String TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE = """
            {0}-координата кнопки розширення в меню {1} при використанні стилю кнопки «{2}», відносно основного сегмента поточного екрана""";
    private static final String FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE = """
            {0}-координата кнопки зміни сутности в меню {1} відносно головного сегмента поточного екрана""";

    public OhmegaUkUaProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "uk_ua", lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);
        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Тип аксесуара: %s");
        helper.addType(KEY_ACCESSORY_TYPE_NONE, "Жоден");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Загальний");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Звичайний");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Приладдя");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Спеціальний");

        // Commands
        // Misc
        builder.add(CommandHelper.CONTEXT_HOVER, "(наведення)");
        builder.add(CommandHelper.EXCEPTION_ARGUMENT_LIVING, "Для цього аргументу можна вказати лише живі сутності, однак наданий селектор уключає неживих сутностей");
        // Exceptions
        builder.add(AccessoryTypeArgument.EXCEPTION_UNKNOWN_TYPE_KEY, "Невідомий тип аксесуара: «%s»");
        builder.add(AccessoryTypeArgument.EXCEPTION_UNSPECIFIABLE_TYPE_KEY, "Тип аксесуара «%s» позначено як невизначений, і цей аргумент приймає лише визначені типи");
        // Clear
        builder.add(ClearCommand.ROOT_EXCEPTION_MULTIPLE, "В інвентарі аксесуарів %s сутностей не знайдено відповідних предметів");
        builder.add(ClearCommand.ROOT_EXCEPTION_SINGLE, "В інвентарі аксесуарів %s не знайдено відповідних предметів");
        builder.add(ClearCommand.ROOT_FEEDBACK_MULTIPLE, "Вилучено %s предметів з інвентарю аксесуарів %s сутностей");
        builder.add(ClearCommand.ROOT_FEEDBACK_SINGLE, "Вилучено %s предметів з інвентарю аксесуарів %s");
        // Extensions
        builder.add(ExtensionsCommand.ROOT_FEEDBACK, "Ohmega розпізнає такі розширення аксесуарів %s: %s");
        // Info
        builder.add(InfoCommand.CROWDIN_FEEDBACK, "Подумайте про переклад Ohmega на Crowdin, натиснувши по цьому повідомленні!");
        builder.add(InfoCommand.DISCORD_FEEDBACK, "Якщо вам потрібна допомога з API або ви хочете надіслати відгук, натисніть по цьому повідомленні, щоб приєднатися до сервера Discord Ohmega");
        builder.add(InfoCommand.REPORT_FEEDBACK, "Дякуємо за використання Ohmega, якщо ви хочете звітувати помилку, натисніть по цьому повідомленні, щоб відкрити наш засіб відстеження проблем");
        builder.add(InfoCommand.WIKI_FEEDBACK, "Хочете створити мод за допомогою Ohmega? Натисніть по цьому повідомленні, щоб відкрити вікі Ohmega, щоб дізнатися, як");
        // Item
        builder.add(ItemCommand.ARGUMENT_INDEX_EXCEPTION, "Індекс %s виходить за межі допустимого діапазону! Має бути меншим за %s");
        builder.add(ItemCommand.GET_FEEDBACK, "Сутність %s має %s в індексі %s свого інвентарю аксесуарів");
        builder.add(ItemCommand.SET_FEEDBACK_MULTIPLE, "Установлено стіс в індексі %s інвентарю аксесуарів %s сутностей на %s %s");
        builder.add(ItemCommand.SET_FEEDBACK_SINGLE, "Установлено стіс в індексі %s інвентарю %s на %s %s");
        builder.add(ItemCommand.TYPE_FEEDBACK, "Предмет «%s» має усталений тип аксесуара «%s»");
        // Items
        builder.add(ItemsCommand.ROOT_FEEDBACK, "%s має такі предмети у своєму інвентарі аксесуарів: %s");
        builder.add(ItemsCommand.ROOT_FEEDBACK_EMPTY, "%s не має предметів у своєму інвентарі аксесуарів");
        // Slots
        builder.add(SlotsCommand.ADD_FEEDBACK_MULTIPLE, "Додано %s слотів для аксесуарів типу «%s» до інвентарів аксесуарів %s сутностей");
        builder.add(SlotsCommand.ADD_FEEDBACK_SINGLE, "Додано %s слотів для аксесуарів типу «%s» до інвентарю аксесуарів %s");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_MULTIPLE, "У %s сутностей не знайдено слотів для аксесуарів");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_SINGLE, "У %s не знайдено слотів для аксесуарів");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_MULTIPLE, "Видалено %s слотів для аксесуарів у %s сутностей");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_SINGLE, "идалено %s слотів для аксесуарів у %s");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_MULTIPLE, "Установлено слоти для аксесуарів %s сутностей на усталені");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_SINGLE, "Установлено слоти для аксесуарів %s на усталені");
        builder.add(SlotsCommand.GET_FEEDBACK_RANGED, "%s має %s слотів для аксесуарів із такими типами: %s");
        builder.add(SlotsCommand.GET_FEEDBACK, "Слот з індексом %s в інвентарі аксесуарів %s має тип «%s»");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_MULTIPLE, "Слоти для аксесуарів %s успадковано для %s сутностей");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_MULTIPLE, "Слоти для аксесуарів %s успадковано для %s сутностей для індексування %s до %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_SINGLE, "Слоти для аксесуарів %s успадковано для %s для індексування %s до %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_SINGLE, "Слоти для аксесуарів %s успадковано для %s");
        builder.add(SlotsCommand.INSERT_FEEDBACK_MULTIPLE, "Додано %s слотів для аксесуарів типу «%s» за індексом %s в інвентарі аксесуарів %s сутностей");
        builder.add(SlotsCommand.INSERT_FEEDBACK_SINGLE, "Додано %s слотів для аксесуарів типу «%s» за індексом %s в інвентарі аксесуарів %s");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_MULTIPLE, "Видалено %s слотів для аксесуарів у %s сутностей");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_SINGLE, "Видалено %s слотів для аксесуарів у %s");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_ARGUMENT, "Аргумент «%s» зі значенням %s виходить за межі допустимого діапазону, має бути більшим або рівним аргументу «%s»");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_SLOTS, "Аргумент «%s» зі значенням %s виходить за межі допустимого діапазону, він має бути меншим або рівним кількости слотів для аксесуарів %s");
        builder.add(SlotsCommand.SET_FEEDBACK_MULTIPLE, "Установлено слот для аксесуарів з індексом %s в інвентарях аксесуарів %s сутностей тип «%s»");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_MULTIPLE, "Установлено слоти для аксесуарів з індексами %s на %s в інвентарях аксесуарів %s сутностей тип «%s»");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_SINGLE, "Установлено слот для аксесуарів з індексами %s на %s в інвентарях аксесуарів %s тип «%s»");
        builder.add(SlotsCommand.SET_FEEDBACK_SINGLE, "Установлено слот для аксесуарів з індексом %s в інвентарі аксесуарів %s для типу «%s»");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_DEFAULT, "%s відстежує усталені слоти для аксесуарів");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_NONE, "%s не відстежує будь-які інші слоти для аксесуарів");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_MULTIPLE, "Не відстежуються слоти для аксесуарів %s сутностей");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_SINGLE, "Не відстежуються слоти для аксесуарів %s");
        // Type
        builder.add(TypesCommand.LIST_FEEDBACK, "У цьому світі розпізнано %s типів аксесуарів: %s");
        builder.add(TypesCommand.QUERY_FEEDBACK, "Тип аксесуара «%s» має такі властивості: %s");

        // Config
        internalHelper.addConfigTitle("Налаштування Ohmega");

        // Client
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Клієнт Ohmega", "Клієнтські налаштування Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Режим сумісности",
                """
                        Вимикає деякі корисні, але здебільшого непомітні функції, які можуть покращити сумісність модів у рідкісних випадках""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_KEY,
                "Показати спливне повідомлення перекладу",
                """
                        Якщо ввімкнено, буде показано спливне повідомлення з посиланням на переклади Ohmega на Crowdin при приєднанні до світу.
                        Це автоматично вимикається після першого спливного вікна, тому воно показується лише один раз""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Перемкнути стиль кнопки розширення",
                """
                        Стиль кнопки розширення для аксесуарів
                        DEFAULT: Стандартний стиль кнопки Ohmega
                        LEGACY: Кнопка в стилі Curios/Baubles, що показуються поруч із моделлю гравця в інвентарі
                        TAG_LEFT: Маленька кнопка у вигляді ярлика, що з’являється біля верхнього лівого кута інвентарю
                        TAG_RIGHT: Маленька кнопка у вигляді ярлика, що з’являється біля верхнього правого кута інвентарю
                        HIDDEN: Не показується, для відкриття розширення аксесуарів використовуйте призначену клавішу""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.ACCESSORY_EXTENSION_ID_KEY,
                "ID розширення аксесуара",
                """
                        Тип розширення аксесуара для використання, інші моди можуть реєструвати спеціальні розширення аксесуарів, які можна вибрати тут""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Напрямок заповнення",
                """
                        Напрямок, у якому будуть заповнюватися слоти для аксесуарів""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Макс. стовпців",
                """
                        Максимальна кількість стовпців для промальовування""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Макс. слотів для стовпців",
                """
                        Максимальна кількість слотів на стовпець.
                        Якщо перевищено, буде створено новий стовпець, якщо він не перевищує «maxColumns»""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Макс. слотів для промальовування стовпців",
                """
                        Максимальна кількість слотів для промальовування в стовпцю""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Показати спливну підказку наведення",
                """
                        Якщо ввімкнено, буде видно спливну підказку типу слота для аксесуарів, коли на нього наведено курсор""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.RENDER_ACCESSORIES_KEY,
                "Промальовування аксесуарів",
                """
                        Глобальний параметр промальовування аксесуарів. Якщо ввімкнено, промальовуватиме аксесуари на сутностях, коли це доречно, або не промальовуватиме їх зовсім, якщо вимкнено""");
        // Edit UI
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_EDIT_UI,
                "Редагувати інтерфейс",
                """
                        Містить певні значення налаштування, що стосуються редагування інтерфейсу""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_EDIT_UI, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BACKGROUND_ALPHA_KEY,
                "Прозорість тла",
                """
                        Значення прозорости для тла інтерфейсу редагування""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAGNETICS_STRENGTH_KEY,
                "Сила магнетизму",
                """
                        Максимальна відстань у пікселях, у межах якої магнітні лінії враховуються для захоплення""");
        // Positions
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON,
                "Перемкнути кнопку розширення",
                """
                        Містить позиції для перемикання кнопки розширення""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON, "Редагувати");
        // Survival
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_POSITIONS,
                "Позиція",
                """
                        Керує розташуванням певних елементів Ohmega на різних екранах""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_POSITIONS, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_SURVIVAL,
                "Інвентар виживання",
                """
                        Містить позиції для інвентарю виживання""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_SURVIVAL, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_X_KEY,
                "Розширення X",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_Y_KEY,
                "Розширення Y",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Перемкнути усталене розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Перемкнути усталене розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Перемкнути старе розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Перемкнути старе розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Перемкнути лівий теґ розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Перемкнути лівий теґ розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Перемкнути правий теґ розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Перемкнути правий теґ розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY,
                "Змінити X кнопки сутности",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY,
                "Змінити Y кнопки сутности",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY));
        // Creative
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_CREATIVE,
                "Інвентар творчости",
                """
                        Містить позицію для інвентарю творчости""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_CREATIVE, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_X_KEY,
                "Розширення X",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_Y_KEY,
                "Розширення Y",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Перемкнути усталене розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Перемкнути усталене розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Перемкнути старе розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Перемкнути старе розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Перемкнути лівий теґ розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Перемкнути лівий теґ розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Перемкнути правий теґ розширення X кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Перемкнути правий теґ розширення Y кнопки",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_X_KEY,
                "Змінити X кнопки сутности",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY,
                "Змінити Y кнопки сутности",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY));

        // Server
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Сервер Ohmega", "Серверні налаштування Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Усталені типи слотів",
                """
                        Визначає типи та кількість слотів, що використовуються усталено для інвентарю аксесуарів""");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SHRINK_DEFAULT_SLOT_TYPES_KEY,
                "Зменшити усталені типи слотів",
                """
                        Якщо ввімкнено, усталені типи слотів автоматично скорочуватимуться відповідно до типів зареєстрованих предметів.
                        Це означає, що якщо певний тип аксесуара існує, але жоден предмет не має відповідного теґу, усі екземпляри цього типу будуть видалені зі списку усталених слотів""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Типи слотів, прив’язані до клавіш",
                """
                        Керує типами аксесуарів, які можна прив'язати до клавіші""");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Редагувати");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Зберігати поведінку аксесуарів",
                """
                        Керує тим, як упоратися зі смертю гравця з точки зору викидання аксесуарів.
                        DEFAULT: використовує стандартне ігрове правило «keepInventory».
                        ALWAYS_ON: ніколи не викидає аксесуари після смерти.
                        ALWAYS_OFF: завжди викидає аксесуари після смерти""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Вимкнути типи аксесуарів",
                """
                        Якщо ввімкнено, фактично жодні типи аксесуарів не використовуватимуться, і всі вони будуть перевизначені, змінюючи їх усі на «ohmega:generic»""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.ALLOW_HIDE_ACCESSORIES_KEY,
                "Дозволити приховування аксесуарів",
                """
                        Завадить гравцям перемикати видимість своїх аксесуарів, якщо вимкнено, щоб вони завжди промальовувалися""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.INJECT_VANILLA_CLEAR_KEY,
                "Додати до стандартного очищення",
                """
                        Додає операцію очищення аксесуарів до стандартного коду очищення""");

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


        // Datapack
        internalHelper.addDataPackDescription("Ресурси мода для Ohmega");
        internalHelper.addDataPackDescription(OhmegaClient.PACK_DARK_ID, "Пакет темної теми для Ohmega");

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Ангельське кільце",
                "Надає носію політ",
                "Натисніть %s, щоб літати");
        builder.add(Ohmega.MODID + ".item.modifiers.accessory_active", "Коли діє:");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%s %s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.EDIT_MAGNETICS, "Редагувати інтерфейс Magnetics");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_DOWN, "Змістити вниз");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_LEFT, "Змістити вліво");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_RIGHT, "Змістити вправо");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_UP, "Змістити вгору");
        internalHelper.add(OhmegaBinds.EDIT_REDO, "Повторити");
        internalHelper.add(OhmegaBinds.EDIT_SHOW_LINES, "Показати лінії відстані");
        internalHelper.add(OhmegaBinds.EDIT_UNDO, "Скасувати");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Відкрити інвентар аксесуарів");
        internalHelper.add(OhmegaBinds.OPEN_EDIT_UI, "Відкрити інтерфейс розширення");

        // Toast
        internalHelper.addToast("translation.title", "Перекладіть Ohmega");
        internalHelper.addToast("translation.message", "Розгляньте можливість перекладу Ohmega на Crowdin через меню налаштувань");

        // Widget
        builder.add(CrowdinButton.TRANSLATION_KEY, "Crowdin");
        builder.add(FlipEntityButton.TRANSLATION_KEY, "Змінити сутність");
        builder.add(ToggleExtensionButton.TRANSLATION_KEY, "Перемкнути розширення");
        builder.add(ToggleVisibilityButton.TRANSLATION_KEY, "Перемкнути видимість");
    }
}
