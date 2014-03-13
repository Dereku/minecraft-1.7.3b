package net.minecraft.src;

import net.minecraft.src.MapColor;
import net.minecraft.src.Material;

public class MaterialLiquid extends Material {

   public MaterialLiquid(MapColor var1) {
      super(var1);
      this.setIsGroundCover();
      this.setNoPushMobility();
   }

   public boolean getIsLiquid() {
      return true;
   }

   public boolean getIsSolid() {
      return false;
   }

   public boolean isSolid() {
      return false;
   }
}
