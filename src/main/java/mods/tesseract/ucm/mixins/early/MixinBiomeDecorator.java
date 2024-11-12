package mods.tesseract.ucm.mixins.early;

import net.minecraft.world.biome.BiomeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BiomeDecorator.class, priority = 456)
public class MixinBiomeDecorator {
    @Shadow
    public boolean generateLakes;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void BiomeDecorator(CallbackInfo ci) {
        generateLakes = false;
    }
}
