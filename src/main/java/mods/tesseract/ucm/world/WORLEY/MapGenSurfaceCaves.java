package mods.tesseract.ucm.world.WORLEY;

import mods.tesseract.ucm.Utils;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenSurfaceCaves extends MapGenCaves {
	protected void func_151538_a(World worldIn, int dx, int dz, int cx, int cz, Block[] blocks) {
		int topY = 128, bottomY = 40;
		int numAttempts = 0;
		if (this.rand.nextInt(100) < 7) {
			int randVal = this.rand.nextInt(15) + 1;
			numAttempts = this.rand.nextInt(this.rand.nextInt(randVal) + 1) + 1;
		}
		
		for (int i = 0; i < numAttempts; ++i) {
			double caveStartX = (dx << 4) + this.rand.nextInt(16);
			double caveStartY = this.rand.nextInt(topY - bottomY) + bottomY;
			double caveStartZ = (dz << 4) + this.rand.nextInt(16);
			
			float yaw = this.rand.nextFloat() * ((float) Math.PI * 2F);
			float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
			float width = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
			
			this.func_151541_a(this.rand.nextLong(), cx, cz, blocks, caveStartX, caveStartY, caveStartZ, width, yaw, pitch, 0, 0, 1.0D);
		}
	}
	
	@Override
	protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
		Utils.digBlock(worldObj, data, index, x, y, z, chunkX, chunkZ, foundTop);
	}
}
