package mods.tesseract.ucm.asm;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import mods.tesseract.ucm.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.Name("UcmEarlyMixins")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class UcmEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.ultimatecavesmod.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<>();
        if(Main.enableGregCaves) {
            mixins.add("MixinBiomeDecorator");
            mixins.add("MixinBiomeGenBase");
            mixins.add("MixinMapGenRavine");
            mixins.add("MixinWorldGenMinable");
        }

        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
