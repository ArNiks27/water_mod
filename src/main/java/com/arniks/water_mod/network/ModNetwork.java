package com.arniks.water_mod.network;

import com.arniks.water_mod.Water_mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Water_mod.MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        CHANNEL.messageBuilder(ThirstSyncPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ThirstSyncPacket::encode)
                .decoder(ThirstSyncPacket::new)
                .consumerMainThread(ThirstSyncPacket::handle)
                .add();
    }
}
