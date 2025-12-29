package com.swacky.ohmega.network;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class OhmegaNetworking {
    public static final class C2S {
        private static final Service IMPL = OhmegaCommon.loadService(Service.class);

        public static void send(CustomPacketPayload packet) {
            IMPL.send(packet);
        }

        public interface Service {
            void send(CustomPacketPayload packet);
        }
    }

    public static final class S2C {
        private static final Service IMPL = OhmegaCommon.loadService(Service.class);

        public static void send(ServerPlayer receiver, CustomPacketPayload packet) {
            IMPL.send(receiver, packet);
        }

        public interface Service {
            void send(ServerPlayer receiver, CustomPacketPayload packet);
        }
    }
}
