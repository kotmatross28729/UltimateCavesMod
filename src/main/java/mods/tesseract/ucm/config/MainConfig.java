package mods.tesseract.ucm.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class MainConfig {
	public static boolean enableWorleyCaves;
	public static boolean enableGregCaves;
	public static int[] blackListedDims = {-1};
	public static boolean revertBlacklist;
	
	public static void loadMainConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		
		enableGregCaves = config.getBoolean("enableGregCaves", "Functions", true, "Enable GregCaves generation.");
		enableWorleyCaves = config.getBoolean("enableWorleyCaves", "Functions", true, "Enable WorleyCaves generation.");
		
		blackListedDims = config.get("Functions", "blackListedDims", blackListedDims, "Dimension IDs that will use Vanilla cave generation.").getIntList();
		revertBlacklist = config.getBoolean("revertBlacklist", "Functions", false, "Flips blacklist so it becomes whitelist.");
		
		if(config.hasChanged()) {
			config.save();
		}
	}
}
