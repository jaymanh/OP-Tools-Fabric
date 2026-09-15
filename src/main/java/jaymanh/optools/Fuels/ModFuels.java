package jaymanh.optools.Fuels;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import static jaymanh.optools.Tools.ModTools.*;

public class ModFuels {

    public static final Item SUPER_FUEL = register(
            new Item(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .setId(key("super_fuel"))
                    .component(DataComponents.COOKING_FUEL, new CookingFuel(
                            new ResolvableInt.Constant(20 * 640),
                            ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)
                    ))),
            "super_fuel"
    );

    public static void initialize(){

        CreativeModeTabEvents.modifyOutputEvent(OP_TOOLS_ITEM_GROUP_KEY).register(event -> {
            event.accept(SUPER_FUEL);
        });
    }
}