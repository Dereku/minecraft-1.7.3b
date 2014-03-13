package net.minecraft.src;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class ItemCoal extends Item {

   public ItemCoal(int var1) {
      super(var1);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   public String getItemNameIS(ItemStack var1) {
      return var1.getItemDamage() == 1?"item.charcoal":"item.coal";
   }
}
