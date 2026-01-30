package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import org.valkyrienskies.core.api.events.PhysTickEvent;
import org.valkyrienskies.core.api.events.TickEndEvent;
import org.valkyrienskies.mod.api.ValkyrienSkies;

public class VSEvents {
    public static void addListeners() {
        ValkyrienSkies.api().getPhysTickEvent().on(VSEvents::onPhysTick);
        ValkyrienSkies.api().getTickEndEvent().on(VSEvents::onTickEnd);
    }

    public static void onPhysTick(PhysTickEvent event) {
       if (SolarSystem.get() != null) {
           SolarSystem solarSys = SolarSystem.get();

           if (event.getWorld().getDimension().equals(solarSys.getSpaceLevelString())) {
               solarSys.OnPhysTick(event.getDelta());
           }
           // ShipTeleporter.OnPhysTick(event.getWorld(), event.getWorld().getDimension(), solarSys);
       }
    }

    public static void onTickEnd(TickEndEvent event){
    }
}
