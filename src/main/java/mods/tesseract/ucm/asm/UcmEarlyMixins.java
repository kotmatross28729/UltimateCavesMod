package mods.tesseract.ucm.asm;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import mods.tesseract.ucm.config.GregCavesConfig;
import mods.tesseract.ucm.config.MainConfig;
import mods.tesseract.ucm.config.WorleyCavesConfig;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
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
        
        String configFolder = "config" + File.separator + "ultimatecavesmod" + File.separator;
        
        MainConfig.loadMainConfig(new File(Launch.minecraftHome, configFolder + "UCM_MAIN.cfg"));
        GregCavesConfig.loadGregConfig(new File(Launch.minecraftHome, configFolder + "UCM_GREG.cfg"));
        WorleyCavesConfig.loadWorleyConfig(new File(Launch.minecraftHome, configFolder + "UCM_WORLEY.cfg"));
    
        List<String> mixins = new ArrayList<>();
        
        if(MainConfig.enableGregCaves) {
            
            if(GregCavesConfig.smoothBedrock)
                mixins.add("MixinBiomeGenBase");
            
            mixins.add("MixinMapGenRavine");
            
            if(GregCavesConfig.reduceOreGen)
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
