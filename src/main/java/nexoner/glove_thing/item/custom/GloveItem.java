package nexoner.glove_thing.item.custom;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GloveItem extends Item {
    public GloveItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        //Block Replace Function
            if (pContext.getPlayer().getInventory().offhand.get(0).getItem() instanceof BlockItem) {
                if (pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock() != Blocks.AIR){
                    Level level = pContext.getLevel();
                    Block clickedOn = level.getBlockState(pContext.getClickedPos()).getBlock();
                    BlockState clickedOnState = level.getBlockState(pContext.getClickedPos());

                    if (clickedOnState.getDestroySpeed(level,pContext.getClickedPos()) <= 35){
                        if (clickedOn != ((BlockItem) pContext.getPlayer().getInventory().offhand.get(0).getItem()).getBlock()) {
                            level.setBlock(pContext.getClickedPos(), ((BlockItem) pContext.getPlayer().getInventory().offhand.get(0).getItem()).getBlock().defaultBlockState(), 1);
                            level.addFreshEntity(new ItemEntity(level, pContext.getClickedPos().getX(), pContext.getClickedPos().getY(), pContext.getClickedPos().getZ(), new ItemStack(clickedOn.asItem())));

                            pContext.getPlayer().getInventory().offhand.get(0).shrink(1);
                        }
                    } else {
                        pContext.getPlayer().displayClientMessage(new TranslatableComponent("glove_thing.glove_reject"),true);
                    }
                }
            }


        return InteractionResult.FAIL;
    }
}
