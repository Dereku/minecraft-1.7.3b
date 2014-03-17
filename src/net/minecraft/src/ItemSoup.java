package net.minecraft.src;

import net.minecraft.entity.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.world.World;

public class ItemSoup extends ItemFood {

   public ItemSoup(int var1, int var2) {
      super(var1, var2, false);
   }

   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      super.onItemRightClick(var1, var2, var3);
      return new ItemStack(Item.bowlEmpty);
   }
}
