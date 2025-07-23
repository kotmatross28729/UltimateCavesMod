package mods.tesseract.ucm.mixins.early;

import mods.tesseract.ucm.config.MainConfig;
import net.minecraft.world.gen.MapGenRavine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MapGenRavine.class, priority = 456)
public class MixinMapGenRavine {

    @ModifyConstant(method = "digBlock([Lnet/minecraft/block/Block;IIIIIIZ)V", constant = @Constant(intValue = 10), remap = false)
    public int setCaveLavaLevel(int caveLavaLevel) {
        return MainConfig.caveLavaLevel + 1; //Because vanilla uses "<" and not "<="
    }
}
