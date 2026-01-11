package net.kraftw.neoforgiveness.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        System.out.println("========================================");
        System.out.println("NEOFORGIVENESS: Building recipes!");
        System.out.println("========================================");

        processBlockConversions(recipeOutput, "_stairs", 2, 3);
        processBlockConversions(recipeOutput, "_slab", 2, 1);
        processBlockConversions(recipeOutput, "_wall", 1, 1);
        processBlockConversions(recipeOutput, "_trapdoor", 1, 3);

        processPanes(recipeOutput);
        processCarpets(recipeOutput);

        System.out.println("========================================");
        System.out.println("NEOFORGIVENESS: Finished building recipes!");
        System.out.println("========================================");
    }

    private void processBlockConversions(RecipeOutput output, String suffix, int inputCount, int outputCount) {
        System.out.println("Processing blocks with suffix: " + suffix);

        int count = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == Blocks.AIR) continue;

            Item blockItem = block.asItem();
            if (blockItem == Items.AIR) continue;

            ResourceLocation blockLocation = BuiltInRegistries.BLOCK.getKey(block);
            String blockName = blockLocation.getPath();

            if (blockName.endsWith(suffix)) {
                count++;

                String baseName = blockName.substring(0, blockName.length() - suffix.length());
                Block baseBlock = tryFindBaseBlock(blockLocation.getNamespace(), baseName, "", "_planks", "_ingot", "s");

                if (baseBlock != null) {
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock, outputCount)
                            .requires(blockItem, inputCount)
                            .unlockedBy("has_" + suffix.substring(1), has(blockItem))
                            .save(output, blockLocation.getNamespace() + "/" + suffix.substring(1) + "_conversion/" + blockName);
                }
            }
        }
        System.out.println("  Total blocks processed: " + count);
    }

    private void processPanes(RecipeOutput output) {
        System.out.println("Processing panes...");

        int count = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == Blocks.AIR) continue;

            Item paneItem = block.asItem();
            if (paneItem == Items.AIR) continue;

            ResourceLocation paneLocation = BuiltInRegistries.BLOCK.getKey(block);
            String paneName = paneLocation.getPath();

            if (paneName.endsWith("_pane")) {
                count++;
                String baseNameGlass = paneName.substring(0, paneName.length() - 5) + "_glass";
                ResourceLocation baseLocationGlass = ResourceLocation.fromNamespaceAndPath(paneLocation.getNamespace(), baseNameGlass);

                if (BuiltInRegistries.BLOCK.containsKey(baseLocationGlass)) {
                    Block baseBlock = BuiltInRegistries.BLOCK.get(baseLocationGlass);

                    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock, 3)
                            .requires(paneItem, 8)
                            .unlockedBy("has_pane", has(paneItem))
                            .save(output, paneLocation.getNamespace() + "/pane_conversion/" + paneName);
                }
            }
        }
        System.out.println("  Total panes processed: " + count);
    }

    private void processCarpets(RecipeOutput output) {
        System.out.println("Processing carpets...");

        int count = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == Blocks.AIR) continue;

            Item carpetItem = block.asItem();
            if (carpetItem == Items.AIR) continue;

            ResourceLocation carpetLocation = BuiltInRegistries.BLOCK.getKey(block);
            String carpetName = carpetLocation.getPath();

            if (carpetName.endsWith("_carpet")) {
                count++;
                String baseNameWool = carpetName.substring(0, carpetName.length() - 7) + "_wool";
                ResourceLocation baseLocationWool = ResourceLocation.fromNamespaceAndPath(carpetLocation.getNamespace(), baseNameWool);

                if (BuiltInRegistries.BLOCK.containsKey(baseLocationWool)) {
                    Block baseBlock = BuiltInRegistries.BLOCK.get(baseLocationWool);

                    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock, 2)
                            .requires(carpetItem, 3)
                            .unlockedBy("has_carpet", has(carpetItem))
                            .save(output, carpetLocation.getNamespace() + "/carpet_conversion/" + carpetName);
                }
            }
        }
        System.out.println("  Total carpets processed: " + count);
    }

    private Block tryFindBaseBlock(String namespace, String baseName, String... suffixes) {
        for (String suffix : suffixes) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, baseName + suffix);
            if (BuiltInRegistries.BLOCK.containsKey(location)) {
                return BuiltInRegistries.BLOCK.get(location);
            }
        }
        return null;
    }
}