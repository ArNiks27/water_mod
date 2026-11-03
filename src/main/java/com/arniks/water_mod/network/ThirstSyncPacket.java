package com.arniks.water_mod.network;

import com.arniks.water_mod.client.ClientThirstData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ThirstSyncPacket {
    private final float thirst;

    public ThirstSyncPacket(float thirst) { this.thirst = thirst; }
    public ThirstSyncPacket(FriendlyByteBuf buf) { this.thirst = buf.readFloat(); }

    public void encode(FriendlyByteBuf buf) { buf.writeFloat(thirst); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ClientThirstData.set(thirst);
    }
}
