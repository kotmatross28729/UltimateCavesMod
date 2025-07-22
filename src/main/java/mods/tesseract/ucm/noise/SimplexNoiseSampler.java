package mods.tesseract.ucm.noise;

import java.util.Random;

public class SimplexNoiseSampler {
    protected static final int[][] GRADIENTS = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
	public final double originX;
    public final double originY;
    public final double originZ;

    public SimplexNoiseSampler(Random random) {
        this.originX = random.nextDouble() * 256.0D;
        this.originY = random.nextDouble() * 256.0D;
        this.originZ = random.nextDouble() * 256.0D;

        int j;
		int[] permutations = new int[512];
        
		for(j = 0; j < 256; permutations[j] = j++) {
            
        }

        for(j = 0; j < 256; ++j) {
            int k = random.nextInt(256 - j);
            int l = permutations[j];
            permutations[j] = permutations[k + j];
            permutations[k + j] = l;
        }

    }

    protected static double dot(int[] gArr, double x, double y, double z) {
        return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
    }
}
