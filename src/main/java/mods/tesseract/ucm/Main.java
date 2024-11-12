package mods.tesseract.ucm;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mods.tesseract.ucm.event.CaveEventComposite;
import mods.tesseract.ucm.event.CaveEventGreg;
import mods.tesseract.ucm.event.CaveEventWorley;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "ultimatecavesmod", name = "Ultimate Caves Mod", version = "1.0.1", acceptableRemoteVersions = "*")
public class Main
{
    public static final Logger LOGGER = LogManager.getLogger("ultimatecavesmod");

    //GREG CAVES
    public static boolean smoothBedrock;
    public static boolean reduceOreGen;
    public static int reduceOreGenY;
    public static float reduceOreGenRate;
    public static int caveLavaLevel;
    //public static boolean disableUndergroundLiquid;

    //WORLEY CAVES
    public static float noiseCutoffValue = -0.18f;
    public static float surfaceCutoffValue = -0.081f;
    public static float warpAmplifier = 8.0f;
    public static int easeInDepth = 15;
    public static float verticalCompressionMultiplier = 2.0f;
    public static float horizonalCompressionMultiplier = 1.0f;
    public static int[] blackListedDims = {-1};
    public static int maxCaveHeight = 128;
    public static int minCaveHeight = 1;
    public static String lavaBlock = "minecraft:lava";
    public static int lavaDepth = 2;
    public static boolean allowReplaceMoreBlocks = true;

    public static boolean enableWorleyCaves;
    public static boolean enableGregCaves;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent e)
	{
        Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());

        enableGregCaves = cfg.getBoolean("enableGregCaves", "Functions", true, "Enable GregCaves generation.");
        enableWorleyCaves = cfg.getBoolean("enableWorleyCaves", "Functions", true, "Enable WorleyCaves generation.");

        //GREG CAVES
        smoothBedrock = cfg.getBoolean("smoothBedrock", "GregCaves", false, "Only generates one layer of bedrock.");
        reduceOreGen = cfg.getBoolean("reduceOreGen", "GregCaves", false, "Reduce ores in the deep.");
        reduceOreGenY = cfg.getInt("reduceOreGenY", "GregCaves", 33, 0, 256, "Reduce ores start height.");
        reduceOreGenRate = cfg.getFloat("reduceOreGenRate", "GregCaves", 0.6F, 0, 1, "1 = all, 0 = does nothing");
        caveLavaLevel = cfg.getInt("caveLavaLevel", "GregCaves", 2, 0, 256, "For greg caves requires smoothBedrock on to work.");
        //disableUndergroundLiquid = cfg.getBoolean("disableUndergroundLiquid", "GregCaves", true, "Disable water and lava source generations in underground.");

        //WORLEY CAVES
        noiseCutoffValue = cfg.getFloat("noiseCutoffValue", "WorleyCaves", noiseCutoffValue, -1f, 1f, "Controls size of caves. Smaller values = larger caves. Between -1.0 and 1.0");
        surfaceCutoffValue = cfg.getFloat("surfaceCutoffValue", "WorleyCaves", surfaceCutoffValue, -1f, 1f, "Controls size of caves at the surface. Smaller values = more caves break through the surface. Between -1.0 and 1.0");
        warpAmplifier = cfg.getFloat("warpAmplifier", "WorleyCaves", warpAmplifier, 0f, Float.MAX_VALUE, "Controls how much to warp caves. Lower values = straighter caves");
        easeInDepth = cfg.getInt("easeInDepth", "WorleyCaves", easeInDepth, 0, Integer.MAX_VALUE, "Reduces number of caves at surface level, becoming more common until caves generate normally X number of blocks below the surface");
        verticalCompressionMultiplier = cfg.getFloat("verticalCompressionMultiplier", "WorleyCaves", verticalCompressionMultiplier, 0, Float.MAX_VALUE, "Squishes caves on the Y axis. Lower values = taller caves and more steep drops");
        horizonalCompressionMultiplier = cfg.getFloat("horizonalCompressionMultiplier", "WorleyCaves", horizonalCompressionMultiplier, 0, Float.MAX_VALUE, "Streches (when < 1.0) or compresses (when > 1.0) cave generation along X and Z axis");
        blackListedDims = cfg.get("WorleyCaves", "blackListedDims", blackListedDims, "Dimension IDs that will use Vanilla cave generation rather than Worley's Caves").getIntList();
        maxCaveHeight = cfg.getInt("maxCaveHeight", "WorleyCaves", maxCaveHeight, 1, 256, "Caves will not attempt to generate above this y level. Range 1-256");
        minCaveHeight = cfg.getInt("minCaveHeight", "WorleyCaves", minCaveHeight, 1, 256, "Caves will not attempt to generate below this y level. Range 1-256");
        lavaBlock = cfg.getString("lavaBlock", "WorleyCaves", lavaBlock, "Block to use when generating large lava lakes below lavaDepth (usually y=10)");
        lavaDepth = cfg.getInt("lavaDepth", "WorleyCaves", lavaDepth, 1, 256, "Air blocks at or below this y level will generate as lavaBlock");
        allowReplaceMoreBlocks = cfg.getBoolean("allowReplaceMoreBlocks", "WorleyCaves", allowReplaceMoreBlocks, "Allow replacing more blocks with caves (useful for mods which completely overwrite world gen)");

        if(enableGregCaves && enableWorleyCaves)
            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventComposite());
        else if(enableGregCaves)
            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventGreg());
        else if(enableWorleyCaves)
            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventWorley());

//         if(enableGregCaves)
//            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventGreg());
//         if(enableWorleyCaves)
//            MinecraftForge.TERRAIN_GEN_BUS.register(new CaveEventWorley());

        if (cfg.hasChanged())
            cfg.save();
    }
}
