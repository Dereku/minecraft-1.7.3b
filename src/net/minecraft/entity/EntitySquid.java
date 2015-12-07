package net.minecraft.entity;

import net.minecraft.client.item.Item;
import net.minecraft.client.item.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySquid extends EntityWaterMob {

   public float field_21089_a = 0.0F;
   public float field_21088_b = 0.0F;
   public float field_21087_c = 0.0F;
   public float field_21086_f = 0.0F;
   public float field_21085_g = 0.0F;
   public float field_21084_h = 0.0F;
   public float field_21083_i = 0.0F;
   public float field_21082_j = 0.0F;
   private float randomMotionSpeed = 0.0F;
   private float field_21080_l = 0.0F;
   private float field_21079_m = 0.0F;
   private float randomMotionVecX = 0.0F;
   private float randomMotionVecY = 0.0F;
   private float randomMotionVecZ = 0.0F;


   public EntitySquid(World var1) {
      super(var1);
      this.texture = "/assets/mob/squid.png";
      this.setSize(0.95F, 0.95F);
      this.field_21080_l = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
   }

   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
   }

   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
   }

   protected String getLivingSound() {
      return null;
   }

   protected String getHurtSound() {
      return null;
   }

   protected String getDeathSound() {
      return null;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected int getDropItemId() {
      return 0;
   }

   protected void dropFewItems() {
      int var1 = this.rand.nextInt(3) + 1;

      for(int var2 = 0; var2 < var1; ++var2) {
         this.entityDropItem(new ItemStack(Item.dyePowder, 1, 0), 0.0F);
      }

   }

   public boolean interact(EntityPlayer var1) {
      return false;
   }

   public boolean isInWater() {
      return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.6000000238418579D, 0.0D), Material.water, this);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.field_21088_b = this.field_21089_a;
      this.field_21086_f = this.field_21087_c;
      this.field_21084_h = this.field_21085_g;
      this.field_21082_j = this.field_21083_i;
      this.field_21085_g += this.field_21080_l;
      if(this.field_21085_g > 6.2831855F) {
         this.field_21085_g -= 6.2831855F;
         if(this.rand.nextInt(10) == 0) {
            this.field_21080_l = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
         }
      }

      if(this.isInWater()) {
         float var1;
         if(this.field_21085_g < 3.1415927F) {
            var1 = this.field_21085_g / 3.1415927F;
            this.field_21083_i = MathHelper.sin(var1 * var1 * 3.1415927F) * 3.1415927F * 0.25F;
            if((double)var1 > 0.75D) {
               this.randomMotionSpeed = 1.0F;
               this.field_21079_m = 1.0F;
            } else {
               this.field_21079_m *= 0.8F;
            }
         } else {
            this.field_21083_i = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.field_21079_m *= 0.99F;
         }

         if(!this.isMultiplayerEntity) {
            this.motionX = (double)(this.randomMotionVecX * this.randomMotionSpeed);
            this.motionY = (double)(this.randomMotionVecY * this.randomMotionSpeed);
            this.motionZ = (double)(this.randomMotionVecZ * this.randomMotionSpeed);
         }

         var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset += (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / 3.1415927F - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.field_21087_c += 3.1415927F * this.field_21079_m * 1.5F;
         this.field_21089_a += (-((float)Math.atan2((double)var1, this.motionY)) * 180.0F / 3.1415927F - this.field_21089_a) * 0.1F;
      } else {
         this.field_21083_i = MathHelper.abs(MathHelper.sin(this.field_21085_g)) * 3.1415927F * 0.25F;
         if(!this.isMultiplayerEntity) {
            this.motionX = 0.0D;
            this.motionY -= 0.08D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ = 0.0D;
         }

         this.field_21089_a = (float)((double)this.field_21089_a + (double)(-90.0F - this.field_21089_a) * 0.02D);
      }

   }

   public void moveEntityWithHeading(float var1, float var2) {
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
   }

   protected void updateEntityActionState() {
      if(this.rand.nextInt(50) == 0 || !this.inWater || this.randomMotionVecX == 0.0F && this.randomMotionVecY == 0.0F && this.randomMotionVecZ == 0.0F) {
         float var1 = this.rand.nextFloat() * 3.1415927F * 2.0F;
         this.randomMotionVecX = MathHelper.cos(var1) * 0.2F;
         this.randomMotionVecY = -0.1F + this.rand.nextFloat() * 0.2F;
         this.randomMotionVecZ = MathHelper.sin(var1) * 0.2F;
      }

      this.despawnEntity();
   }
}
