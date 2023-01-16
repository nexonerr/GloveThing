package nexoner.glove_thing.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import nexoner.glove_thing.ModClass;
import nexoner.glove_thing.caps.GloveCaps;
import nexoner.glove_thing.screen.GloveMenu;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                        level.setBlockAndUpdate(pContext.getClickedPos(), ((BlockItem) pContext.getPlayer().getInventory().offhand.get(0).getItem()).getBlock().defaultBlockState());
                        level.updateNeighborsAt(pContext.getClickedPos(), ((BlockItem) pContext.getPlayer().getInventory().offhand.get(0).getItem()).getBlock());
                        level.addFreshEntity(new ItemEntity(level, pContext.getClickedPos().getX(), pContext.getClickedPos().getY(), pContext.getClickedPos().getZ(), new ItemStack(clickedOn.asItem())));

                        pContext.getPlayer().getInventory().offhand.get(0).shrink(1);

                        //RETURNING AN INTERACTION RESULT BREAKS THIS, FOR WHATEVER REASON
                    }
                } else {
                    pContext.getPlayer().displayClientMessage(new TranslatableComponent("glove_thing.glove_reject"),true);
                }
            }
        } else {
            ItemStack gloveItem = pContext.getItemInHand();
            gloveItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                ((ItemStackHandler) handler).deserializeNBT(gloveItem.getOrCreateTag().getCompound("inventory"));
                List<Integer> slotList = new ArrayList<>();
                for (int i = 0; i < handler.getSlots(); ++i){
                    if (handler.getStackInSlot(i).getItem() instanceof BlockItem){
                        slotList.add(i);
                    }
                }
                if (slotList.size() > 0) {
                    int toPlace = (int) (Math.random() * (slotList.size()));
                    BlockHitResult hitResult = null;
                    for (Method method : pContext.getClass().getDeclaredMethods()) {
                        if (method.getName().equals("getHitResult") || method.getName().equals("m_43718_")) {
                            method.setAccessible(true);
                            try {
                                hitResult = (BlockHitResult) method.invoke(pContext);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (hitResult != null) {
                        ItemStack blockStack = handler.getStackInSlot(slotList.get(toPlace));
                        System.out.println(blockStack.getItem());
                        UseOnContext customContext = new UseOnContext(pContext.getLevel(), pContext.getPlayer(), pContext.getHand(), blockStack, hitResult);
                        blockStack.getItem().useOn(customContext);
                        gloveItem.getOrCreateTag().put("inventory", ((ItemStackHandler) handler).serializeNBT());
                    } else {
                        ModClass.LOGGER.error("Couldn't get hit result! Unable to place block.");
                    }
                }

            });
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        pStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); ++i){
                if (handler.getStackInSlot(i).getItem() instanceof BlockItem && pEntity instanceof Player && ((Player) pEntity).getInventory().contains(handler.getStackInSlot(i))) {
                    ((ItemStackHandler) handler).deserializeNBT(pStack.getOrCreateTag().getCompound("inventory"));
                    ItemStack stack = handler.getStackInSlot(i);
                    if (((Player) pEntity).getInventory().findSlotMatchingItem(stack) >= 0) {
                        ItemStack playerStack = ((Player) pEntity).getInventory().getItem(((Player) pEntity).getInventory().findSlotMatchingItem(stack));
                        while (playerStack.getCount() > 0 && stack.getCount() < 64) {
                            playerStack.shrink(1);
                            stack.grow(1);
                            pStack.getOrCreateTag().put("inventory", ((ItemStackHandler) handler).serializeNBT());
                        }
                    }
                }
            }

        });
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
