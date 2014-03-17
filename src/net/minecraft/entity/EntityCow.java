package net.minecraft.entity;

import net.minecraft.entity.EntityAnimal;
import net.minecraft.entity.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.world.World;

public class EntityCow extends EntityAnimal {

   public EntityCow(World var1) {
      super(var1);
      this.texture = "/mob/cow.png";
      this.setSize(0.9F, 1.3F);
   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
   }

   protected String getLivingSound() {
      return "mob.cow";
   }

   protected String getHurtSound() {
      return "mob.cowhurt";
   }

   protected String getDeathSound() {
      return "mob.cowhurt";
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected int getDropItemId() {
      return Item.leather.shiftedIndex;
   }

   public boolean interact(EntityPlayer var1) {
      ItemStack var2 = var1.inventory.getCurrentItem();
      if(var2 != null && var2.itemID == Item.bucketEmpty.shiftedIndex) {
         var1.inventory.setInventorySlotContents(var1.inventory.currentItem, new ItemStack(Item.bucketMilk));
         return true;
      } else {
         return false;
      }
   }
}
