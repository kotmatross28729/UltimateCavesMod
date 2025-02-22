package mods.tesseract.ucm;

import mods.tesseract.ucm.config.MainConfig;

public class Utils {
	public static boolean isDimensionBlacklisted(int dimensionId) {
		for (int blacklistedDim : MainConfig.blackListedDims) {
			if (dimensionId == blacklistedDim) {
				return true;
			}
		}
		return false;
	}
}
