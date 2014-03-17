package net.minecraft.src;

import net.minecraft.src.Container;
import net.minecraft.client.item.ItemStack;

public interface ICrafting {

   void updateCraftingInventorySlot(Container var1, int var2, ItemStack var3);

   void updateCraftingInventoryInfo(Container var1, int var2, int var3);
}
