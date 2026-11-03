package com.arniks.water_mod.thirst;

import com.arniks.water_mod.Config;
import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.network.ModNetwork;
import com.arniks.water_mod.network.ThirstSyncPacket;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Water_mod.MODID)
public class ThirstEvents {

    private static boolean tracked(Player p) {
        return p.isAlive() && !p.isCreative() && !p.isSpectator();
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        ThirstCapability.get(player).ifPresent(thirst -> {
            if (tracked(player)) {
                float perTick = PlayerThirst.MAX / (float) (Config.IDLE_SECONDS.get() * 20.0);
                float drain = perTick;
                if (player.isSprinting()) drain *= Config.SPRINT_MULTIPLIER.get().floatValue();
                if (player.onGround() && player.getBlockStateOn().is(BlockTags.SAND)) {
                    drain += PlayerThirst.MAX / (float) (Config.SAND_SECONDS.get() * 20.0);
                    level.sendParticles( ParticleTypes.SMOKE, player.getX(), player.getY(),
                            player.getZ(), 1, 0.1, 0, 0.1, 0.05);
                }
                thirst.drain(drain);

                if (thirst.get() <= 0f && player.tickCount % 80 == 0) {
                    player.hurt(ThirstDamage.source(player.level()), 2.0F);
                }
            }
            if (thirst.needsSync()) sync(player, thirst);
        });
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && tracked(player)) {
            float cost = Config.JUMP_COST.get().floatValue() * (player.isSprinting() ? 2f : 1f);
            ThirstCapability.get(player).ifPresent(t -> t.drain(cost));
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) return;
        event.getOriginal().reviveCaps();
        ThirstCapability.get(event.getOriginal()).ifPresent(old ->
                ThirstCapability.get(event.getEntity()).ifPresent(n -> n.copyFrom(old)));
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) { resync(e.getEntity()); }
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) { resync(e.getEntity()); }
    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) { resync(e.getEntity()); }

    private static void resync(Player p) {
        if (p instanceof ServerPlayer sp) ThirstCapability.get(sp).ifPresent(t -> sync(sp, t));
    }

    public static void sync(ServerPlayer player, PlayerThirst thirst) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ThirstSyncPacket(thirst.get()));
        thirst.markSynced();
    }

    public static void addThirst(ServerPlayer player, float amount) {
        ThirstCapability.get(player).ifPresent(t -> t.add(amount));
    }
}
