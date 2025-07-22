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
}
