package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SpellCheckingInspection")
public final class NlNlProvider extends OhmegaLangProvider {
    public NlNlProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "nl_nl", lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);

        // Datapack
        internalHelper.addDataPackDescription("Bronnen voor Ohmega mod");

        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Engelenring",
                "Staat de drager toe om te vliegen",
                "Druk op %s om vlucht aan of uit te zetten");

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Type Accessoire: %s");
        //helper.addType(KEY_ACCESSORY_TYPE_NONE, "");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Algemeen");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Normaal");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Nuttig");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Speciaal");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%s %s");
        builder.add(KEY_BIND_CATEGORY, "Ohmega");
        builder.add(KEY_BIND_OPEN_ACC_INV, "Open/Sluit Accessoires Inventaris");

        // Config
        internalHelper.addConfigTitle("Ohmega Configuratie");

        // Client config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Ohmega Client", "Ohmega Client Configuratie");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Compatibiliteitsmodus",
                "Schakelt wat nuttige, maar vooral onopvallende functies uit die de compatibiliteit in zeldzame gevallen verbeteren");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BUTTON_STYLE_KEY,
                "Knop Stijl",
                """
                        Stijl van de accessoire inventaris knop
                        DEFAULT: De normale Ohmega knop stijl
                        LEGACY: Een curios/baubles geïnspireerde knop die naast het spelersmodel staat
                        TAG: Een kleine markering die in de bovenste hoek van de inventaris staat
                        HIDDEN: Zal verborgen blijven, gebruik de toegewezen toets om de inventaris te openen""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.INVENTORY_SIDE_KEY,
                "Inventaris Zijde",
                "De kant van de inventaris waar de accessoire inventaris geplaatst wordt");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Laat Zwevende Tooltips Zien",
                "Indien waar, laat een tooltip scherm zien van het type accessoire vak als de muis erover zweeft");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Maximaal Aantal Kolommen",
                "Het maximaal aantal kolommen om te renderen");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Maximaal Kolom Vakken",
                """
                        Het maximaal aantal vakken per kolom
                        Bij overschrijding zal een nieuwe kolom gemaakt worden als het niet "maxColumns" overschrijdt""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Maximaal Kolom Renderende Vakken",
                "Het maximaal aantal slots dat per kolom wordt gerenderd");

        // Server config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Ohmega Server", "Ohmega Server Configuratie");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SLOT_TYPES_KEY,
                "Slot Types",
                "Definieert de types en het aantal vakken in de accessoire inventaris");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.SLOT_TYPES_KEY, "Bewerken");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Toetsgebonden Slot Types",
                "Definieert het type accessoires dat toetsgebonden kan worden");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Bewerken");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Behouden van Gedrag Accessoires",
                """
                        Definieert hoe om te gaan met sterven van spelers in de vorm van het laten vallen van accessoires
                        DEFAULT: Gebruikt de vanilla "keepInventory" spelregel
                        ALWAYS_ON: Zal nooit de accessoires laten vallen bij sterven
                        ALWAYS_OFF: Zal altijd de accessoires laten vallen bij sterven""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Accessoire Types Uitschakelen",
                "Indien waar, zullen er geen accessoire types worden gebruikt, en worden ze allemaal overschreden en veranderd in \"ohmega:generic\"");
    }
}
