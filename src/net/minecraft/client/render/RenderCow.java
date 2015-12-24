package net.minecraft.client.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCow;
import net.minecraft.entity.EntityCow;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.client.models.ModelBase;
import net.minecraft.client.models.ModelBase;
import net.minecraft.client.render.RenderLiving;

public class RenderCow extends RenderLiving {

   public RenderCow(ModelBase var1, float var2) {
      super(var1, var2);
   }

   public void renderCow(EntityCow var1, double var2, double var4, double var6, float var8, float var9) {
      super.doRenderLiving(var1, var2, var4, var6, var8, var9);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
      this.renderCow((EntityCow)var1, var2, var4, var6, var8, var9);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      this.renderCow((EntityCow)var1, var2, var4, var6, var8, var9);
   }
}
