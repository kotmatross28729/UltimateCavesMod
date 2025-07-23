package mods.tesseract.ucm.world.WORLEY;

import mods.tesseract.mycelium.world.ChunkPrimer;
import mods.tesseract.ucm.Utils;
import mods.tesseract.ucm.config.MainConfig;
import mods.tesseract.ucm.config.WorleyCavesConfig;
import mods.tesseract.ucm.util.FastNoise;
import mods.tesseract.ucm.util.WorleyUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public class WorleyCaveGenerator extends MapGenCaves {
    private final MapGenBase surfaceCaves = new MapGenSurfaceCaves();
    private final WorleyUtil worleyF1divF3 = new WorleyUtil();
    private FastNoise displacementNoisePerlin = new FastNoise();
    private final MapGenBase replacementCaves;
    private final int maxCaveHeight = WorleyCavesConfig.maxCaveHeight,
            minCaveHeight = WorleyCavesConfig.minCaveHeight;
    private final float noiseCutoff = WorleyCavesConfig.noiseCutoffValue,
            warpAmplifier = WorleyCavesConfig.warpAmplifier,
            easeInDepth = WorleyCavesConfig.easeInDepth,
            yCompression = WorleyCavesConfig.verticalCompressionMultiplier,
            xzCompression = WorleyCavesConfig.horizonalCompressionMultiplier;
    private final int lavaDepth = MainConfig.caveLavaLevel;
    private static final Block lava = Blocks.flowing_lava;
    private static final int HAS_CAVES_FLAG = 129;
    
    public WorleyCaveGenerator() {
        MapGenBase moddedCaveGen = TerrainGen.getModdedMapGen(this, InitMapGenEvent.EventType.CAVE);
        replacementCaves = (moddedCaveGen != this) ? moddedCaveGen : new MapGenCaves();
    }
    
    @Override
    public void func_151539_a(IChunkProvider provider, World worldIn, int x, int z, Block[] blocks) {
        ChunkPrimer primer = new ChunkPrimer(blocks);
        int currentDim = worldIn.provider.dimensionId;
        
//        boolean useVanillaCaves = Main.revertBlacklist
//                ? !isDimensionBlacklisted(currentDim)
//                : isDimensionBlacklisted(currentDim);
        boolean useVanillaCaves = MainConfig.revertBlacklist != Utils.isDimensionBlacklisted(currentDim);
        //revert to vanilla cave generation for blacklisted dims
        if (useVanillaCaves) {
            this.replacementCaves.func_151539_a(provider, worldIn, x, z, blocks);
            return;
        }
        
        this.worldObj = worldIn;
        int seed2 = new Random(worldObj.getSeed()).nextInt();
        
        worleyF1divF3.SetSeed(seed2);
        worleyF1divF3.SetFrequency(0.016f);
        
        displacementNoisePerlin = new FastNoise(seed2);
        displacementNoisePerlin.SetNoiseType(FastNoise.NoiseType.Perlin);
        displacementNoisePerlin.SetFrequency(0.05f);
        
        this.generateWorleyCaves(worldIn, x, z, primer);
        this.surfaceCaves.func_151539_a(provider, worldIn, x, z, primer.data);
    }
    
    protected void generateWorleyCaves(World worldIn, int chunkX, int chunkZ, ChunkPrimer chunkPrimerIn) {
        int chunkMaxHeight = getMaxSurfaceHeight(chunkPrimerIn);
        int seaLevel = 63;
        float[][][] samples = sampleNoise(chunkX, chunkZ, chunkMaxHeight + 1);
        float oneQuarter = 0.25F;
        float oneHalf = 0.5F;
        BiomeGenBase currentBiome;
        int[] realPos;
        //float cutoffAdjuster = 0F; //TODO one day, perlin adjustments to cutoff
        
        //each chunk divided into 4 subchunks along X axis
        for (int x = 0; x < 4; x++) {
            //each chunk divided into 4 subchunks along Z axis
            for (int z = 0; z < 4; z++) {
                int depth = 0;
                
                //don't bother checking all the other logic if there's nothing to dig in this column
                if (samples[x][HAS_CAVES_FLAG][z] == 0 && samples[x + 1][HAS_CAVES_FLAG][z] == 0 && samples[x][HAS_CAVES_FLAG][z + 1] == 0 && samples[x + 1][HAS_CAVES_FLAG][z + 1] == 0)
                    continue;
                
                //each chunk divided into 128 subchunks along Y axis. Need lots of y sample points to not break things
                for (int y = (maxCaveHeight / 2) - 1; y >= 0; y--) {
                    //grab the 8 sample points needed from the noise values
                    float x0y0z0 = samples[x][y][z];
                    float x0y0z1 = samples[x][y][z + 1];
                    float x1y0z0 = samples[x + 1][y][z];
                    float x1y0z1 = samples[x + 1][y][z + 1];
                    float x0y1z0 = samples[x][y + 1][z];
                    float x0y1z1 = samples[x][y + 1][z + 1];
                    float x1y1z0 = samples[x + 1][y + 1][z];
                    float x1y1z1 = samples[x + 1][y + 1][z + 1];
                    
                    //how much to increment noise along y value
                    //linear interpolation from start y and end y
                    float noiseStepY00 = (x0y1z0 - x0y0z0) * -oneHalf;
                    float noiseStepY01 = (x0y1z1 - x0y0z1) * -oneHalf;
                    float noiseStepY10 = (x1y1z0 - x1y0z0) * -oneHalf;
                    float noiseStepY11 = (x1y1z1 - x1y0z1) * -oneHalf;
                    
                    //noise values of 4 corners at y=0
                    float noiseStartX0 = x0y0z0;
                    float noiseStartX1 = x0y0z1;
                    float noiseEndX0 = x1y0z0;
                    float noiseEndX1 = x1y0z1;
                    
                    // loop through 2 blocks of the Y subchunk
                    for (int suby = 1; suby >= 0; suby--) {
                        int localY = suby + y * 2;
                        float noiseStartZ = noiseStartX0;
                        float noiseEndZ = noiseStartX1;
                        
                        //how much to increment X values, linear interpolation
                        float noiseStepX0 = (noiseEndX0 - noiseStartX0) * oneQuarter;
                        float noiseStepX1 = (noiseEndX1 - noiseStartX1) * oneQuarter;
                        
                        // loop through 4 blocks of the X subchunk
                        for (int subx = 0; subx < 4; subx++) {
                            int localX = subx + x * 4;
                            int realX = localX + chunkX * 16;
                            
                            //how much to increment Z values, linear interpolation
                            float noiseStepZ = (noiseEndZ - noiseStartZ) * oneQuarter;
                            
                            //Y and X already interpolated, just need to interpolate final 4 Z block to get final noise value
                            float noiseVal = noiseStartZ;
                            
                            // loop through 4 blocks of the Z subchunk
                            for (int subz = 0; subz < 4; subz++) {
                                int localZ = subz + z * 4;
                                int realZ = localZ + chunkZ * 16;
                                realPos = new int[]{realX, localY, realZ};
                                currentBiome = null;
                                
                                if (depth == 0) {
                                    //only checks depth once per 4x4 subchunk
                                    if (subx == 0 && subz == 0) {
                                        Block currentBlock = chunkPrimerIn.getBlockState(localX, localY, localZ);
                                        currentBiome = worldObj.provider.getBiomeGenForCoords(realPos[0], realPos[2]);//world.getBiome(realPos);
                                        
                                        //use isDigable to skip leaves/wood getting counted as surface
                                        if (Utils.canReplaceBlock(currentBlock, Blocks.air) || currentBlock == currentBiome.topBlock || currentBlock == currentBiome.fillerBlock) {
                                            depth++;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (subx == 0 && subz == 0) {
                                    //already hit surface, simply increment depth counter
                                    depth++;
                                }
                                
                                float adjustedNoiseCutoff = noiseCutoff;// + cutoffAdjuster;
                                if (depth < easeInDepth) {
                                    //higher threshold at surface, normal threshold below easeInDepth
                                    adjustedNoiseCutoff = 1;//(float) clampedLerp(noiseCutoff, surfaceCutoff, (easeInDepth - (float) depth) / easeInDepth);
                                    
                                }
                                
                                //increase cutoff as we get closer to the minCaveHeight so it's not all flat floors
                                if (localY < (minCaveHeight + 5)) {
                                    adjustedNoiseCutoff += (float) (((minCaveHeight + 5) - localY) * 0.05);
                                }
                                
                                if (noiseVal > adjustedNoiseCutoff) {
                                    Block aboveBlock = chunkPrimerIn.getBlockState(localX, localY + 1, localZ);
                                    if (!Utils.isFluidBlock(aboveBlock) || localY <= lavaDepth) {
                                        //if we are in the easeInDepth range or near sea level or subH2O is installed, do some extra checks for water before digging
                                        if ((depth < easeInDepth || localY > (seaLevel - 8)) && localY > lavaDepth) {
                                            if (localX < 15)
                                                if (Utils.isFluidBlock(chunkPrimerIn.getBlockState(localX + 1, localY, localZ)))
                                                    continue;
                                            if (localX > 0)
                                                if (Utils.isFluidBlock(chunkPrimerIn.getBlockState(localX - 1, localY, localZ)))
                                                    continue;
                                            if (localZ < 15)
                                                if (Utils.isFluidBlock(chunkPrimerIn.getBlockState(localX, localY, localZ + 1)))
                                                    continue;
                                            if (localZ > 0)
                                                if (Utils.isFluidBlock(chunkPrimerIn.getBlockState(localX, localY, localZ - 1)))
                                                    continue;
                                        }
                                        Block currentBlock = chunkPrimerIn.getBlockState(localX, localY, localZ);
                                        if (currentBiome == null)
                                            currentBiome = worldObj.provider.getBiomeGenForCoords(realPos[0], realPos[2]);//world.getBiome(realPos);
                                        
                                        boolean foundTopBlock = currentBlock == currentBiome.topBlock;
                                        digBlock(chunkPrimerIn, localX, localY, localZ, chunkX, chunkZ, foundTopBlock, currentBlock, aboveBlock, currentBiome);
                                    }
                                }
                                
                                noiseVal += noiseStepZ;
                            }
                            
                            noiseStartZ += noiseStepX0;
                            noiseEndZ += noiseStepX1;
                        }
                        
                        noiseStartX0 += noiseStepY00;
                        noiseStartX1 += noiseStepY01;
                        noiseEndX0 += noiseStepY10;
                        noiseEndX1 += noiseStepY11;
                    }
                }
            }
        }
    }
    
    public float[][][] sampleNoise(int chunkX, int chunkZ, int maxSurfaceHeight) {
        int originalMaxHeight = 128;
        float[][][] noiseSamples = new float[5][130][5];
        float noise;
        for (int x = 0; x < 5; x++) {
            int realX = x * 4 + (chunkX << 4);
            for (int z = 0; z < 5; z++) {
                int realZ = z * 4 + (chunkZ << 4);
                
                int columnHasCaveFlag = 0;
                
                //loop from top down for y values so we can adjust noise above current y later on
                for (int y = 128; y >= 0; y--) {
                    float realY = y * 2;
                    if (realY > maxSurfaceHeight || realY > maxCaveHeight || realY < minCaveHeight) {
                        //if outside of valid cave range set noise value below normal minimum of -1.0
                        noiseSamples[x][y][z] = -1.1F;
                    } else {
                        //Experiment making the cave system more chaotic the more you descend
                        ///TODO might be too dramatic down at lava level
                        float dispAmp = (float) (warpAmplifier * ((originalMaxHeight - y) / (originalMaxHeight * 0.85)));
                        
                        float xDisp = 0f;
                        float yDisp = 0f;
                        float zDisp = 0f;
                        
                        xDisp = displacementNoisePerlin.GetNoise(realX, realZ) * dispAmp;
                        yDisp = displacementNoisePerlin.GetNoise(realX, realZ + 67.0f) * dispAmp;
                        zDisp = displacementNoisePerlin.GetNoise(realX, realZ + 149.0f) * dispAmp;
                        
                        //doubling the y frequency to get some more caves
                        noise = worleyF1divF3.SingleCellular3Edge(realX * xzCompression + xDisp, realY * yCompression + yDisp, realZ * xzCompression + zDisp);
                        noiseSamples[x][y][z] = noise;
                        
                        if (noise > noiseCutoff) {
                            columnHasCaveFlag = 1;
                            //if noise is below cutoff, adjust values of neighbors
                            //helps prevent caves fracturing during interpolation
                            
                            if (x > 0) noiseSamples[x - 1][y][z] = (noise * 0.2f) + (noiseSamples[x - 1][y][z] * 0.8f);
                            if (z > 0) noiseSamples[x][y][z - 1] = (noise * 0.2f) + (noiseSamples[x][y][z - 1] * 0.8f);
                            
                            //more heavily adjust y above 'air block' noise values to give players more headroom
                            if (y < 128) {
                                float noiseAbove = noiseSamples[x][y + 1][z];
                                if (noise > noiseAbove)
                                    noiseSamples[x][y + 1][z] = (noise * 0.8F) + (noiseAbove * 0.2F);
                                if (y < 127) {
                                    float noiseTwoAbove = noiseSamples[x][y + 2][z];
                                    if (noise > noiseTwoAbove)
                                        noiseSamples[x][y + 2][z] = (noise * 0.35F) + (noiseTwoAbove * 0.65F);
                                }
                            }
                            
                        }
                    }
                }
                noiseSamples[x][HAS_CAVES_FLAG][z] = columnHasCaveFlag; //used to skip cave digging logic when we know there is nothing to dig out
            }
        }
        return noiseSamples;
    }
    
    private int recursiveBinarySurfaceSearch(ChunkPrimer chunkPrimer, int localX, int localZ, int searchTop, int searchBottom) {
        int top = searchTop;
        if (searchTop > searchBottom) {
            int searchMid = (searchBottom + searchTop) / 2;
            if (Utils.canReplaceBlock(chunkPrimer.getBlockState(localX, searchMid, localZ), null)) {
                top = recursiveBinarySurfaceSearch(chunkPrimer, localX, localZ, searchTop, searchMid + 1);
            } else {
                top = recursiveBinarySurfaceSearch(chunkPrimer, localX, localZ, searchMid, searchBottom);
            }
        }
        return top;
    }
    
    private int getMaxSurfaceHeight(ChunkPrimer primer) {
        int y = 0;
        int[] cords = {2, 6, 3, 11, 7, 2, 9, 13, 12, 4, 13, 9};
        
        for (int i = 0; i < cords.length; i += 2) {
            int test = recursiveBinarySurfaceSearch(primer, cords[i], cords[i + 1], 255, 0);
            if (test > y) {
                y = test;
                if (y > maxCaveHeight) return y;
            }
        }
        return y;
    }
    
    protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, Block block, Block up, BiomeGenBase biome) {
        Block top = biome.topBlock;
        Block filler = biome.fillerBlock;
        if (!Utils.isFluidBlock(up) && (Utils.canReplaceBlock(block, up) || block == top || block == filler)) {
            if (y <= lavaDepth) {
                data.setBlockState(x, y, z, lava);
            } else {
                data.setBlockState(x, y, z, null);
                if (foundTop && data.getBlockState(x, y - 1, z) == filler) {
                    data.setBlockState(x, y - 1, z, top);
                }
            }
        }
    }
}