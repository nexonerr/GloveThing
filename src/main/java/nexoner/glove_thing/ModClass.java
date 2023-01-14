package nexoner.glove_thing;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nexoner.glove_thing.item.ModItems;
import org.slf4j.Logger;

@Mod(ModClass.MOD_ID)
public class ModClass {

    public static final String MOD_ID = "glove_thing";

    private static final Logger LOGGER = LogUtils.getLogger();

    public ModClass() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);

        ModItems.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }
}
