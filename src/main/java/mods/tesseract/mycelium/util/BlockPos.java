package mods.tesseract.mycelium.util;

public class BlockPos {

    public final int x;
    public final int y;
    public final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockPos pos) {
            return pos.x == x && pos.y == y && pos.z == z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x + 31 * (y + 31 * z);
    }
}
