package aartcraft.togglebabymobs.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import static aartcraft.togglebabymobs.items.ModItems.AGE_POTION;

public class ToggleBabyMobsRecipeProvider extends FabricRecipeProvider {
    public ToggleBabyMobsRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                createShaped(RecipeCategory.FOOD, AGE_POTION, 4)
                        .pattern(" S ")
                        .pattern("RGC")
                        .pattern(" E ")
                        .input('S', Items.SUGAR)
                        .input('R', Items.REDSTONE)
                        .input('G', Items.GHAST_TEAR)
                        .input('C', Items.CARROT)
                        .input('E', Items.EMERALD)
                        .group("multi_bench") // Put it in a group called "multi_bench" - groups are shown in one slot in the recipe book
                        .criterion(hasItem(Items.GHAST_TEAR),
                                conditionsFromItem(Items.GHAST_TEAR))
                        .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "ToggleBabyMobsRecipeProvider";
    }
}