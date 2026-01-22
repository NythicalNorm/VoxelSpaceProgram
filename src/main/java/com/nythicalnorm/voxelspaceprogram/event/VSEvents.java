package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import org.valkyrienskies.core.api.events.PhysTickEvent;
import org.valkyrienskies.mod.api.ValkyrienSkies;

public class VSEvents {
   public static void onPhysTick(PhysTickEvent event) {
       if (SolarSystem.get() != null) {
           SolarSystem solarSys = SolarSystem.get();
           if (event.getWorld().getDimension().equals(ValkyrienSkies.api().getDimensionId(solarSys.getSpaceLevel()))) {
               solarSys.OnTick(event.getDelta());
           }
       }
   }
}
