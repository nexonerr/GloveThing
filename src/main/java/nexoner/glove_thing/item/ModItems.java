package nexoner.glove_thing.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nexoner.glove_thing.ModClass;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ModClass.MOD_ID);


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
