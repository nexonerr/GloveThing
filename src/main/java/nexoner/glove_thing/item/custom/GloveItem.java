package nexoner.glove_thing.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import nexoner.glove_thing.caps.GloveCaps;
import nexoner.glove_thing.screen.GloveMenu;
import org.jetbrains.annotations.Nullable;

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

                        //RETURNING AN INTERACTION RESULT BREAKS THIS, FOR WHATEVER REASON
                    }
                } else {
                    pContext.getPlayer().displayClientMessage(new TranslatableComponent("glove_thing.glove_reject"),true);
                }
            }
        }

        return InteractionResult.FAIL;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && !(pPlayer.getInventory().offhand.get(0).getItem() instanceof BlockItem) && Screen.hasAltDown()) {
            pPlayer.getItemInHand(pUsedHand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> ((ItemStackHandler) handler).deserializeNBT(pPlayer.getItemInHand(pUsedHand).getOrCreateTag().getCompound("inventory")));
            NetworkHooks.openGui((ServerPlayer) pPlayer, new SimpleMenuProvider((id, inv, p) -> new GloveMenu(id, inv, pPlayer.getInventory().selected), new TranslatableComponent("glove_thing.glove_title")),
                    buf -> buf.writeVarInt(pPlayer.getInventory().selected));
            return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        }
        return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
    }


    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new GloveCaps();
    }

    //this doesn't work when a menu is open :(
    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return !(player.containerMenu instanceof GloveMenu);
    }
}
