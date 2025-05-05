package mods.tesseract.ucm;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mods.tesseract.ucm.config.MainConfig;
import mods.tesseract.ucm.event.CaveEventComposite;
import mods.tesseract.ucm.event.CaveEventGreg;
import mods.tesseract.ucm.event.CaveEventWorley;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "ultimatecavesmod", name = "Ultimate Caves Mod", version = "1.0.4", acceptableRemoteVersions = "*")
public class Main {
    public static final Logger LOGGER = LogManager.getLogger("ultimatecavesmod");

	@EventHandler
	public static void preInit(FMLPreInitializationEvent e) {
        if(MainConfig.enableGregCaves && MainConfig.enableWorleyCaves)
            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventComposite());
        else if(MainConfig.enableGregCaves)
            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventGreg());
        else if(MainConfig.enableWorleyCaves)
            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventWorley());
    }
}
