package jaymanh.optools.TagGenerator;

import jaymanh.optools.Blocks.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

import static jaymanh.optools.OpTools.MOD_ID;

public class OpToolsModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public OpToolsModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DIAMONDILLIUM_BLOCK.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DIAMONDIUM_BLOCK.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DEEPSLATE_DARKMATTER_ORE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.STONE_DARKMATTER_ORE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.REFINERY.toString()));


        getOrCreateRawBuilder(BlockTags.BEACON_BASE_BLOCKS)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DIAMONDILLIUM_BLOCK.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DIAMONDIUM_BLOCK.toString()));


        getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DIAMONDILLIUM_BLOCK.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DIAMONDIUM_BLOCK.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.DEEPSLATE_DARKMATTER_ORE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModBlocks.STONE_DARKMATTER_ORE.toString()));
    }
}
