package com.nythicalnorm.voxelspaceprogram.planetshine.textures;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ClientCelestialBody;
import com.nythicalnorm.voxelspaceprogram.util.LodTexUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public class ClientTexManager {
    private static final Logger log = LoggerFactory.getLogger(ClientTexManager.class);
    // private final Map<Integer, Integer> posIdTotexID = new Int2IntOpenHashMap();
    private final LodTexQuad[] lodTexQuads;
    private int lastInsertedIndex;
    private final int lodTexAtlasID;

    public ClientTexManager() {
        lodTexAtlasID = TextureUtil.generateTextureId();
        int texSize = LodTexUtils.texInOneAxisCount * LodTexUtils.textureResolution;
        TextureUtil.prepareImage(lodTexAtlasID, texSize, texSize);

        lodTexQuads = new LodTexQuad[LodTexUtils.texInOneAxisCount* LodTexUtils.texInOneAxisCount];
        lastInsertedIndex = -1;
    }

    public LodTexQuad[] getLodTexQuads() {
        return lodTexQuads;
    }

    public int getLodTexAtlasID() {
        return lodTexAtlasID;
    }

    public void incomingPlanetTexture(ClientCelestialBody planet, byte[] tex) {
        VoxelSpaceProgram.log(planet.getName() +" texture received, Size: " + tex.length);

        try {
            ByteBuffer texBytebuffer = ByteBuffer.allocateDirect(tex.length);
            texBytebuffer.put(tex);
            texBytebuffer.rewind();

            NativeImage planetImage = NativeImage.read(texBytebuffer);
            DynamicTexture texture = new DynamicTexture(planetImage);
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            ResourceLocation texResourceLocation = texturemanager.register("voxelspaceprogram/planet_tex/" + planet.getName(), texture);
            planet.setMainTexture(texResourceLocation);
        } catch (IOException e) {
            VoxelSpaceProgram.logError(e.toString());
            VoxelSpaceProgram.logError("png texture can't be parsed");
        }
    }

    public void incomingLodTexture(ResourceKey<Level> dimensionID, int textureID, int textureSize, byte[] biomeTexture) {
        if (lastInsertedIndex < lodTexQuads.length) {
            lastInsertedIndex ++;
            loadTextureToBiomeAtlas(biomeTexture, lastInsertedIndex);
            lodTexQuads[lastInsertedIndex] = new LodTexQuad(lastInsertedIndex, LodTexUtils.textureIndexToAtlasCoordinate(lastInsertedIndex), textureID);
        }
    }

    // reference: https://coffeebeancode.gitbook.io/lwjgl-game-design/tutorials/chapter-2-textures-and-shaders-and-tombstones-oh-wait...
    private void loadTextureToBiomeAtlas(byte[] tex, int arrayIndex) {
        if (tex == null) {
            return;
        }
        int width;
        int height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer texBytebuffer = ByteBuffer.allocateDirect(tex.length);
            texBytebuffer.put(tex);
            texBytebuffer.rewind();

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer loadedBuffer = STBImage.stbi_load_from_memory(texBytebuffer, w, h, channels, 4);
            if(loadedBuffer == null) {
                throw new Exception("Can't load file biome texture " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();

            // posIdTotexID.put(index, id);
            GlStateManager._bindTexture(lodTexAtlasID);

            Vector2i texPos = LodTexUtils.textureIndexToAtlasCoordinate(arrayIndex);

            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GlStateManager._texSubImage2D(GL11.GL_TEXTURE_2D, 0, texPos.x, texPos.y, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, MemoryUtil.memAddress(loadedBuffer));
            STBImage.stbi_image_free(loadedBuffer);
        } catch(Exception e){
            VoxelSpaceProgram.getLogger().error("can't parse biome texture.");
            e.printStackTrace();
        }
    }
}
