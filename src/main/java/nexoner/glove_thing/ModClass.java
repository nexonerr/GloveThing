package nexoner.glove_thing;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nexoner.glove_thing.item.ModItems;
import nexoner.glove_thing.screen.GloveMenu;
import nexoner.glove_thing.screen.GloveScreen;
import nexoner.glove_thing.screen.ModMenuTypes;
import org.slf4j.Logger;

@Mod(ModClass.MOD_ID)
public class ModClass {

    public static final String MOD_ID = "glove_thing";

    private static final Logger LOGGER = LogUtils.getLogger();

    public ModClass() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        ModItems.register(eventBus);
        ModMenuTypes.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event){
        MenuScreens.register(ModMenuTypes.GLOVE_MENU.get(), GloveScreen::new);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }
}
