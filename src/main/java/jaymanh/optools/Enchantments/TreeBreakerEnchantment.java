package jaymanh.optools.Enchantments;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class TreeBreakerEnchantment {
    /** Hard cap on the whole connected break, not on a single neighbor wave. */
    private static final int MAX_BLOCKS = 64 * 9;
    private static final int DESTROY_RECURSION_LIMIT = 512;

    public static void initialise() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (!state.is(BlockTags.LOGS)) {
                return;
            }
            ItemStack tool = serverPlayer.getMainHandItem();
            if (ModEnchantments.itemLevel(serverLevel, tool, ModEnchantments.TREE_BREAKER) <= 0) {
                return;
            }
            breakConnectedLogs(serverLevel, serverPlayer, pos, state.getBlock(), tool);
        });
    }

    private static void breakConnectedLogs(ServerLevel world, ServerPlayer player, BlockPos origin, Block woodType, ItemStack tool) {
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
            if (!world.destroyBlock(current, true, player, DESTROY_RECURSION_LIMIT)) {
                continue;
            }
            broken++;
            tool.hurtAndBreak(1, world, player, stack -> {});
            if (tool.isEmpty() || ModEnchantments.itemLevel(world, tool, ModEnchantments.TREE_BREAKER) <= 0) {
                return;
            }
            enqueueLogs(current, world, woodType, visited, queue);
        }
    }

    private static void enqueueLogs(BlockPos origin, ServerLevel world, Block woodType, Set<BlockPos> visited, ArrayDeque<BlockPos> queue) {
        for (Direction direction : Direction.values()) {
            offerLog(origin.relative(direction), world, woodType, visited, queue);
        }
        for (Direction first : Direction.values()) {
            for (Direction second : Direction.values()) {
                if (first != second && first.getAxis() != second.getAxis()) {
                    offerLog(origin.relative(first).relative(second), world, woodType, visited, queue);
                }
            }
        }
    }

    private static void offerLog(BlockPos pos, ServerLevel world, Block woodType, Set<BlockPos> visited, ArrayDeque<BlockPos> queue) {
        if (world.getBlockState(pos).getBlock() == woodType && visited.add(pos)) {
            queue.add(pos);
        }
    }

    private static boolean mayAlter(ServerLevel world, ServerPlayer player, BlockPos pos) {
        if (player.blockActionRestricted(world, pos, player.gameMode())) {
            return false;
        }
        return world.mayInteract(player, pos);
    }
}
