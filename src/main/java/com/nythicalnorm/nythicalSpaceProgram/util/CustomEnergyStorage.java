package com.nythicalnorm.nythicalSpaceProgram.util;

import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity) {
        super(capacity);
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergy (int energy) {
        if (energy < 0) {
            energy = 0;
        }
        else if (energy > this.capacity) {
            energy = this.capacity;
        }

        this.energy = energy;
    }

    public boolean ConsumeEnergy(int energyVal) {
        if (this.energy - energyVal < 0) {
            return false;
        }
        else {
            this.energy = this.energy - energyVal;
            return true;
        }
    }
}
