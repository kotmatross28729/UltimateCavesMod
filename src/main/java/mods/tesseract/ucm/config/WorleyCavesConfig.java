package mods.tesseract.ucm.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class WorleyCavesConfig {
	//WORLEY CAVES
	public static float noiseCutoffValue = -0.14f;
	public static float surfaceCutoffValue = -0.081f;
	public static float warpAmplifier = 8.0f;
	public static int easeInDepth = 15;
	public static float verticalCompressionMultiplier = 2.0f;
	public static float horizonalCompressionMultiplier = 1.0f;
	public static int maxCaveHeight = 128;
	public static int minCaveHeight = 1;
	public static String lavaBlock = "minecraft:lava";
	public static int lavaDepth = 2;
	public static boolean allowReplaceMoreBlocks = true;
	
	public static void loadWorleyConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		
		//WORLEY CAVES
		noiseCutoffValue = config.getFloat("noiseCutoffValue", "WorleyCaves", noiseCutoffValue, -1f, 1f, "Controls size of caves. Smaller values = larger caves. Between -1.0 and 1.0");
		surfaceCutoffValue = config.getFloat("surfaceCutoffValue", "WorleyCaves", surfaceCutoffValue, -1f, 1f, "Controls size of caves at the surface. Smaller values = more caves break through the surface. Between -1.0 and 1.0");
		warpAmplifier = config.getFloat("warpAmplifier", "WorleyCaves", warpAmplifier, 0f, Float.MAX_VALUE, "Controls how much to warp caves. Lower values = straighter caves");
		easeInDepth = config.getInt("easeInDepth", "WorleyCaves", easeInDepth, 0, Integer.MAX_VALUE, "Reduces number of caves at surface level, becoming more common until caves generate normally X number of blocks below the surface");
		verticalCompressionMultiplier = config.getFloat("verticalCompressionMultiplier", "WorleyCaves", verticalCompressionMultiplier, 0, Float.MAX_VALUE, "Squishes caves on the Y axis. Lower values = taller caves and more steep drops");
		horizonalCompressionMultiplier = config.getFloat("horizonalCompressionMultiplier", "WorleyCaves", horizonalCompressionMultiplier, 0, Float.MAX_VALUE, "Streches (when < 1.0) or compresses (when > 1.0) cave generation along X and Z axis");
		maxCaveHeight = config.getInt("maxCaveHeight", "WorleyCaves", maxCaveHeight, 1, 256, "Caves will not attempt to generate above this y level. Range 1-256");
		minCaveHeight = config.getInt("minCaveHeight", "WorleyCaves", minCaveHeight, 1, 256, "Caves will not attempt to generate below this y level. Range 1-256");
		lavaBlock = config.getString("lavaBlock", "WorleyCaves", lavaBlock, "Block to use when generating large lava lakes below lavaDepth (usually y=10)");
		lavaDepth = config.getInt("lavaDepth", "WorleyCaves", lavaDepth, 1, 256, "Air blocks at or below this y level will generate as lavaBlock");
		allowReplaceMoreBlocks = config.getBoolean("allowReplaceMoreBlocks", "WorleyCaves", allowReplaceMoreBlocks, "Allow replacing more blocks with caves (useful for mods which completely overwrite world gen)");
		
		if(config.hasChanged()) {
			config.save();
		}
	}
	
}
