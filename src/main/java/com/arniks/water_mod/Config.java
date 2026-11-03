package com.arniks.water_mod;

import net.minecraftforge.common.ForgeConfigSpec;
// решил попробывать конфиг в готовом конструктуре IJ, нахер не пойму до сих пор. бесполезная вещь, как будто лучше геймрул сделать надо
public class Config {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue IDLE_SECONDS = B
            .comment("за сколько секунд полная шкала жажды опустеет, если стоять на месте")
            .defineInRange("idleSecondsToEmpty", 1200.0, 10.0, 1_000_000.0);
    public static final ForgeConfigSpec.DoubleValue SPRINT_MULTIPLIER = B
            .comment("во сколько раз быстрее тратится жажда при беге")
            .defineInRange("sprintMultiplier", 4.0, 1.0, 1000.0);
    public static final ForgeConfigSpec.DoubleValue JUMP_COST = B
            .comment("сколько половинок капель (шкала = 20) тратится за один прыжок. При прыжке во время бега - вдвое больше")
            .defineInRange("jumpCost", 0.15, 0.0, 20.0);
    public static final ForgeConfigSpec.DoubleValue SAND_SECONDS = B
            .comment("за сколько секунд полная шкала опустеет, если стоять на песке")
            .defineInRange("sandSecondsToEmpty", 45.0, 1.0, 1_000_000.0);
    public static final ForgeConfigSpec.DoubleValue DRINK_RESTORE = B
            .defineInRange("drinkRestore", 6.0, 0.0, 20.0);
    public static final ForgeConfigSpec.IntValue SALT_POISON_SECONDS = B
            .comment("длительность отравления после глотка солёной воды")
            .defineInRange("saltPoisonSeconds", 15, 1, 1000);
    public static final ForgeConfigSpec.BooleanValue FILL_FROM_WATER = B
            .comment("можно ли набирать воду в бурдюк ПКМ по источнику воды (в океане - солёная)")
            .define("fillFromWater", true);

    public static final ForgeConfigSpec SPEC = B.build();
}
