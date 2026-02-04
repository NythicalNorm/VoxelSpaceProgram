package com.nythicalnorm.voxelspaceprogram.mixin.spaceentites;

import com.nythicalnorm.voxelspaceprogram.spacecraft.player.AbstractPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.PlayerOrbitAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerSpaceMixin implements PlayerOrbitAccessor {
    @Unique
    private AbstractPlayerOrbitBody playerOrbitBody;

    @Unique
    @Override
    public AbstractPlayerOrbitBody getOrbit() {
        return playerOrbitBody;
    }

    @Unique
    @Override
    public void setOrbit(AbstractPlayerOrbitBody abstractPlayerOrbitBody) {
        this.playerOrbitBody = abstractPlayerOrbitBody;
    }

}
