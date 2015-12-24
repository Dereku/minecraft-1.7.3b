package net.minecraft.src;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public interface IRecipe {

   boolean matches(InventoryCrafting var1);

   ItemStack getCraftingResult(InventoryCrafting var1);

   int getRecipeSize();

   ItemStack getRecipeOutput();
}
