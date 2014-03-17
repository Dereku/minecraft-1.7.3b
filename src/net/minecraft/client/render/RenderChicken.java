package net.minecraft.client.render;

import net.minecraft.src.Entity;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelBase;
import net.minecraft.client.render.RenderLiving;

public class RenderChicken extends RenderLiving {

   public RenderChicken(ModelBase var1, float var2) {
      super(var1, var2);
   }

   public void renderChicken(EntityChicken var1, double var2, double var4, double var6, float var8, float var9) {
      super.doRenderLiving(var1, var2, var4, var6, var8, var9);
   }

   protected float getWingRotation(EntityChicken var1, float var2) {
      float var3 = var1.field_756_e + (var1.field_752_b - var1.field_756_e) * var2;
      float var4 = var1.field_757_d + (var1.destPos - var1.field_757_d) * var2;
      return (MathHelper.sin(var3) + 1.0F) * var4;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float handleRotationFloat(EntityLiving var1, float var2) {
      return this.getWingRotation((EntityChicken)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
      this.renderChicken((EntityChicken)var1, var2, var4, var6, var8, var9);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      this.renderChicken((EntityChicken)var1, var2, var4, var6, var8, var9);
   }
}
