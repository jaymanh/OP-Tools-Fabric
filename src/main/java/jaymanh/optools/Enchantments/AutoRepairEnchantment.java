package jaymanh.optools.Enchantments;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

import static jaymanh.optools.OpTools.MOD_ID;

public class AutoRepairEnchantment implements ServerTickEvents.EndLevelTick {
    public static final ResourceKey<Enchantment> AUTO_REPAIR = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "auto_repair"));

    @Override
    public void onEndTick(ServerLevel world) {
        Registry<Enchantment> registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> holder = registry.get(AUTO_REPAIR.identifier());
        if (holder.isEmpty()) {
            return;
        }
        Holder<Enchantment> autoRepair = holder.get();
        for (ServerPlayer player : world.players()) {
            repairInventory(player, world, autoRepair);
        }
    }

    private static void repairInventory(ServerPlayer player, ServerLevel world, Holder<Enchantment> autoRepair) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            int level = EnchantmentHelper.getItemEnchantmentLevel(autoRepair, itemStack);
            if (level <= 0 || !itemStack.isDamaged()) {
                continue;
            }
            int chance = Math.min(Math.max(level / 2, 1), 100);
            if (world.getRandom().nextInt(100 / chance) == 0) {
                itemStack.hurtAndBreak(-level, world, player, stack -> {});
            }
        }
    }

    public static void initialise() {
        ServerTickEvents.END_LEVEL_TICK.register(new AutoRepairEnchantment());
    }
}
