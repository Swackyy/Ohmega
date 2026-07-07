package com.swacky.ohmega.api.common.advancement.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class AccessoryChangeTrigger extends SimpleCriterionTrigger<AccessoryChangeTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player, ItemStack changed) {
        ArrayList<AccessoryDataEntry> entries = OhmegaDataAttachments.getData(player).getEntries();
        int[] slotsFull = {0};
        int[] slotsEmpty = {0};
        int[] slotsOccupied = {0};

        for (AccessoryDataEntry entry : entries) {
            ItemStack stack = entry.getStack();

            if (stack.isEmpty()) {
                slotsEmpty[0]++;
            } else {
                slotsOccupied[0]++;

                if (stack.getCount() >= stack.getMaxStackSize()) {
                    slotsFull[0]++;
                }
            }
        }

        this.trigger(player, instance -> instance.matches(entries, changed, slotsFull[0], slotsEmpty[0], slotsOccupied[0]));
    }

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, InventoryChangeTrigger.TriggerInstance.Slots slots, List<ItemPredicate> items) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                InventoryChangeTrigger.TriggerInstance.Slots.CODEC.optionalFieldOf("slots", InventoryChangeTrigger.TriggerInstance.Slots.ANY).forGetter(TriggerInstance::slots),
                ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(TriggerInstance::items)
        ).apply(builder, TriggerInstance::new));

        public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate.Builder... items) {
            return hasItems(Stream.of(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
        }

        public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate... items) {
            return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(items)));
        }

        @SuppressWarnings("deprecation")
        public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... items) {
            int size = items.length;
            ItemPredicate[] predicates = new ItemPredicate[items.length];

            for (int i = 0; i < size; ++i) {
                predicates[i] = new ItemPredicate(Optional.of(HolderSet.direct(items[i].asItem().builtInRegistryHolder())), MinMaxBounds.Ints.ANY, DataComponentMatchers.ANY);
            }

            return hasItems(predicates);
        }

        public boolean matches(ArrayList<AccessoryDataEntry> entries, ItemStack changed, int slotsFull, int slotsEmpty, int slotsOccupied) {
            if (!this.slots.matches(slotsFull, slotsEmpty, slotsOccupied)) {
                return false;
            } else if (this.items.isEmpty()) {
                return true;
            } else if (this.items.size() != 1) {
                List<ItemPredicate> predicates = new ObjectArrayList<>(this.items);

                for (AccessoryDataEntry entry : entries) {
                    if (predicates.isEmpty()) {
                        return true;
                    }

                    ItemStack stack = entry.getStack();

                    if (!stack.isEmpty()) {
                        predicates.removeIf(predicate -> predicate.test(stack));
                    }
                }

                return predicates.isEmpty();
            } else {
                return !changed.isEmpty() && this.items.getFirst().test(changed);
            }
        }
    }
}
