package mods.tesseract.ucm.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GregCavesConfig {
	public static boolean smoothBedrock;
	public static boolean reduceOreGen;
	public static int reduceOreGenY;
	public static float reduceOreGenRate;
	public static void loadGregConfig(File configFile) {
		Configuration config = new Configuration(configFile);

		smoothBedrock = config.getBoolean("smoothBedrock", "GregCaves", true, "Only generates one layer of bedrock. Fixes unevenness on lower layers");
		reduceOreGen = config.getBoolean("reduceOreGen", "GregCaves", false, "Determines whether to reduce ore generation rate");
		reduceOreGenY = config.getInt("reduceOreGenY", "GregCaves", 33, 0, 256, "Ore generation rate reduction starts from this Y");
		reduceOreGenRate = config.getFloat("reduceOreGenRate", "GregCaves", 0.3F, 0, 1, "Ore reduction after reduceOreGenY in percent. 0.3 - 30% less ore");

		if(config.hasChanged()) {
			config.save();
		}
	}
	
}
