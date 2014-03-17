package net.minecraft.src;

import net.minecraft.client.item.ItemStack;
import net.minecraft.client.item.Item;
import net.minecraft.client.achiviements.AchievementList;
import net.minecraft.entity.EntityPlayer;

public class SlotFurnace extends Slot {

   private EntityPlayer thePlayer;


   public SlotFurnace(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.thePlayer = var1;
   }

   @Override
   public boolean isItemValid(ItemStack var1) {
      return false;
   }

   @Override
   public void onPickupFromSlot(ItemStack var1) {
      var1.onCrafting(this.thePlayer.worldObj, this.thePlayer);
      if(var1.itemID == Item.ingotIron.shiftedIndex) {
         this.thePlayer.addStat(AchievementList.acquireIron, 1);
      }

      if(var1.itemID == Item.fishCooked.shiftedIndex) {
         this.thePlayer.addStat(AchievementList.cookFish, 1);
      }

      super.onPickupFromSlot(var1);
   }
}
