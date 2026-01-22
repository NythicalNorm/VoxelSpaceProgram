package com.nythicalnorm.voxelspaceprogram.storage;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;

public class SpacecraftDataStorage {
    public static final String modSaveDirPath = "voxelspaceprogram";
    private final Path modSaveFolder;

    public SpacecraftDataStorage(MinecraftServer server, PlanetsProvider planetsProvider) {
        this.modSaveFolder = server.getWorldPath(LevelResource.ROOT).resolve(SpacecraftDataStorage.modSaveDirPath);
        getOrCreateDir(modSaveFolder);

        for(CelestialBody celestialBody: planetsProvider.getAllPlanetaryBodies().values()) {
            String fileName = celestialBody.getName().concat(".dat");
            File dataFile = new File(this.modSaveFolder.resolve(fileName).toUri());
            ((ServerCelestialBody) celestialBody).setPlanetDataFile(dataFile);
        }
    }

    public Path getModSaveFolder() {
        return modSaveFolder;
    }

    public void readSpacecraftData(PlanetsProvider planetsProvider) {
        for(CelestialBody celestialBody: planetsProvider.getAllPlanetaryBodies().values()) {
            File planetFileLoc = ((ServerCelestialBody) celestialBody).getPlanetDataFile();
            if (planetFileLoc.exists()) {
                ListTag spacecraftList = readList(planetFileLoc);
                if (spacecraftList == null) {
                    continue;
                }

                for (Tag tag : spacecraftList) {
                    if (tag instanceof CompoundTag compoundTag) {
                        OrbitalBody orbitalBody = NBTEncoders.getOrbitalBody(compoundTag);
                        planetsProvider.playerJoinedOrbital(celestialBody, orbitalBody);
                    }
                }
            }
        }
    }

    public void save(PlanetsProvider planetsProvider) {
        for(CelestialBody celestialBody: planetsProvider.getAllPlanetaryBodies().values()) {
            File planetFileLoc = ((ServerCelestialBody) celestialBody).getPlanetDataFile();
            ListTag spacecraftTags = new ListTag();

            for (OrbitalBody orbitalBody : celestialBody.getChildren()) {
                if (orbitalBody instanceof EntitySpacecraftBody) {
                    CompoundTag orbitalTag = NBTEncoders.putOrbitalBody(orbitalBody);
                    spacecraftTags.add(orbitalTag);
                }
            }

            writeList(planetFileLoc, spacecraftTags);
        }
    }

    private void writeList(File dataFileLoc, ListTag tag) {
        try (
                FileOutputStream fileoutputstream = new FileOutputStream(dataFileLoc);
                DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);
        ) {
            tag.write(dataoutputstream);
        } catch (IOException exception) {
            VoxelSpaceProgram.logError("Can't save planetData file to " + dataFileLoc.getPath());
            exception.printStackTrace();
        }
    }

    private ListTag readList(File dataFileLoc) {
        if (!dataFileLoc.exists()) {
            return null;
        } else {
            ListTag listTag = null;
            try (
                    FileInputStream fileinputstream = new FileInputStream(dataFileLoc);
                    DataInputStream datainputstream = new DataInputStream(fileinputstream);
            ) {
                listTag = ListTag.TYPE.load(datainputstream, 500, NbtAccounter.UNLIMITED);
            } catch (IOException exception) {
                VoxelSpaceProgram.logError("Can't load planetData file from " + dataFileLoc.getPath() + ", is it corrupted? :( sowwy.. ");
                exception.printStackTrace();
            }

            return listTag;
        }
    }

    public static File getOrCreateDir (Path folderPath) {
        File folderDir = new File(folderPath.toUri());

        if (!folderDir.exists()) {
            boolean wasCreated = folderDir.mkdir();
            if (!wasCreated) {
                VoxelSpaceProgram.logError(folderDir.getPath() + " Directory Creation Failed.");
                return null;
            }
        }
        return folderDir;
    }
}
