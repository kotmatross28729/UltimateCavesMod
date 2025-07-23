package mods.tesseract.ucm.noise;

public class SimplexNoiseSampler {
    protected static final int[][] GRADIENTS = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    protected static double dot(int[] gArr, double x, double y, double z) {
        return (double)gArr[0] * x + (double)gArr[1] * y + (double)gArr[2] * z;
    }
}
