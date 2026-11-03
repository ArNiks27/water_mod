package com.arniks.water_mod.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class PlayerThirst {
    public static final float MAX = 20f;

    private float thirst = MAX;
    private int lastSyncedCeil = -1;

    public float get() { return thirst; }

    public void set(float v) { thirst = Mth.clamp(v, 0f, MAX); }
    public void add(float v) { set(thirst + v); }
    public void drain(float v) { set(thirst - v); }

    public boolean isFull() { return thirst >= MAX; }

    public boolean needsSync() { return Mth.ceil(thirst) != lastSyncedCeil; }
    public void markSynced() { lastSyncedCeil = Mth.ceil(thirst); }
    public void forceResync() { lastSyncedCeil = -1; }

    public void copyFrom(PlayerThirst o) { thirst = o.thirst; }

    public CompoundTag save() {
        CompoundTag t = new CompoundTag();
        t.putFloat("Thirst", thirst);
        return t;
    }

    public void load(CompoundTag t) {
        thirst = t.contains("Thirst") ? Mth.clamp(t.getFloat("Thirst"), 0f, MAX) : MAX;
    }
}
