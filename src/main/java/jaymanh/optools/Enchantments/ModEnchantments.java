package jaymanh.optools.Enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

import static jaymanh.optools.OpTools.MOD_ID;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> AUTO_REPAIR = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "auto_repair"));
    public static final RegistryKey<Enchantment> AUTO_REPLANT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "auto_replant"));
    public static final RegistryKey<Enchantment> ELEMENTAL_EARTH = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "elemental_earth"));
    public static final RegistryKey<Enchantment> ELEMENTAL_FIRE = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "elemental_fire"));
    public static final RegistryKey<Enchantment> ELEMENTAL_LIGHTNING = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "elemental_lightning"));
    public static final RegistryKey<Enchantment> ELEMENTAL_WATER = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "elemental_water"));
    public static final RegistryKey<Enchantment> ELEMENTAL_ICE = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "elemental_ice"));
    public static final RegistryKey<Enchantment> TREE_BREAKER = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "tree_breaker"));

    public static void initialise(){

    }

    public static int itemLevel(ServerWorld world, ItemStack stack, RegistryKey<Enchantment> enchantment) {
        if (stack.isEmpty()) {
            return 0;
        }
        Optional<RegistryEntry.Reference<Enchantment>> holder = holder(world, enchantment);
        if (holder.isEmpty()) {
            return 0;
        }
        return EnchantmentHelper.getLevel(holder.get(), stack);
    }

    public static int entityLevel(ServerWorld world, LivingEntity entity, RegistryKey<Enchantment> enchantment) {
        Optional<RegistryEntry.Reference<Enchantment>> holder = holder(world, enchantment);
        if (holder.isEmpty()) {
            return 0;
        }
        return EnchantmentHelper.getEquipmentLevel(holder.get(), entity);
    }

    public static boolean mayAlter(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        if (player.isBlockBreakingRestricted(world, pos, player.interactionManager.getGameMode())) {
            return false;
        }
        return world.canPlayerModifyAt(player, pos);
    }

    private static Optional<RegistryEntry.Reference<Enchantment>> holder(ServerWorld world, RegistryKey<Enchantment> enchantment) {
        Registry<Enchantment> registry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        return registry.getEntry(enchantment.getValue());
    }
}
