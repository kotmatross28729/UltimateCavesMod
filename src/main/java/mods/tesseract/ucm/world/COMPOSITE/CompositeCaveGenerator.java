package mods.tesseract.ucm.world.COMPOSITE;

import mods.tesseract.ucm.world.GREG.MapGenGregCaves;
import mods.tesseract.ucm.world.WORLEY.WorleyCaveGenerator;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenCaves;

public class CompositeCaveGenerator extends MapGenCaves {
    @Override
    public void func_151539_a(IChunkProvider c, World w, int chunkX, int chunkZ, Block[] blocks) {
        MapGenGregCaves gregCaves = new MapGenGregCaves();
        WorleyCaveGenerator worleyCaves = new WorleyCaveGenerator();

        gregCaves.func_151539_a(c, w, chunkX, chunkZ, blocks);
        worleyCaves.func_151539_a(c, w, chunkX, chunkZ, blocks);
    }
}
