package mods.tesseract.ucm.noise;

import mods.tesseract.ucm.util.MathHelper;
import mods.tesseract.ucm.util.Pair;

import java.util.List;
import java.util.Random;

public class OctavePerlinNoiseSampler {
    private final PerlinNoiseSampler[] octaveSamplers;
    private final List<Double> amplitudes;
    private final double persistence;
    private final double lacunarity;

    public static OctavePerlinNoiseSampler create(Random random, int offset, List<Double> amplitudes) {
        return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes));
    }
    
    protected OctavePerlinNoiseSampler(Random random, Pair<Integer, List<Double>> octaves) {
        int i = octaves.getFirst();
        this.amplitudes = octaves.getSecond();
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
        int j = this.amplitudes.size();
        int k = -i;
        this.octaveSamplers = new PerlinNoiseSampler[j];
        if (k >= 0 && k < j) {
            double d = this.amplitudes.get(k);
            if (d != 0.0D) {
                this.octaveSamplers[k] = perlinNoiseSampler;
            }
        }

        for(int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double e = this.amplitudes.get(l);
                if (e != 0.0D) {
                    this.octaveSamplers[l] = new PerlinNoiseSampler(random);
                }
            }
        }

        if (k < j - 1) {
            throw new IllegalArgumentException("Positive octaves are temporarily disabled");
        } else {
            this.lacunarity = Math.pow(2.0D, -k);
            this.persistence = Math.pow(2.0D, j - 1) / (Math.pow(2.0D, j) - 1.0D);
        }
    }
    
    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0D, 0.0D, false);
    }
    
    public double sample(double x, double y, double z, double yScale, double yMax, boolean useOrigin) {
        double d = 0.0D;
        double e = this.lacunarity;
        double f = this.persistence;

        for(int i = 0; i < this.octaveSamplers.length; ++i) {
            PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
            if (perlinNoiseSampler != null) {
                double g = perlinNoiseSampler.sample(maintainPrecision(x * e), useOrigin ? -perlinNoiseSampler.originY : maintainPrecision(y * e), maintainPrecision(z * e), yScale * e, yMax * e);
                d += this.amplitudes.get(i) * g * f;
            }

            e *= 2.0D;
            f /= 2.0D;
        }

        return d;
    }
    
    public static double maintainPrecision(double value) {
        return value - (double) MathHelper.lfloor(value / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }
}
