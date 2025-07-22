package mods.tesseract.ucm;

import mods.tesseract.ucm.config.GregCavesConfig;
import mods.tesseract.ucm.config.MainConfig;
import mods.tesseract.ucm.config.WorleyCavesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class Utils {
	public static boolean isDimensionBlacklisted(int dimensionId) {
		for (int blacklistedDim : MainConfig.blackListedDims) {
			if (dimensionId == blacklistedDim) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isExceptionBiome(BiomeGenBase biome) {
		return biome == BiomeGenBase.desert || biome == BiomeGenBase.beach || biome == BiomeGenBase.mushroomIsland;
	}
	public static void digBlock(World worldObj, Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
		
		boolean isException = isExceptionBiome(biome);
		Block top = isException ? Blocks.grass : biome.topBlock;
		Block filler = isException ? Blocks.dirt : biome.fillerBlock;
		Block block = data[index];
		
		if (canReplaceBlock(block, null) || block == filler || block == top) {
			if (y < GregCavesConfig.caveLavaLevel - 1) {
				data[index] = Blocks.flowing_lava;
			} else {
				data[index] = null;
				if (foundTop && data[index - 1] == filler) {
					data[index - 1] = top;
				}
			}
		}
	}
	
	public static boolean canReplaceBlock(Block block, Block blockUp) {
		if (block == null || (blockUp != null && blockUp.getMaterial() == Material.water))
			return false;
		return (WorleyCavesConfig.allowReplaceMoreBlocks && block.getMaterial() == Material.rock)
				|| block == Blocks.stone
				|| block == Blocks.dirt
				|| block == Blocks.grass
				|| block == Blocks.hardened_clay
				|| block == Blocks.stained_hardened_clay
				|| block == Blocks.snow_layer
				|| block == Blocks.sandstone
				|| block == Blocks.mycelium
				|| block == Blocks.sand
				|| block == Blocks.gravel;
	}
	
}
