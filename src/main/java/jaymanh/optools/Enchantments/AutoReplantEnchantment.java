package jaymanh.optools.Enchantments;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

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
            if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return;
            }
            Item seed = BLOCK_TO_SEED.get(state.getBlock());
            if (seed == null) {
                return;
            }
            ItemStack tool = serverPlayer.getMainHandStack();
            if (ModEnchantments.itemLevel(serverWorld, tool, ModEnchantments.AUTO_REPLANT) <= 0) {
                return;
            }
            replant(serverWorld, serverPlayer, pos, state, seed);
        });
    }

    private static void replant(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState broken, Item seed) {
        if (!world.getBlockState(pos).isAir()) {
            return;
        }
        if (!ModEnchantments.mayAlter(world, player, pos)) {
            return;
        }

        BlockState placed = broken.getBlock().getDefaultState();
        if (broken.contains(HorizontalFacingBlock.FACING) && placed.contains(HorizontalFacingBlock.FACING)) {
            placed = placed.with(HorizontalFacingBlock.FACING, broken.get(HorizontalFacingBlock.FACING));
        }
        if (!placed.canPlaceAt(world, pos)) {
            return;
        }

        ItemStack seedStack = new ItemStack(seed);
        if (!hasItemInInventory(player, seedStack)) {
            return;
        }
        if (!world.setBlockState(pos, placed)) {
            return;
        }
        int slot = findItemSlot(player, seedStack);
        if (slot >= 0) {
            removeItemInInventory(player, slot);
        }
    }
}
