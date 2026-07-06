package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.api.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SpellCheckingInspection")
public final class OhmegaEsEsProvider extends OhmegaLangProvider {
    public OhmegaEsEsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "es_es", lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);

        // Datapack
        internalHelper.addDataPackDescription("Recursos de mod para Ohmega");

        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Anillo de Ángel",
                "Permite volar al portador",
                "Presiona %s para alternar el vuelo");

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Tipo de Accesorio: %s");
        //helper.addType(KEY_ACCESSORY_TYPE_NONE, "");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Genérico");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Normal");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Útiles");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Especial");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%1$s %2$s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Abrir/Cerrar Inventario de Accesorios");

        // Config
        internalHelper.addConfigTitle("Configuración de Ohmega");

        // Client config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Cliente de Ohmega", "Configuración del Cliente Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Modo de Compatibilidad",
                "Deshabilita algunas características útiles pero que pasan desapercibidas que pueden mejorar la compatibilidad con otros mods en algunos casos raros");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Estilo del Botón",
                """
                        Estilo del botón del inventario de accesorios
                        POR DEFECTO: El estilo normal del botón de Ohmega
                        ANTIGUO: Un botón inspirado en Curios/Baubles que se renderiza al lado del modelo en el inventario del jugador
                        ETIQUETA: Un botón pequeño como una etiqueta en la esquina superior del inventario
                        OCULTO: No se va a mostrar, usa una tecla asignada para abrir el inventario de accesorios""");
        /*internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Lado del Inventario",
                "El lado del inventario en el que el accesorio será colocado");*/
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Hacer aparecer una pista de la utilidad al tener el ratón encima",
                "Si es verdadero, se mostrará una pista en un cuadro de la utilidad al tener el ratón encima del espacio del accesorio");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Columnas Máximas",
                "Las columnas máximas a renderizar");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Espacios Máximos por Columna",
                """
                        Máxima cantidad de espacios por columna
                        Si se excede, una nueva columna se creará si no excede "maxColumns\"""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Columnas de Espacios Máximas a Renderizar",
                "Cantidad Máxima de Espacios a Renderizar por Columna");

        // Server config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Server de Ohmega", "Configuración del Servidor de Ohmega");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Tipos de Espacios",
                "Define los tipos y números de los espacios en el inventario de accesorios");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Editar");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Tipos de espacios con teclas asignadas",
                "Define los tipos de accesorios que pueden tener teclas asignadas");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Editar");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Mantener el Comportamiento de los Accesorios",
                """
                        Define cómo manejar la muerte del jugador a la hora de soltar los accesorios
                        DEFAULT: Usa el game-rule vanilla "keepInventory"
                        ALWAYS_ON: Nunca va a dejar caer los accesorios al morir
                        ALWAYS_OFF: Siempre dejará caer los accesorios al morir""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Deshabilitar Tipos de Accesorios",
                "Si es verdadero, no se usarán tipos de accesorios, y serán reemplazados, cambiándolos todos a \"ohmega:generic\"");

        // ConfigurationScreen Forge port UI translations
        // Misc
        internalHelper.addConfigPort("notonline", "Los ajustes aquí están determinados por el servidor y no se pueden cambiar mientras estén en línea.");
    }
}
