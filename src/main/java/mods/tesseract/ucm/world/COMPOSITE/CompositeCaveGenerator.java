package mods.tesseract.ucm.world.COMPOSITE;

import mods.tesseract.mycelium.world.ChunkPrimer;
import mods.tesseract.ucm.Utils;
import mods.tesseract.ucm.config.GregCavesConfig;
import mods.tesseract.ucm.config.MainConfig;
import mods.tesseract.ucm.config.WorleyCavesConfig;
import mods.tesseract.ucm.util.FastNoise;
import mods.tesseract.ucm.util.WorleyUtil;
import mods.tesseract.ucm.world.GREG.NoiseCaveGenerator;
import mods.tesseract.ucm.world.WORLEY.MapGenSurfaceCaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fluids.IFluidBlock;

public class CompositeCaveGenerator extends MapGenCaves {
    //WORLEY
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
    private final int lavaDepth = WorleyCavesConfig.lavaDepth;
    private static final Block lava = Blocks.lava;
    private static final int HAS_CAVES_FLAG = 129;
    
    //GREG
    private final double[] caveNoise = new double[825];
    private static final float[] biomeWeightTable = new float[25];
    
    static {
        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float) (j * j + k * k) + 0.2F);
                biomeWeightTable[j + 2 + (k + 2) * 5] = f;
            }
        }
    }
    
    private NoiseCaveGenerator noiseCaves;
    public NoiseGeneratorOctaves noiseGen6, field_147431_j, field_147432_k, interpolationNoise;
    private double[] interpolationNoises, lowerInterpolatedNoises, upperInterpolatedNoises, depthNoises;
    
    public CompositeCaveGenerator() {
        MapGenBase moddedCaveGen = TerrainGen.getModdedMapGen(this, InitMapGenEvent.EventType.CAVE);
        replacementCaves = (moddedCaveGen != this) ? moddedCaveGen : new MapGenCaves();
    }
    
    @Override
    public void func_151539_a(IChunkProvider provider, World world, int chunkX, int chunkZ, Block[] blocks) {
        this.generate(provider, world, chunkX, chunkZ, blocks);
    }
    
    public void generate(IChunkProvider provider, World world, int chunkX, int chunkZ, Block[] blocks) {
        ChunkPrimer primer = new ChunkPrimer(blocks);
        this.worldObj = world;
        long seed = worldObj.getSeed();
        this.rand.setSeed(seed);
        
        int currentDim = worldObj.provider.dimensionId;
        
        /*
         * boolean useVanillaCaves = Main.revertBlacklist ? !isDimensionBlacklisted(currentDim)  : isDimensionBlacklisted(currentDim);
         */
        boolean useVanillaCaves = MainConfig.revertBlacklist != Utils.isDimensionBlacklisted(currentDim);
        
        //revert to vanilla cave generation for blacklisted dims
        if (useVanillaCaves) {
            this.replacementCaves.func_151539_a(provider, worldObj, chunkX, chunkZ, blocks);
            return;
        }
        
        //WORLEY
        int seed2 = this.rand.nextInt();
        
        worleyF1divF3.SetSeed(seed2);
        worleyF1divF3.SetFrequency(0.016f);
        
        displacementNoisePerlin = new FastNoise(seed2);
        displacementNoisePerlin.SetNoiseType(FastNoise.NoiseType.Perlin);
        displacementNoisePerlin.SetFrequency(0.05f);
        
        BlockFalling.fallInstantly = true;
        this.generateWorleyCaves(chunkX, chunkZ, primer);
        this.surfaceCaves.func_151539_a(provider, worldObj, chunkX, chunkZ, primer.data);
        
        //GREG
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.interpolationNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseCaves = new NoiseCaveGenerator(this.rand);
        
        int k = this.range;
        long l = this.rand.nextLong();
        long i1 = this.rand.nextLong();
        for (int j1 = chunkX - k; j1 <= chunkX + k; ++j1) {
            for (int k1 = chunkZ - k; k1 <= chunkZ + k; ++k1) {
                long l1 = (long) j1 * l;
                long i2 = (long) k1 * i1;
                this.rand.setSeed(l1 ^ i2 ^ seed);
                this.func_151538_a(worldObj, j1, k1, chunkX, chunkZ, blocks);
            }
        }
        this.generateNoiseCaves(chunkX, chunkZ, blocks);
        BlockFalling.fallInstantly = false;
    }
    
    //WORLEY
    protected void generateWorleyCaves(int chunkX, int chunkZ, ChunkPrimer chunkPrimerIn) {
        int chunkMaxHeight = getMaxSurfaceHeight(chunkPrimerIn);
        final int seaLevel = 63;
        float[][][] samples = sampleNoise(chunkX, chunkZ, chunkMaxHeight + 1);
        final float oneQuarter = 0.25F;
        final float oneHalf = 0.5F;
        BiomeGenBase currentBiome;
        
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
                                currentBiome = null;
                                
                                if (depth == 0) {
                                    //only checks depth once per 4x4 subchunk
                                    if (subx == 0 && subz == 0) {
                                        Block currentBlock = chunkPrimerIn.getBlockState(localX, localY, localZ);
                                        currentBiome = worldObj.provider.getBiomeGenForCoords(realX, realZ);
                                        
                                        //use isDigable to skip leaves/wood getting counted as surface
                                        if (canReplaceBlock(currentBlock, Blocks.air) || currentBlock == currentBiome.topBlock || currentBlock == currentBiome.fillerBlock) {
                                            depth++;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (subx == 0 && subz == 0) {
                                    //already hit surface, simply increment depth counter
                                    depth++;
                                }
                                
                                float adjustedNoiseCutoff = getAdjustedNoiseCutoff(depth, localY);
                                
                                if (noiseVal > adjustedNoiseCutoff) {
                                    Block aboveBlock = chunkPrimerIn.getBlockState(localX, localY + 1, localZ);
                                    if (!isFluidBlock(aboveBlock) || localY <= lavaDepth) {
                                        //if we are in the easeInDepth range or near sea level or subH2O is installed, do some extra checks for water before digging
                                        if ((depth < easeInDepth || localY > (seaLevel - 8)) && localY > lavaDepth) {
                                            if (localX < 15)
                                                if (isFluidBlock(chunkPrimerIn.getBlockState(localX + 1, localY, localZ)))
                                                    continue;
                                            if (localX > 0)
                                                if (isFluidBlock(chunkPrimerIn.getBlockState(localX - 1, localY, localZ)))
                                                    continue;
                                            if (localZ < 15)
                                                if (isFluidBlock(chunkPrimerIn.getBlockState(localX, localY, localZ + 1)))
                                                    continue;
                                            if (localZ > 0)
                                                if (isFluidBlock(chunkPrimerIn.getBlockState(localX, localY, localZ - 1)))
                                                    continue;
                                        }
                                        Block currentBlock = chunkPrimerIn.getBlockState(localX, localY, localZ);
                                        if (currentBiome == null)
                                            currentBiome = worldObj.provider.getBiomeGenForCoords(realX, realZ);
                                        
                                        boolean foundTopBlock = currentBlock == currentBiome.topBlock;
                                        digBlock(chunkPrimerIn, localX, localY, localZ, foundTopBlock, currentBlock, aboveBlock, currentBiome);
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
    
    private float getAdjustedNoiseCutoff(int depth, int localY) {
        float adjustedNoiseCutoff = noiseCutoff;// + cutoffAdjuster;
        if (depth < easeInDepth) {
            //higher threshold at surface, normal threshold below easeInDepth
            adjustedNoiseCutoff = 1;//(float) clampedLerp(noiseCutoff, surfaceCutoff, (easeInDepth - (float) depth) / easeInDepth);
            
        }
        
        //increase cutoff as we get closer to the minCaveHeight so it's not all flat floors
        if (localY < (minCaveHeight + 5)) {
            adjustedNoiseCutoff += (float) (((minCaveHeight + 5) - localY) * 0.05);
        }
        return adjustedNoiseCutoff;
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
                        float dispAmp = (float) (warpAmplifier * ((originalMaxHeight - y) / (originalMaxHeight * 0.85)));
                        
                        float xDisp;
                        float yDisp;
                        float zDisp;
                        
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
            if (canReplaceBlock(chunkPrimer.getBlockState(localX, searchMid, localZ), null)) {
                top = recursiveBinarySurfaceSearch(chunkPrimer, localX, localZ, searchTop, searchMid + 1);
            } else {
                top = recursiveBinarySurfaceSearch(chunkPrimer, localX, localZ, searchMid, searchBottom);
            }
        }
        return top;
    }
    static final int[] cords = {2, 6, 3, 11, 7, 2, 9, 13, 12, 4, 13, 9};
    private int getMaxSurfaceHeight(ChunkPrimer primer) {
        int y = 0;
        for (int i = 0; i < cords.length; i += 2) {
            int test = recursiveBinarySurfaceSearch(primer, cords[i], cords[i + 1], 255, 0);
            if (test > y) {
                y = test;
                if (y > maxCaveHeight)
                    return y;
            }
        }
        return y;
    }
    
    public boolean canReplaceBlock(Block block, Block blockUp) {
        if (block == null || (blockUp != null && blockUp.getMaterial() == Material.water)) return false;
        return (WorleyCavesConfig.allowReplaceMoreBlocks && block.getMaterial() == Material.rock) || block == Blocks.stone || block == Blocks.dirt || block == Blocks.grass || block == Blocks.hardened_clay || block == Blocks.stained_hardened_clay || block == Blocks.sandstone || block == Blocks.mycelium || block == Blocks.snow_layer || block == Blocks.snow || block == Blocks.sand || block == Blocks.gravel;
    }
    
    private boolean isFluidBlock(Block block) {
        return block instanceof BlockLiquid || block instanceof IFluidBlock;
    }
    
    protected void digBlock(ChunkPrimer data, int x, int y, int z, boolean foundTop, Block block, Block up, BiomeGenBase biome) {
        Block top = biome.topBlock;
        Block filler = biome.fillerBlock;
        if (!isFluidBlock(up) && (this.canReplaceBlock(block, up) || block == top || block == filler)) {
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
    
    
    //GREG
    private void generateNoiseCaves(int chunkX, int chunkZ, Block[] blocks) {
        generateNoiseCavesNoise(chunkX, chunkZ);
        
        for (int noiseX = 0; noiseX < 4; ++noiseX) {
            int ix0 = noiseX * 5;
            int ix1 = (noiseX + 1) * 5;
            
            for (int noiseZ = 0; noiseZ < 4; ++noiseZ) {
                int ix0z0 = (ix0 + noiseZ) * 33;
                int ix0z1 = (ix0 + noiseZ + 1) * 33;
                int ix1z0 = (ix1 + noiseZ) * 33;
                int ix1z1 = (ix1 + noiseZ + 1) * 33;
                
                for (int noiseY = 0; noiseY < 32; ++noiseY) {
                    double x0z0 = this.caveNoise[ix0z0 + noiseY];
                    double x0z1 = this.caveNoise[ix0z1 + noiseY];
                    double x1z0 = this.caveNoise[ix1z0 + noiseY];
                    double x1z1 = this.caveNoise[ix1z1 + noiseY];
                    double x0z0Add = (this.caveNoise[ix0z0 + noiseY + 1] - x0z0) * 0.125D;
                    double x0z1Add = (this.caveNoise[ix0z1 + noiseY + 1] - x0z1) * 0.125D;
                    double x1z0Add = (this.caveNoise[ix1z0 + noiseY + 1] - x1z0) * 0.125D;
                    double x1z1Add = (this.caveNoise[ix1z1 + noiseY + 1] - x1z1) * 0.125D;
                    
                    for (int pieceY = 0; pieceY < 8; ++pieceY) {
                        double z0 = x0z0;
                        double z1 = x0z1;
                        double z0Add = (x1z0 - x0z0) * 0.25D;
                        double z1Add = (x1z1 - x0z1) * 0.25D;
                        
                        for (int pieceX = 0; pieceX < 4; ++pieceX) {
                            int index = pieceX + noiseX * 4 << 12 | noiseZ * 4 << 8 | noiseY * 8 + pieceY;
                            short idAdd = 256;
                            index -= idAdd;
                            double densityAdd = (z1 - z0) * 0.25D;
                            double density = z0 - densityAdd;
                            
                            for (int pieceZ = 0; pieceZ < 4; ++pieceZ) {
                                index += idAdd;
                                if ((density += densityAdd) < 0) {
                                    if (GregCavesConfig.smoothBedrock) {
                                        if (blocks[index] == Blocks.stone) {
                                            int y = noiseY * 8 + pieceY;
                                            if (y > 5) {
                                                if (y < GregCavesConfig.caveLavaLevel) {
                                                    blocks[index] = Blocks.flowing_lava;
                                                } else {
                                                    blocks[index] = null;
                                                }
                                            }
                                        }
                                    } else {
                                        int y = noiseY * 8 + pieceY;
                                        if (y > 0) {
                                            if (blocks[index] == Blocks.bedrock) {
                                                blocks[index] = Blocks.stone;
                                            } else {
                                                blocks[index] = null;
                                            }
                                        }
                                    }
                                }
                            }
                            
                            z0 += z0Add;
                            z1 += z1Add;
                        }
                        
                        x0z0 += x0z0Add;
                        x0z1 += x0z1Add;
                        x1z0 += x1z0Add;
                        x1z1 += x1z1Add;
                    }
                }
            }
        }
    }
    
    private void generateNoiseCavesNoise(int chunkX, int chunkZ) {
        int cx = chunkX * 4, cz = chunkZ * 4;
        this.depthNoises = this.noiseGen6.generateNoiseOctaves(this.depthNoises, cx, cz, 5, 5, 200.0D, 200.0D, 0.5D);
        this.interpolationNoises = this.interpolationNoise.generateNoiseOctaves(this.interpolationNoises, cx, 0, cz, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.lowerInterpolatedNoises = this.field_147431_j.generateNoiseOctaves(this.lowerInterpolatedNoises, cx, 0, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.upperInterpolatedNoises = this.field_147432_k.generateNoiseOctaves(this.upperInterpolatedNoises, cx, 0, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        BiomeGenBase[] biomes = null;
        biomes = this.worldObj.getWorldChunkManager().getBiomesForGeneration(biomes, cx - 2, cz - 2, 10, 10);
        int i = 0, j = 0;
        
        for (int x = 0; x < 5; ++x) {
            for (int z = 0; z < 5; ++z) {
                float scale = 0.0F;
                float depth = 0.0F;
                float weight = 0.0F;
                double lowestScaledDepth = 0;
                
                BiomeGenBase biome0 = biomes[x + 2 + (z + 2) * 10];
                // We iterate the entire area to ensure we're not anywhere even near an ocean
                for (int x1 = -2; x1 <= 2; ++x1) {
                    for (int z1 = -2; z1 <= 2; ++z1) {
                        BiomeGenBase biome = biomes[x + x1 + 2 + (z + z1 + 2) * 10];
                        float depthHere = biome.rootHeight;
                        float scaleHere = biome.heightVariation;
                        
                        float weightHere = biomeWeightTable[x1 + 2 + (z1 + 2) * 5] / (depthHere + 2.0F);
                        
                        if (biome.rootHeight > biome0.rootHeight) {
                            weightHere /= 2.0F;
                        }
                        
                        scale += scaleHere * weightHere;
                        depth += depthHere * weightHere;
                        weight += weightHere;
                        // Disable in oceans
                        lowestScaledDepth = Math.min(lowestScaledDepth, biome.rootHeight);
                    }
                }
                scale /= weight;
                depth /= weight;
                scale = scale * 0.9F + 0.1F;
                depth = (depth * 4.0F - 1.0F) / 8.0F;
                double depthNoise = getDepthNoise(j);
                
                ++j;
                double scaledDepth = depth;
                double scaledScale = scale;
                scaledDepth += depthNoise * 0.2D;
                scaledDepth = scaledDepth * 8.5D / 8.0D;
                double terrainHeight = 8.5D + scaledDepth * 4.0D;
                
                // Each unit of depth roughly corresponds to 16 blocks, but we use 20 for good measure
                // We start reduction at 56 instead of 64, the sea level, to give ourselves some more room.
                double startLevel = 56 + (lowestScaledDepth * 20);
                int sub = (int) (startLevel / 8);
                
                for (int y = 0; y < 33; y++) {
                    double falloff = ((double) y - terrainHeight) * 12.0D * 128.0D / 256.0D / scaledScale;
                    
                    if (falloff < 0.0D) {
                        falloff *= 4.0D;
                    }
                    
                    double noise = getNoise(i, falloff, y);
                    
                    double caveNoise = this.noiseCaves.sample(noise, y * 8, chunkZ * 16 + (z * 4), chunkX * 16 + (x * 4));
                    
                    // Reduce so we don't break the surface
                    caveNoise = mods.tesseract.ucm.util.MathHelper.clampedLerp(caveNoise, (lowestScaledDepth * -30) + 20, (y - sub + 2) / 2.0);
                    
                    this.caveNoise[i] = caveNoise;
                    i++;
                }
            }
        }
    }
    
    private double getDepthNoise(int j) {
        double depthNoise = this.depthNoises[j] / 8000;
        
        if (depthNoise < 0.0D) {
            depthNoise = -depthNoise * 0.3D;
        }
        
        depthNoise = depthNoise * 3.0D - 2.0D;
        
        if (depthNoise < 0.0D) {
            depthNoise /= 2.0D;
            
            if (depthNoise < -1.0D) {
                depthNoise = -1.0D;
            }
            
            depthNoise /= 1.4D;
            depthNoise /= 2.0D;
        } else {
            if (depthNoise > 1.0D) {
                depthNoise = 1.0D;
            }
            
            depthNoise /= 8.0D;
        }
        return depthNoise;
    }
    
    private double getNoise(int i, double falloff, int y) {
        double lowerNoise = this.lowerInterpolatedNoises[i] / 512.0D;
        double upperNoise = this.upperInterpolatedNoises[i] / 512.0D;
        double interpolation = (this.interpolationNoises[i] / 10.0D + 1.0D) / 2.0D;
        double noise = MathHelper.denormalizeClamp(lowerNoise, upperNoise, interpolation) - falloff;
        
        // Scale down the last 3 layers
        if (y > 29) {
            double lerp = (float) (y - 29) / 3.0F;
            noise = noise * (1.0D - lerp) + -10.0D * lerp;
        }
        return noise;
    }
    
    @Override
    protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        Utils.digBlock(worldObj, data, index, x, y, z, chunkX, chunkZ, foundTop);
    }
    
}
