package grygrflzr.mods.glowstonewire;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.GuiConfig;

public class GlowstoneWireConfigGUI extends GuiConfig {

    public GlowstoneWireConfigGUI(GuiScreen parent) {
        super(
                //parentScreen
                parent,
                //configElements
                new ConfigElement(GlowstoneWireMod.config.getCategory("color")).getChildElements(),
                //modID
                GlowstoneWireMod.MODID,
                //allRequireWorldRestart
                false,
                //allRequireMcRestart
                false,
                //title
                GuiConfig.getAbridgedConfigPath(GlowstoneWireMod.config.toString())
                );
    }

}
