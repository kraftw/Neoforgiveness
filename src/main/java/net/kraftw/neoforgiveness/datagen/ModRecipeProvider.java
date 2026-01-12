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

        // Standard block conversions
        processBlockConversions(recipeOutput, "_stairs", 2, 3);
        processBlockConversions(recipeOutput, "_slab", 2, 1);
        processBlockConversions(recipeOutput, "_wall", 1, 1);
        processBlockConversions(recipeOutput, "_trapdoor", 1, 3);
        processBlockConversions(recipeOutput, "_door", 1, 3);
        processBlockConversions(recipeOutput, "_fence", 1, 3);
        processBlockConversions(recipeOutput, "_fence_gate", 1, 2);
        processBlockConversions(recipeOutput, "_button", 1, 1);
        processBlockConversions(recipeOutput, "_pressure_plate", 1, 2);

        // Special conversions
        processPanes(recipeOutput);
        processCarpets(recipeOutput);

        // Mod-specific conversions
        processModSpecificBlocks(recipeOutput);

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
                Block baseBlock = tryFindBaseBlock(blockLocation.getNamespace(), baseName);

                if (baseBlock != null && baseBlock.asItem() != Items.AIR) {
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

                    if (baseBlock.asItem() != Items.AIR) {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock, 3)
                                .requires(paneItem, 8)
                                .unlockedBy("has_pane", has(paneItem))
                                .save(output, paneLocation.getNamespace() + "/pane_conversion/" + paneName);
                    }
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

                    if (baseBlock.asItem() != Items.AIR) {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock, 2)
                                .requires(carpetItem, 3)
                                .unlockedBy("has_carpet", has(carpetItem))
                                .save(output, carpetLocation.getNamespace() + "/carpet_conversion/" + carpetName);
                    }
                }
            }
        }
        System.out.println("  Total carpets processed: " + count);
    }

    private void processModSpecificBlocks(RecipeOutput output) {
        System.out.println("Processing mod-specific block conversions...");

        // Create Deco - tiles and bricks
        processModPattern(output, "createdeco", "_tiles", "_bricks", 1, 1);
        processModPattern(output, "createdeco", "_tiles_stairs", "_bricks", 2, 3);
        processModPattern(output, "createdeco", "_tiles_slab", "_bricks", 2, 1);

        // Supplementaries - timber frames and such
        processModPattern(output, "supplementaries", "_timber_frame", "_planks", 1, 1);
        processModPattern(output, "supplementaries", "_timber_cross_frame", "_planks", 1, 1);

        // Clayworks - bricks and tiles
        processModPattern(output, "clayworks", "_bricks", "_block", 1, 1);
        processModPattern(output, "clayworks", "_tiles", "_bricks", 1, 1);

        // Every Comp - handles tons of variants
        processModPattern(output, "everycomp", "_vertical_slab", "", 1, 1);
        processModPattern(output, "everycomp", "_vertical_stairs", "", 2, 3);

        // Macaw's Paths - path variants
        processModPattern(output, "mcwpaths", "_path", "", 1, 1);

        // Better End - end stone variants
        processModPattern(output, "betterend", "_tiles", "_bricks", 1, 1);
        processModPattern(output, "betterend", "_pillar", "", 1, 1);

        // No Man's Land - various decorative blocks
        processModPattern(output, "nomansland", "_tiles", "_bricks", 1, 1);
        processModPattern(output, "nomansland", "_bricks", "_block", 1, 1);

        // DN Decor - decorative variants
        processModPattern(output, "dndecor", "_tiles", "_block", 1, 1);
        processModPattern(output, "dndecor", "_panel", "_planks", 1, 1);

        System.out.println("Finished mod-specific conversions");
    }

    private void processModPattern(RecipeOutput output, String modId, String suffix, String baseSuffix, int inputCount, int outputCount) {
        if (!isModLoaded(modId)) return;

        int count = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == Blocks.AIR) continue;

            Item blockItem = block.asItem();
            if (blockItem == Items.AIR) continue;

            ResourceLocation blockLocation = BuiltInRegistries.BLOCK.getKey(block);

            if (!blockLocation.getNamespace().equals(modId)) continue;

            String blockName = blockLocation.getPath();

            if (blockName.endsWith(suffix)) {
                count++;
                String baseName = blockName.substring(0, blockName.length() - suffix.length()) + baseSuffix;
                ResourceLocation baseLocation = ResourceLocation.fromNamespaceAndPath(modId, baseName);

                if (BuiltInRegistries.BLOCK.containsKey(baseLocation)) {
                    Block baseBlock = BuiltInRegistries.BLOCK.get(baseLocation);

                    if (baseBlock.asItem() != Items.AIR) {
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock, outputCount)
                                .requires(blockItem, inputCount)
                                .unlockedBy("has_" + suffix.substring(1), has(blockItem))
                                .save(output, modId + "/neoforgiveness_" + suffix.substring(1) + "_conversion/" + blockName);
                    }
                }
            }
        }
        if (count > 0) {
            System.out.println("  " + modId + " - " + suffix + ": " + count + " recipes");
        }
    }

    private Block tryFindBaseBlock(String namespace, String baseName) {
        // Try common suffixes in order of preference
        String[] suffixes = {
                "",              // Exact match
                "_planks",       // Wood variants
                "_block",        // Generic blocks
                "_bricks",       // Brick variants
                "_tiles",        // Tile variants
                "_ingot",        // Metal variants
                "s",             // Plural (like stone -> stones)
                "_stone",        // Stone variants
                "_wood",         // Wood variants
                "_log",          // Log variants
        };

        for (String suffix : suffixes) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, baseName + suffix);
            if (BuiltInRegistries.BLOCK.containsKey(location)) {
                Block block = BuiltInRegistries.BLOCK.get(location);
                if (block.asItem() != Items.AIR) {
                    return block;
                }
            }
        }
        return null;
    }

    private boolean isModLoaded(String modId) {
        return BuiltInRegistries.BLOCK.keySet().stream()
                .anyMatch(loc -> loc.getNamespace().equals(modId));
    }
}