package net.minecraft.item.crafting;

import java.util.Comparator;
import net.minecraft.src.IRecipe;
import net.minecraft.src.IRecipe;

class RecipeSorter implements Comparator {

   // $FF: synthetic field
   final CraftingManager craftingManager;


   RecipeSorter(CraftingManager var1) {
      this.craftingManager = var1;
   }

   public int compareRecipes(IRecipe var1, IRecipe var2) {
      return var1 instanceof ShapelessRecipes && var2 instanceof ShapedRecipes?1:(var2 instanceof ShapelessRecipes && var1 instanceof ShapedRecipes?-1:(var2.getRecipeSize() < var1.getRecipeSize()?-1:(var2.getRecipeSize() > var1.getRecipeSize()?1:0)));
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compare(Object var1, Object var2) {
      return this.compareRecipes((IRecipe)var1, (IRecipe)var2);
   }
}
