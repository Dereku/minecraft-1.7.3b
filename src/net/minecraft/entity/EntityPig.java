package net.minecraft.entity;

import net.minecraft.client.achiviements.AchievementList;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPig extends EntityAnimal {

   public EntityPig(World var1) {
      super(var1);
      this.texture = "/assets/mob/pig.png";
      this.setSize(0.9F, 0.9F);
   }

   @Override
   protected void entityInit() {
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("Saddle", this.getSaddled());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setSaddled(var1.getBoolean("Saddle"));
   }

   @Override
   protected String getLivingSound() {
      return "mob.pig";
   }

   @Override
   protected String getHurtSound() {
      return "mob.pig";
   }

   @Override
   protected String getDeathSound() {
      return "mob.pigdeath";
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      if(this.getSaddled() && !this.worldObj.multiplayerWorld && (this.riddenByEntity == null || this.riddenByEntity == var1)) {
         var1.mountEntity(this);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected int getDropItemId() {
      return this.fire > 0?Item.porkCooked.shiftedIndex:Item.porkRaw.shiftedIndex;
   }

   public boolean getSaddled() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSaddled(boolean var1) {
      if(var1) {
         this.dataWatcher.updateObject(16, Byte.valueOf((byte)1));
      } else {
         this.dataWatcher.updateObject(16, Byte.valueOf((byte)0));
      }

   }

   @Override
   public void onStruckByLightning(EntityLightningBolt var1) {
      if(!this.worldObj.multiplayerWorld) {
         EntityPigZombie var2 = new EntityPigZombie(this.worldObj);
         var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.worldObj.entityJoinedWorld(var2);
         this.setEntityDead();
      }
   }

   @Override
   protected void fall(float var1) {
      super.fall(var1);
      if(var1 > 5.0F && this.riddenByEntity instanceof EntityPlayer) {
         ((EntityPlayer)this.riddenByEntity).triggerAchievement(AchievementList.flyPig);
      }

   }
}
