package jaymanh.optools.Enchantments;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

import static jaymanh.optools.Enchantments.InventoryHelper.findItemSlot;
import static jaymanh.optools.Enchantments.InventoryHelper.hasItemInInventory;
import static jaymanh.optools.Enchantments.InventoryHelper.removeItemInInventory;

public class AutoReplantEnchantment {
    private static final Map<Block, Item> BLOCK_TO_SEED = new HashMap<>();

    static {
        BLOCK_TO_SEED.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
        BLOCK_TO_SEED.put(Blocks.CARROTS, Items.CARROT);
        BLOCK_TO_SEED.put(Blocks.POTATOES, Items.POTATO);
        BLOCK_TO_SEED.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
        BLOCK_TO_SEED.put(Blocks.MELON_STEM, Items.MELON_SEEDS);
        BLOCK_TO_SEED.put(Blocks.PUMPKIN_STEM, Items.PUMPKIN_SEEDS);
        BLOCK_TO_SEED.put(Blocks.NETHER_WART, Items.NETHER_WART);
        BLOCK_TO_SEED.put(Blocks.COCOA, Items.COCOA_BEANS);
    }

    public static void initialise() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            Item seed = BLOCK_TO_SEED.get(state.getBlock());
            if (seed == null) {
                return;
            }
            ItemStack tool = serverPlayer.getMainHandItem();
            if (ModEnchantments.itemLevel(serverLevel, tool, ModEnchantments.AUTO_REPLANT) <= 0) {
                return;
            }
            replant(serverLevel, serverPlayer, pos, state, seed);
        });
    }

    private static void replant(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState broken, Item seed) {
        if (!world.getBlockState(pos).isAir()) {
            return;
        }
        if (player.blockActionRestricted(world, pos, player.gameMode()) || !world.mayInteract(player, pos)) {
            return;
        }

        BlockState placed = broken.getBlock().defaultBlockState();
        if (broken.hasProperty(HorizontalDirectionalBlock.FACING) && placed.hasProperty(HorizontalDirectionalBlock.FACING)) {
            placed = placed.setValue(HorizontalDirectionalBlock.FACING, broken.getValue(HorizontalDirectionalBlock.FACING));
        }
        if (!placed.canSurvive(world, pos)) {
            return;
        }

        ItemStack seedStack = new ItemStack(seed);
        if (!hasItemInInventory(player, seedStack)) {
            return;
        }
        if (!world.setBlockAndUpdate(pos, placed)) {
            return;
        }
        int slot = findItemSlot(player, seedStack);
        if (slot >= 0) {
            removeItemInInventory(player, slot);
        }
    }
}
