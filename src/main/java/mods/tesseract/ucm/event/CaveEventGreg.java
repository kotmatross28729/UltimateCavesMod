package mods.tesseract.ucm.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.tesseract.ucm.Main;
import mods.tesseract.ucm.world.MapGenGregCaves;
import mods.tesseract.ucm.world.WorleyCaveGenerator;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class CaveEventGreg {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCaveEvent(InitMapGenEvent event) {
        if (event.type == InitMapGenEvent.EventType.CAVE /*&& !event.originalGen.getClass().equals(WorleyCaveGenerator.class)*/ && !event.originalGen.getClass().equals(MapGenGregCaves.class)) {
            //Main.LOGGER.info("Replacing cave generation with Greg Caves, original: " + event.originalGen.getClass());
            event.newGen = new MapGenGregCaves();
        }
    }
}