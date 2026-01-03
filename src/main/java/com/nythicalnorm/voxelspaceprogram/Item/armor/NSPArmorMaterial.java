package com.nythicalnorm.voxelspaceprogram.Item.armor;

import com.nythicalnorm.voxelspaceprogram.Item.NSPItems;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum NSPArmorMaterial implements ArmorMaterial {
    MAGNETIC("magnet", 18, new int[]{2,5,6,2}, 10, SoundEvents.ARMOR_EQUIP_IRON,
            0.2f, 0.5f, () -> Ingredient.of(NSPItems.MAGNETIZED_IRON_INGOT.get())),

    SPACESUIT("spacesuit", 18, new int[]{2,5,6,2}, 10, SoundEvents.ARMOR_EQUIP_GOLD,
            0.2f, 0.0f, () -> Ingredient.of(Items.GOLD_INGOT));

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmount;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private static final int[] BASE_DURABILITY = {11, 16, 15, 13};

    NSPArmorMaterial(String name, int durabilityMultiplier, int[] protectionAmount, int enchantmentValue, SoundEvent equipSound,
                     float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmount = protectionAmount;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {
        return BASE_DURABILITY[pType.ordinal()] * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type pType) {
        return protectionAmount[pType.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return VoxelSpaceProgram.MODID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
