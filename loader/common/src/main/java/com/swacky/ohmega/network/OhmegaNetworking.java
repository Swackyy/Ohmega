package com.swacky.ohmega.network;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.server.level.ServerPlayer;

public final class OhmegaNetworking {
    public static void bootstrap() {
        C2S.bootstrap();
        S2C.bootstrap();
    }

    public static final class C2S {
        private static final Service IMPL = OhmegaCommon.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(OhmegaPacket<?> packet) {
            IMPL.send(packet);
        }

        public interface Service {
            void send(OhmegaPacket<?> packet);
        }
    }

    public static final class S2C {
        private static final Service IMPL = OhmegaCommon.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(ServerPlayer receiver, OhmegaPacket<?> packet) {
            IMPL.send(receiver, packet);
        }

        public interface Service {
            void send(ServerPlayer receiver, OhmegaPacket<?> packet);
        }
    }
}
