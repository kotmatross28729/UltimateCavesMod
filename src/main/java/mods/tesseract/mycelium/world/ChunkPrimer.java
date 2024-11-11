package mods.tesseract.mycelium.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ChunkPrimer {
    private static final Block DEFAULT_STATE = Blocks.air;
    public final Block[] data;

    public ChunkPrimer(Block[] data) {
        this.data = data;
    }

    public Block getBlockState(int x, int y, int z)
    {
        Block block = this.data[getBlockIndex(x, y, z)];
        return block == null ? DEFAULT_STATE : block;
    }

    public void setBlockState(int x, int y, int z, Block block)
    {
        this.data[getBlockIndex(x, y, z)] = block;
    }

    private static int getBlockIndex(int x, int y, int z)
    {
        return x << 12 | z << 8 | y;
    }

    /**
     * Counting down from the highest block in the sky, find the first non-air block for the given location
     * (actually, looks like mostly checks x, z+1? And actually checks only the very top sky block of actual x, z)
     */
    public int findGroundBlockIdx(int x, int z)
    {
        int i = (x << 12 | z << 8) + 256 - 1;

        for (int j = 255; j >= 0; --j)
        {
            Block block = this.data[i + j];

            if (block != null && block != DEFAULT_STATE)
            {
                return j;
            }
        }

        return 0;
    }
}
