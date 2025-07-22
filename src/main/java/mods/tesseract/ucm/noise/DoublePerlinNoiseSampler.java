package mods.tesseract.ucm.noise;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class DoublePerlinNoiseSampler {
    private static final double DOMAIN_SCALE = 1.0181268882175227D;
    private final double amplitude;
    private final OctavePerlinNoiseSampler firstSampler;
    private final OctavePerlinNoiseSampler secondSampler;

    public static DoublePerlinNoiseSampler create(Random random, int offset, double... octaves) {
        List<Double> list = new ArrayList<>();
        for (double octave : octaves) {
            list.add(octave);
        }

        return new DoublePerlinNoiseSampler(random, offset, list);
    }
    
    private DoublePerlinNoiseSampler(Random random, int offset, List<Double> octaves) {
        this.firstSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
        this.secondSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
        int i = 2147483647;
        int j = -2147483648;
        ListIterator<Double> doubleListIterator = octaves.listIterator();

        while(doubleListIterator.hasNext()) {
            int k = doubleListIterator.nextIndex();
            double d = doubleListIterator.next();
            if (d != 0.0D) {
                i = Math.min(i, k);
                j = Math.max(j, k);
            }
        }

        this.amplitude = 0.16666666666666666D / createAmplitude(j - i);
    }

    private static double createAmplitude(int octaves) {
        return 0.1D * (1.0D + 1.0D / (double)(octaves + 1));
    }

    public double sample(double x, double y, double z) {
        double d = x * DOMAIN_SCALE;
        double e = y * DOMAIN_SCALE;
        double f = z * DOMAIN_SCALE;
        return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
    }
}
