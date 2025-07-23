package mods.tesseract.ucm.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.tesseract.ucm.world.GREG.MapGenGregCaves;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class CaveEventGreg {
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCaveEvent(InitMapGenEvent event) {
        if (event.type == InitMapGenEvent.EventType.CAVE && !event.originalGen.getClass().equals(MapGenGregCaves.class)) {
            event.newGen = new MapGenGregCaves();
        }
    }
}
