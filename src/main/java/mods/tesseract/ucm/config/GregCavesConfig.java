package mods.tesseract.ucm.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GregCavesConfig {
	
	public static boolean smoothBedrock;
	public static boolean reduceOreGen;
	public static int reduceOreGenY;
	public static float reduceOreGenRate;
	public static int caveLavaLevel;
	
	public static void loadGregConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		
		smoothBedrock = config.getBoolean("smoothBedrock", "GregCaves", false, "Only generates one layer of bedrock.");
		reduceOreGen = config.getBoolean("reduceOreGen", "GregCaves", false, "Reduce ores in the deep.");
		reduceOreGenY = config.getInt("reduceOreGenY", "GregCaves", 33, 0, 256, "Reduce ores start height.");
		reduceOreGenRate = config.getFloat("reduceOreGenRate", "GregCaves", 0.6F, 0, 1, "1 = all, 0 = does nothing");
		caveLavaLevel = config.getInt("caveLavaLevel", "GregCaves", 2, 0, 256, "For greg caves requires smoothBedrock true to work. In combined mode you will also need to change \"lavaDepth\" from WorleyCaves");
		
		if(config.hasChanged()) {
			config.save();
		}
	}
	
}
