package mods.tesseract.ucm.world.WORLEY;

import mods.tesseract.ucm.config.WorleyCavesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenSurfaceCaves extends MapGenCaves {
	protected void func_151538_a(World worldIn, int dx, int dz, int cx, int cz, Block[] blocks) {
		int topY = 128, bottomY = 40;
		int numAttempts = 0;
		if (this.rand.nextInt(100) < 7) {
			numAttempts = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);
		}
		
		for (int i = 0; i < numAttempts; ++i) {
			double caveStartX = (dx << 4) + this.rand.nextInt(16);
			double caveStartY = this.rand.nextInt(topY - bottomY) + bottomY;
			double caveStartZ = (dz << 4) + this.rand.nextInt(16);
			
			int numAddTunnelCalls = 1;
			
			
			for (int j = 0; j < numAddTunnelCalls; ++j) {
				float yaw = this.rand.nextFloat() * ((float) Math.PI * 2F);
				float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float width = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
				
				this.func_151541_a(this.rand.nextLong(), cx, cz, blocks, caveStartX, caveStartY, caveStartZ, width, yaw, pitch, 0, 0, 1.0D);
			}
		}
	}
	
	protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + (chunkX << 4), z + (chunkZ << 4));
		Block top = (isExceptionBiome(biome) ? Blocks.grass : biome.topBlock);
		Block filler = (isExceptionBiome(biome) ? Blocks.dirt : biome.fillerBlock);
		Block block = data[index];
		
		if (this.canReplaceBlock(block, null) || block == filler || block == top) {
			if (y < 6) {
				data[index] = Blocks.lava;
			} else {
				data[index] = null;
				
				if (foundTop && data[index - 1] == filler) {
					data[index - 1] = top;
				}
			}
		}
	}
	
	public boolean isExceptionBiome(BiomeGenBase biome) {
		return biome == BiomeGenBase.desert || biome == BiomeGenBase.beach;
	}
	
	public boolean canReplaceBlock(Block block, Block blockUp) {
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
