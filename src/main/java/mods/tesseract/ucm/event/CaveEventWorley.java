package mods.tesseract.ucm.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.tesseract.ucm.world.WorleyCaveGenerator;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class CaveEventWorley {
	@SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCaveEvent(InitMapGenEvent event) {
		if (event.type == InitMapGenEvent.EventType.CAVE && !event.originalGen.getClass().equals(WorleyCaveGenerator.class)) {
	        event.newGen = new WorleyCaveGenerator();
	    }
	}
}
