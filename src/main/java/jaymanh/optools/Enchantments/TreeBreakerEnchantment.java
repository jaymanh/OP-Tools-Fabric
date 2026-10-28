package jaymanh.optools.Enchantments;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class TreeBreakerEnchantment {
    /** Hard cap on the whole connected break, not on a single neighbor wave. */
    private static final int MAX_BLOCKS = 64 * 9;

    public static void initialise() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return;
            }
            if (!state.isIn(BlockTags.LOGS)) {
                return;
            }
            ItemStack tool = serverPlayer.getMainHandStack();
            if (ModEnchantments.itemLevel(serverWorld, tool, ModEnchantments.TREE_BREAKER) <= 0) {
                return;
            }
            breakConnectedLogs(serverWorld, serverPlayer, pos, state.getBlock(), tool);
        });
    }

    private static void breakConnectedLogs(ServerWorld world, ServerPlayerEntity player, BlockPos origin, Block woodType, ItemStack tool) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(origin);
        enqueueLogs(origin, world, woodType, visited, queue);

        int broken = 1;
        while (!queue.isEmpty() && broken < MAX_BLOCKS) {
            BlockPos current = queue.removeFirst();
            if (!mayAlter(world, player, current)) {
                continue;
            }
            BlockState state = world.getBlockState(current);
            if (state.getBlock() != woodType) {
                continue;
            }
            if (!world.breakBlock(current, true, player)) {
                continue;
            }
            broken++;
            tool.damage(1, world, player, stack -> {});
            if (tool.isEmpty() || ModEnchantments.itemLevel(world, tool, ModEnchantments.TREE_BREAKER) <= 0) {
                return;
            }
            enqueueLogs(current, world, woodType, visited, queue);
        }
    }

    private static void enqueueLogs(BlockPos origin, ServerWorld world, Block woodType, Set<BlockPos> visited, ArrayDeque<BlockPos> queue) {
        for (Direction direction : Direction.values()) {
            offerLog(origin.offset(direction), world, woodType, visited, queue);
        }
        for (Direction first : Direction.values()) {
            for (Direction second : Direction.values()) {
                if (first != second && first.getAxis() != second.getAxis()) {
                    offerLog(origin.offset(first).offset(second), world, woodType, visited, queue);
                }
            }
        }
    }

    private static void offerLog(BlockPos pos, ServerWorld world, Block woodType, Set<BlockPos> visited, ArrayDeque<BlockPos> queue) {
        if (world.getBlockState(pos).getBlock() == woodType && visited.add(pos)) {
            queue.add(pos);
        }
    }

    private static boolean mayAlter(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        if (player.isBlockBreakingRestricted(world, pos, player.interactionManager.getGameMode())) {
            return false;
        }
        return world.canPlayerModifyAt(player, pos);
    }
}
