package jaymanh.optools.Enchantments;

import static jaymanh.optools.OpTools.MOD_ID;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> AUTO_REPAIR = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "auto_repair"));
    public static final ResourceKey<Enchantment> AUTO_REPLANT = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "auto_replant"));
    public static final ResourceKey<Enchantment> ELEMENTAL_EARTH = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "elemental_earth"));
    public static final ResourceKey<Enchantment> ELEMENTAL_FIRE = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "elemental_fire"));
    public static final ResourceKey<Enchantment> ELEMENTAL_LIGHTNING = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "elemental_lightning"));
    public static final ResourceKey<Enchantment> ELEMENTAL_WATER = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "elemental_water"));
    public static final ResourceKey<Enchantment> ELEMENTAL_ICE = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "elemental_ice"));
    public static final ResourceKey<Enchantment> TREE_BREAKER = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(MOD_ID, "tree_breaker"));

    public static void initialise(){

    }

    public static int itemLevel(ServerLevel world, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        if (stack.isEmpty()) {
            return 0;
        }
        Optional<Holder.Reference<Enchantment>> holder = holder(world, enchantment);
        if (holder.isEmpty()) {
            return 0;
        }
        return EnchantmentHelper.getItemEnchantmentLevel(holder.get(), stack);
    }

    public static int entityLevel(ServerLevel world, LivingEntity entity, ResourceKey<Enchantment> enchantment) {
        Optional<Holder.Reference<Enchantment>> holder = holder(world, enchantment);
        if (holder.isEmpty()) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(holder.get(), entity);
    }

    private static Optional<Holder.Reference<Enchantment>> holder(ServerLevel world, ResourceKey<Enchantment> enchantment) {
        Registry<Enchantment> registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return registry.get(enchantment.identifier());
    }
}
