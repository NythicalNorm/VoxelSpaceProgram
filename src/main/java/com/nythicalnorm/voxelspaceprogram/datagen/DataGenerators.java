package com.nythicalnorm.voxelspaceprogram.datagen;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new VSPRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), VSPLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(), new VSPBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new VSPItemModelProvider(packOutput, existingFileHelper));

        VSPBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(), new VSPBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new VSPItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new VSPFluidTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new VSPWorldGenProvider(packOutput, lookupProvider));
    }
}
