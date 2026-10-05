package com.swacky.ohmega.api.common.accessorytype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;

public final class AdvancementSlotRewards {
    public static final AdvancementSlotRewards EMPTY = new Builder().build();
    public static final Codec<AdvancementSlotRewards> CODEC = Codec.list(Entry.CODEC).xmap(
            entries -> {
                AdvancementSlotRewards rewards = new AdvancementSlotRewards(entries.size());

                for (Entry entry : entries) {
                    rewards.map.put(entry.key, entry.value);
                }

                return rewards;
            }, rewards -> {
                ArrayList<Entry> entries = new ArrayList<>();

                for (Object2IntMap.Entry<Identifier> entry : rewards.map.object2IntEntrySet()) {
                    entries.add(new Entry(entry.getKey(), entry.getIntValue()));
                }

                return entries;
            });

    public static final StreamCodec<ByteBuf, AdvancementSlotRewards> STREAM_CODEC = StreamCodec.of(
            (buf, rewards) -> {
                VarInt.write(buf, rewards.size());

                for (Object2IntMap.Entry<Identifier> entry : rewards.map.object2IntEntrySet()) {
                    Identifier.STREAM_CODEC.encode(buf, entry.getKey());
                    VarInt.write(buf, entry.getIntValue());
                }
            }, buf -> {
                int size = VarInt.read(buf);
                AdvancementSlotRewards rewards = new AdvancementSlotRewards(size);

                for (int i = 0; i < size; i++) {
                    rewards.map.put(Identifier.STREAM_CODEC.decode(buf), VarInt.read(buf));
                }

                return rewards;
            });

    private final Object2IntOpenHashMap<Identifier> map;

    private AdvancementSlotRewards(Object2IntOpenHashMap<Identifier> map) {
        this.map = map;
    }

    private AdvancementSlotRewards(int size) {
        this(new Object2IntOpenHashMap<>(size));
    }

    public int size() {
        return map.size();
    }

    public Object2IntMap.FastEntrySet<Identifier> entrySet() {
        return map.object2IntEntrySet();
    }

    private record Entry(Identifier key, int value) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Identifier.CODEC.fieldOf("id").forGetter(Entry::key),
                Codec.INT.fieldOf("amount").forGetter(Entry::value)
        ).apply(builder, Entry::new));
    }

    public static final class Builder {
        private final Object2IntOpenHashMap<Identifier> map = new Object2IntOpenHashMap<>();

        public Builder put(Identifier advancementId, int amount) {
            map.put(advancementId, amount);
            return this;
        }

        public AdvancementSlotRewards build() {
            return new AdvancementSlotRewards(map.clone());
        }
    }
}
