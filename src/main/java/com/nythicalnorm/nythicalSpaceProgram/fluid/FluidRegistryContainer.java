package com.nythicalnorm.nythicalSpaceProgram.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.Item.custom.CryogenicBucketItem;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Credit to TurtyWurty for this amazing class, here's the video where it is from: https://www.youtube.com/watch?v=ESEI1-tcdHI
public class FluidRegistryContainer {
    public final RegistryObject<FluidType> type;
    public final FluidType.Properties typeProperties;
    public final RegistryObject<LiquidBlock> block;
    public final RegistryObject<Item> fluidContainer;
    private ForgeFlowingFluid.Properties properties;
    public final RegistryObject<ForgeFlowingFluid.Source> source;
    public final RegistryObject<ForgeFlowingFluid.Flowing> flowing;

    public FluidRegistryContainer(String name, FluidType.Properties typeProperties,
                                  Supplier<IClientFluidTypeExtensions> clientExtensions, @Nullable AdditionalProperties additionalProperties,
                                  BlockBehaviour.Properties blockProperties, Item.Properties itemProperties, boolean isCryogenic, boolean isGaseous) {
        this.typeProperties = typeProperties;
        this.type = ModFluids.FLUID_TYPES.register(name, () -> new FluidType(this.typeProperties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(clientExtensions.get());
            }
        });

        this.source = ModFluids.FLUIDS.register(name + "_source", () -> new ForgeFlowingFluid.Source(this.properties));
        this.flowing = ModFluids.FLUIDS.register(name + "_flowing",
                () -> new ForgeFlowingFluid.Flowing(this.properties));

        this.properties = new ForgeFlowingFluid.Properties(this.type, this.source, this.flowing);
        if (additionalProperties != null) {
            this.properties.explosionResistance(additionalProperties.explosionResistance)
                    .levelDecreasePerBlock(additionalProperties.levelDecreasePerBlock)
                    .slopeFindDistance(additionalProperties.slopeFindDistance).tickRate(additionalProperties.tickRate);
        }

        //LiquidBlock blockType = ;
        if (isCryogenic && !isGaseous) {
            this.block = ModBlocks.BLOCKS.register(name, () -> new CryogenicFluid(this.source, blockProperties));
            this.fluidContainer = ModItems.ITEMS.register(name + "_bucket", () -> new CryogenicBucketItem(this.source, itemProperties));
        }
        else if (!isGaseous) {
            this.block = ModBlocks.BLOCKS.register(name, () -> new LiquidBlock(this.source, blockProperties));
            this.fluidContainer = ModItems.ITEMS.register(name + "_bucket", () -> new BucketItem(this.source, itemProperties));
        }
        else {
            this.block = ModBlocks.BLOCKS.register(name, () -> new LiquidBlock(this.source, blockProperties));
            this.fluidContainer = ModItems.ITEMS.register(name + "_canister", () -> new BucketItem(this.source, itemProperties));
        }
        this.properties.block(this.block);

        this.properties.bucket(this.fluidContainer);
    }

    public FluidRegistryContainer(String name, FluidType.Properties typeProperties,
                                  Supplier<IClientFluidTypeExtensions> clientExtensions, BlockBehaviour.Properties blockProperties,
                                  Item.Properties itemProperties, boolean IsCryogenic, boolean IsGaseous) {
        this(name, typeProperties, clientExtensions, null, blockProperties, itemProperties, IsCryogenic, IsGaseous);
    }

    public ForgeFlowingFluid.Properties getProperties() {
        return this.properties;
    }

    public static IClientFluidTypeExtensions createExtension(ClientExtensions extensions) {
        return new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getFlowingTexture() {
                return extensions.flowing;
            }

            @Nullable
            @Override
            public ResourceLocation getOverlayTexture() {
                return extensions.overlay;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft minecraft) {
                return extensions.renderOverlay;
            }

            @Override
            public ResourceLocation getStillTexture() {
                return extensions.still;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return extensions.tint;//extensions.tint == null ? 0xFFFFFFFF : getTintColor();
            }

            @Override
            public int getTintColor(FluidStack Stack) {
                return extensions.tint;//extensions.tint == null ? 0xFFFFFFFF : extensions.tintFunction.apply(null,null,null);
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return extensions.fogColor == null
                        ? IClientFluidTypeExtensions.super.modifyFogColor(camera, partialTick, level, renderDistance,
                        darkenWorldAmount, fluidFogColor)
                        : extensions.fogColor;
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick,
                                        float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f);
            }
        };
    }

    public static class AdditionalProperties {
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1;
        private int slopeFindDistance = 4;
        private int tickRate = 5;

        public AdditionalProperties explosionResistance(float resistance) {
            this.explosionResistance = resistance;
            return this;
        }

        public AdditionalProperties levelDecreasePerBlock(int decrease) {
            this.levelDecreasePerBlock = decrease;
            return this;
        }

        public AdditionalProperties slopeFindDistance(int distance) {
            this.slopeFindDistance = distance;
            return this;
        }

        public AdditionalProperties tickRate(int rate) {
            this.tickRate = rate;
            return this;
        }
    }

    public static class ClientExtensions {
        private ResourceLocation still;
        private ResourceLocation flowing;
        private ResourceLocation overlay;
        private ResourceLocation renderOverlay;
        private Vector3f fogColor;
        private int tint = 1;

        private final String modid;

        public ClientExtensions(String modid, String fluidName) {
            this.modid = modid;
            still(fluidName);
            flowing(fluidName);
            overlay(fluidName);
        }

        public ClientExtensions flowing(String name) {
            return flowing(name, "block");
        }

        public ClientExtensions flowing(String name, String folder) {
            this.flowing = ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, folder + "/" + name + "_flow");
            return this;
        }

        public ClientExtensions fogColor(float red, float green, float blue) {
            this.fogColor = new Vector3f(red, green, blue);
            return this;
        }

        public ClientExtensions overlay(String name) {
            return overlay(name, "block");
        }

        public ClientExtensions overlay(String name, String folder) {
            this.overlay = ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, folder + "/" + name + "_overlay");
            return renderOverlay(ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "textures/" + folder + "/" + name + "_overlay.png"));
        }

        public ClientExtensions renderOverlay(ResourceLocation path) {
            this.renderOverlay = path;
            return this;
        }

        public ClientExtensions still(String name) {
            return still(name, "block");
        }

        public ClientExtensions still(String name, String folder) {
            this.still = ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, folder + "/" + name + "_still");
            return this;
        }

        public ClientExtensions tint(int tint) {
            this.tint = tint;
            return this;
        }
    }
}
