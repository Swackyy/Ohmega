package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.api.common.init.OhmegaItems;
import com.swacky.ohmega.api.config.OhmegaConfig;
import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SpellCheckingInspection")
public final class OhmegaItItProvider extends OhmegaLangProvider {
    public OhmegaItItProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "it_it", lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);

        // Datapack
        internalHelper.addDataPackDescription("Risorse Mod per Ohmega");

        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Anello Dell'Angelo",
                "Consente a chi lo indossa di volare",
                "Premi %s per attivare o disattivare il volo");

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Tipo Di Accessori: %s");
        //helper.addType(KEY_ACCESSORY_TYPE_NONE, "");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Generico");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Normale");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Utilità");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Speciale");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%1$s %2$s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Apri/Chiudi Inventario Accessori");

        // Config
        internalHelper.addConfigTitle("Configurazione Ohmega");

        // Client config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Client Ohmega", "Configurazione Client Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Modalità Compatibilità",
                "Disabilita alcune funzioni utili ma per lo più invisibili che possono migliorare la compatibilità delle mod in rari casi");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Stile Pulsanti",
                """
                        Stile del pulsante dell'inventario accessori
                        DEFAULT: Lo stile di default dei pulsanti Ohmega
                        LEGACY: Uno stile ispirato alla Curios/Baubles che renderizza il pulsante accanto al modello del giocatore nell'inventario
                        TAG: Un piccolo pulsante simile ad un tag che appare appena fuori dall'angolo superiore dell'inventario
                        HIDDEN: Non mostrerà alcun pulsante, si utilizzi il tasto dedicato invece per aprire l'inventario accessori""");
        /*internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Lato Dell'Inventario",
                "Il lato dell'inventario nel quale l'inventario accessori sarà posizionato");*/
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Mostra suggerimento quando passa il cursore del mouse",
                "Se vero, verrà visualizzato un suggerimento del tipo di index di accessori quando si passa sopra col mouse");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Numero Di Colonne Massime",
                "Numero di colonne massime da renderizzare");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Slot Massimi Per Colonna",
                """
                        La quantità massima di index per colonna
                        Se superata, verrà creata una nuova colonna se non supera "maxColumns\"""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Slot Massimi Renderizzabili Per Colonna",
                "La quantità massima di index da renderizzare per colonna");

        // Server config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Server Ohmega", "Configurazione Server Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Tipi Di Slot",
                "Definisce i tipi e il numero di index nell'inventario accessori");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Modifica");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Associazione Tasti Tipi Di Slot",
                "Definisce i tipi di accessori che possono essere associati ad un tasto");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Modifica");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Mantieni Comportamento Accessori",
                """
                        Definisce come il drop degli accessori, in caso di morte del giocatore
                        DEFAULT: Utilizza la gamerule "keepInventory" vanilla
                        ALWAYS_ON: Non si perderanno mai gli accessori alla morte del giocatore
                        ALWAYS_OFF: Si perderanno sempre gli accessori alla morte del giocatore""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Disabilita i Tipi D'Accessorio",
                "Se vero, nessun tipo di accessorio sarà usato, e saranno tutti sovrascritti da \"ohmega:generic\"");
    }
}
