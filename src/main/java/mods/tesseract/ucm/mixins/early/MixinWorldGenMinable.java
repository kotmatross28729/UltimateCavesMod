package mods.tesseract.ucm.mixins.early;

import mods.tesseract.ucm.config.GregCavesConfig;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = WorldGenMinable.class, priority = 456)
public class MixinWorldGenMinable {

    @Inject(method = "generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z", at = @At("TAIL"), cancellable = true)
    public boolean generate(World w, Random r, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (GregCavesConfig.reduceOreGen && y < GregCavesConfig.reduceOreGenY && w.provider.dimensionId == 0)
            cir.setReturnValue(r.nextFloat() < GregCavesConfig.reduceOreGenRate);
        return false;
    }

}
