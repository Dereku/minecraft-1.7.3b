package net.minecraft.client.render;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityPig;
import net.minecraft.entity.EntityPig;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelBase;
import net.minecraft.client.render.RenderLiving;

public class RenderPig extends RenderLiving {

   public RenderPig(ModelBase var1, ModelBase var2, float var3) {
      super(var1, var3);
      this.setRenderPassModel(var2);
   }

   protected boolean renderSaddledPig(EntityPig var1, int var2, float var3) {
      this.loadTexture("/mob/saddle.png");
      return var2 == 0 && var1.getSaddled();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean shouldRenderPass(EntityLiving var1, int var2, float var3) {
      return this.renderSaddledPig((EntityPig)var1, var2, var3);
   }
}
