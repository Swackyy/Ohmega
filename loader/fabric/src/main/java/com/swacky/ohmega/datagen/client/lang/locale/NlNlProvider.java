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

@SuppressWarnings("SpellCheckingInspection")
public final class NlNlProvider extends OhmegaLangProvider {
    public NlNlProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "nl_nl", lookup);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
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
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Open/Sluit Accessoires Inventaris");

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
        /*internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Inventaris Zijde",
                "De kant van de inventaris waar de accessoire inventaris geplaatst wordt");*/
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

        // ConfigurationScreen Forge port UI translations
        // Titles
        internalHelper.addConfigPort("title", "%s Configuratie");
        internalHelper.addConfigPortTitle("client", "%s Client Configuratie");
        internalHelper.addConfigPortTitle("server", "%s Server Configuratie");
        internalHelper.addConfigPortTitle("common", "%s Algemene Configuratie");
        // Types
        internalHelper.addConfigPortType("client", "Client Instellingen");
        internalHelper.addConfigPortType("server", "Algemene Instellingen");
        internalHelper.addConfigPortType("common", "Server Instellingen");
        // Misc
        internalHelper.addConfigPort("notonline", "Deze instellingen worden bepaald door de server en kunnen niet worden gewijzigd terwijl je online speelt.");
        internalHelper.addConfigPort("notlan", "Deze instellingen worden bepaald door de server en kunnen niet worden gewijzigd terwijl je online speelt.");
        internalHelper.addConfigPort("notloaded", "Deze instellingen zijn alleen beschikbaar wanneer een wereld geladen is.");
        internalHelper.addConfigPort("unsupportedelement", "Deze waarde kan niet worden bewerkt in de UI. Neem contact op met de mod auteur over het verstrekken van een aangepaste UI hiervoor.");
        //internalHelper.addConfigPort("longstring", "This value is too long to be edited in the UI. Please edit it in the config file.");
        internalHelper.addConfigPort("section", "%s...");
        internalHelper.addConfigPort("sectiontext", "Bewerken");
        internalHelper.addConfigPort("breadcrumb.order", "%1$s %2$s %3$s");
        internalHelper.addConfigPort("breadcrumb.separator", ">");
        internalHelper.addConfigPort("listelement", "%s:");
        internalHelper.addConfigPort("undo", "Herstel");
        internalHelper.addConfigPort("undo.tooltip", "Wijzigingen alleen op dit scherm ongedaan maken.");
        internalHelper.addConfigPort("reset", "Reset");
        internalHelper.addConfigPort("reset.tooltip", "Draait alles op dit scherm terug naar zijn standaardwaarde.");
        internalHelper.addConfigPort("newlistelement", "+");
        internalHelper.addConfigPort("listelementup", "\u23f6");
        internalHelper.addConfigPort("listelementdown", "\u23f7");
        internalHelper.addConfigPort("listelementremove", "\u274c");
        internalHelper.addConfigPort("rangetooltip", "Bereik: %s");
        internalHelper.addConfigPort("filenametooltip", "Bestand: \"%s\"");
        internalHelper.addConfigPort("common", "Algemene Opties");
        internalHelper.addConfigPort("client", "Client Opties");
        internalHelper.addConfigPort("server", "Server Opties");
        internalHelper.addConfigPort("restart.game.title", "Minecraft moet opnieuw worden gestart");
        internalHelper.addConfigPort("restart.game.text", "Één of meer van de gemaakte configuratie wijzigingen zullen pas in effect gaan nadat je het spel start.");
        internalHelper.addConfigPort("restart.server.title", "Wereld moet herladen worden");
        internalHelper.addConfigPort("restart.server.text", "Één of meer van de gemaakte configuratie wijzigingen zullen pas in effect gaan nadat je het wereld opnieuw opstart.");
        internalHelper.addConfigPort("restart.return", "Negeer");
        internalHelper.addConfigPort("restart.return.tooltip", "Jouw wijzigingen gaan pas na een het opnieuw opstarten in werking!");
    }
}
