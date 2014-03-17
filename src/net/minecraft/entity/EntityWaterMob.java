package net.minecraft.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWaterMob extends EntityCreature {

   public EntityWaterMob(World var1) {
      super(var1);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
   }

   public boolean getCanSpawnHere() {
      return this.worldObj.checkIfAABBIsClear(this.boundingBox);
   }

   public int getTalkInterval() {
      return 120;
   }
}