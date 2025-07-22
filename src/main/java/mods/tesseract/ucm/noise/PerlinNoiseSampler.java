package mods.tesseract.ucm.noise;

import mods.tesseract.ucm.util.MathHelper;

import java.util.Random;

public final class PerlinNoiseSampler {
    private final byte[] permutations;
    public final double originX;
    public final double originY;
    public final double originZ;

    public PerlinNoiseSampler(Random random) {
        this.originX = random.nextDouble() * 256.0D;
        this.originY = random.nextDouble() * 256.0D;
        this.originZ = random.nextDouble() * 256.0D;
        this.permutations = new byte[256];

        int j;
        for(j = 0; j < 256; ++j) {
            this.permutations[j] = (byte)j;
        }

        for(j = 0; j < 256; ++j) {
            int k = random.nextInt(256 - j);
            byte b = this.permutations[j];
            this.permutations[j] = this.permutations[j + k];
            this.permutations[j + k] = b;
        }

    }
    
    public double sample(double x, double y, double z, double yScale, double yMax) {
        double d = x + this.originX;
        double e = y + this.originY;
        double f = z + this.originZ;
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        int k = MathHelper.floor(f);
        double g = d - (double)i;
        double h = e - (double)j;
        double l = f - (double)k;
        double p;
        if (yScale != 0.0D) {
            double n;
            if (yMax >= 0.0D && yMax < h) {
                n = yMax;
            } else {
                n = h;
            }

            p = (double)MathHelper.floor(n / yScale + 1.0000000116860974E-7D) * yScale;
        } else {
            p = 0.0D;
        }

        return this.sample(i, j, k, g, h - p, l, h);
    }
    
    private static double grad(int hash, double x, double y, double z) {
        return SimplexNoiseSampler.dot(SimplexNoiseSampler.GRADIENTS[hash & 15], x, y, z);
    }

    private int getGradient(int hash) {
        return this.permutations[hash & 255] & 255;
    }

    private double sample(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalX) {
        int i = this.getGradient(sectionX);
        int j = this.getGradient(sectionX + 1);
        int k = this.getGradient(i + sectionY);
        int l = this.getGradient(i + sectionY + 1);
        int m = this.getGradient(j + sectionY);
        int n = this.getGradient(j + sectionY + 1);
        double d = grad(this.getGradient(k + sectionZ), localX, localY, localZ);
        double e = grad(this.getGradient(m + sectionZ), localX - 1.0D, localY, localZ);
        double f = grad(this.getGradient(l + sectionZ), localX, localY - 1.0D, localZ);
        double g = grad(this.getGradient(n + sectionZ), localX - 1.0D, localY - 1.0D, localZ);
        double h = grad(this.getGradient(k + sectionZ + 1), localX, localY, localZ - 1.0D);
        double o = grad(this.getGradient(m + sectionZ + 1), localX - 1.0D, localY, localZ - 1.0D);
        double p = grad(this.getGradient(l + sectionZ + 1), localX, localY - 1.0D, localZ - 1.0D);
        double q = grad(this.getGradient(n + sectionZ + 1), localX - 1.0D, localY - 1.0D, localZ - 1.0D);
        double r = MathHelper.perlinFade(localX);
        double s = MathHelper.perlinFade(fadeLocalX);
        double t = MathHelper.perlinFade(localZ);
        return MathHelper.lerp3(r, s, t, d, e, f, g, h, o, p, q);
    }
}
