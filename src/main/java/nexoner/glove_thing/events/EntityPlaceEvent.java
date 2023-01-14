package nexoner.glove_thing.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nexoner.glove_thing.ModClass;
import nexoner.glove_thing.item.ModItems;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityPlaceEvent {

    @SubscribeEvent
    public static void entityPlaceEvent(BlockEvent.EntityPlaceEvent event){

        if (event.getEntity() instanceof Player &&
                ((Player) event.getEntity()).getMainHandItem().getItem() == ModItems.GLOVE.get()
                && ((Player) event.getEntity()).getInventory().offhand.get(0).getItem() instanceof BlockItem){
                event.setCanceled(true);
        }

    }
}
