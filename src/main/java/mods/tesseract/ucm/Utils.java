package mods.tesseract.ucm;

public class Utils {
	public static boolean isDimensionBlacklisted(int dimensionId) {
		for (int blacklistedDim : Main.blackListedDims) {
			if (dimensionId == blacklistedDim) {
				return true;
			}
		}
		return false;
	}
}
