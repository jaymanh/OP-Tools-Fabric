package jaymanh.optools.Enchantments;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class AutoRepairEnchantment implements ServerTickEvents.EndWorldTick {
    @Override
    public void onEndTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            repairInventory(player, world);
        }
    }

    private static void repairInventory(ServerPlayerEntity player, ServerWorld world) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            int level = ModEnchantments.itemLevel(world, itemStack, ModEnchantments.AUTO_REPAIR);
            if (level <= 0 || !itemStack.isDamaged()) {
                continue;
            }
            int chance = Math.min(Math.max(level / 2, 1), 100);
            if (world.getRandom().nextInt(100 / chance) == 0) {
                itemStack.damage(-level, world, player, stack -> {});
            }
        }
    }

    public static void initialise() {
        ServerTickEvents.END_WORLD_TICK.register(new AutoRepairEnchantment());
    }
}
