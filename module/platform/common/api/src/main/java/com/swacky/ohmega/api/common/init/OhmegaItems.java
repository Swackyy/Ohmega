package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class OhmegaItems {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static Item getAngelRing() {
        return IMPL.getAngelRing();
    }

    public static Item.Properties getAngelRingProperties() {
        return new Item.Properties()
                .component(OhmegaDataComponents.getAccessoryActiveModifiers(), ItemAttributeModifiers.builder()
                        .add(Attributes.MAX_HEALTH, new AttributeModifier(
                                Ohmega.id(Ohmega.id(Service.ANGEL_RING_KEY).toLanguageKey() + ".effect.health_boost"), 4,
                                AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)
                        .build())
                .attributes(ItemAttributeModifiers.builder().add(
                        Attributes.ATTACK_DAMAGE, new AttributeModifier(
                                Ohmega.id(Ohmega.id(Service.ANGEL_RING_KEY).toLanguageKey() + ".effect.strength"), 1,
                                AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)
                        .build())
                .stacksTo(1);
    }

    public interface Service {
        String ANGEL_RING_KEY = "angel_ring";

        Item getAngelRing();
    }
}
