package com.nythicalnorm.nythicalSpaceProgram.event;


import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModForgeEvents {

}
