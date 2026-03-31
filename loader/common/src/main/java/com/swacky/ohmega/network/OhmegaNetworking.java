package com.swacky.ohmega.network;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

// todo: move handlers to here
// todo: will probably need to add entityIds to a lot of packets for allowing entities to have accessory inventories
// todo: reorder packet registration on forge and neoforge to be alphabetical
public final class OhmegaNetworking {
    public static void bootstrap() {
        C2S.bootstrap();
        S2C.bootstrap();
    }

    public static final class C2S {
        private static final Service IMPL = Ohmega.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(CustomPacketPayload packet) {
            IMPL.send(packet);
        }

        public interface Service {
            void send(CustomPacketPayload packet);
        }
    }

    public static final class S2C {
        private static final Service IMPL = Ohmega.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(ServerPlayer receiver, CustomPacketPayload packet) {
            IMPL.send(receiver, packet);
        }

        public interface Service {
            void send(ServerPlayer receiver, CustomPacketPayload packet);
        }
    }
}
