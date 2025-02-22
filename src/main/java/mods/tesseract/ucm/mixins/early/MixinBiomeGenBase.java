package mods.tesseract.ucm.mixins.early;

import mods.tesseract.ucm.config.GregCavesConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = BiomeGenBase.class, priority = 456)
public class MixinBiomeGenBase {

    @Inject(method = "genBiomeTerrain(Lnet/minecraft/world/World;Ljava/util/Random;[Lnet/minecraft/block/Block;[BIID)V", at = @At("TAIL"))
    public final void genBiomeTerrain(World w, Random r, Block[] blocks, byte[] b, int cx, int cz, double d, CallbackInfo ci) {
         if (GregCavesConfig.smoothBedrock) {
            int i1 = cx & 15;
            int j1 = cz & 15;
            int k1 = blocks.length / 256;
            for (int l1 = 5; l1 > 0; --l1) {
                int i2 = (j1 * 16 + i1) * k1 + l1;
                if (blocks[i2] == Blocks.bedrock)
                    blocks[i2] = Blocks.stone;
            }
        }
    }
}
