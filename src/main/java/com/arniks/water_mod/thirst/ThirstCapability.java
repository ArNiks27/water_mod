package com.arniks.water_mod.thirst;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.client.ClientThirstData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Water_mod.MODID)
public class ThirstCapability {
    public static final Capability<PlayerThirst> THIRST = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation ID = new ResourceLocation(Water_mod.MODID, "thirst");

    public static LazyOptional<PlayerThirst> get(Player player) {
        return player.getCapability(THIRST);
    }

    public static float getCurrent(Player player) {
        if (player.level().isClientSide) return ClientThirstData.get();
        return get(player).map(PlayerThirst::get).orElse(PlayerThirst.MAX);
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ID, new Provider());
        }
    }

    @Mod.EventBusSubscriber(modid = Water_mod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Register {
        @SubscribeEvent
        public static void register(RegisterCapabilitiesEvent event) {
            event.register(PlayerThirst.class);
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final PlayerThirst thirst = new PlayerThirst();
        private final LazyOptional<PlayerThirst> opt = LazyOptional.of(() -> thirst);

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return THIRST.orEmpty(cap, opt);
        }

        @Override public CompoundTag serializeNBT() { return thirst.save(); }
        @Override public void deserializeNBT(CompoundTag nbt) { thirst.load(nbt); }
    }
}
