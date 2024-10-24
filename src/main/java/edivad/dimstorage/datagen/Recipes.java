package edivad.dimstorage.datagen;

import java.util.concurrent.CompletableFuture;
import edivad.dimstorage.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class Recipes extends RecipeProvider {

  public Recipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIMCORE.get())
        .pattern("aba")
        .pattern("bcb")
        .pattern("aba")
        .define('a', Items.IRON_INGOT)
        .define('b', Items.REDSTONE)
        .define('c', Items.DIAMOND)
        .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIMWALL.get(), 4)
        .pattern("aba")
        .pattern("bcb")
        .pattern("aba")
        .define('a', Items.IRON_INGOT)
        .define('b', Items.REDSTONE)
        .define('c', Items.ENDER_PEARL)
        .unlockedBy(getHasName(Items.ENDER_PEARL), has(Items.ENDER_PEARL))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.SOLIDDIMCORE.get())
        .pattern("aaa")
        .pattern("aba")
        .pattern("aaa")
        .define('a', Items.IRON_INGOT)
        .define('b', Registration.DIMCORE.get())
        .unlockedBy(getHasName(Registration.DIMCORE.get()), has(Registration.DIMCORE.get()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIMCHEST.get())
        .pattern("aaa")
        .pattern("aba")
        .pattern("aaa")
        .define('a', Registration.DIMWALL.get())
        .define('b', Registration.SOLIDDIMCORE.get())
        .unlockedBy(getHasName(Registration.SOLIDDIMCORE.get()),
            has(Registration.SOLIDDIMCORE.get()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIMTABLET.get())
        .pattern("cdc")
        .pattern("cdc")
        .pattern("aba")
        .define('a', Items.OBSIDIAN)
        .define('b', Registration.SOLIDDIMCORE.get())
        .define('c', Items.IRON_INGOT)
        .define('d', Tags.Items.GLASS_PANES)
        .unlockedBy(getHasName(Registration.DIMCHEST.get()), has(Registration.DIMCHEST.get()))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIMTANK.get())
        .pattern("ada")
        .pattern("dcd")
        .pattern("aba")
        .define('a', Registration.DIMWALL.get())
        .define('b', Registration.SOLIDDIMCORE.get())
        .define('c', Items.CAULDRON)
        .define('d', Tags.Items.GLASS_BLOCKS)
        .unlockedBy(getHasName(Items.CAULDRON), has(Items.CAULDRON))
        .save(recipeOutput);
  }
}
