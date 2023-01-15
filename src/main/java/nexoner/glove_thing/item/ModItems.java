package nexoner.glove_thing.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nexoner.glove_thing.ModClass;
import nexoner.glove_thing.item.custom.GloveItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ModClass.MOD_ID);

    public static final RegistryObject<Item> GLOVE = ITEMS.register("glove",
            () -> new GloveItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
