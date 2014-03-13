package net.minecraft.src;

import net.minecraft.src.MapColor;
import net.minecraft.src.Material;

public class MaterialTransparent extends Material {

   public MaterialTransparent(MapColor var1) {
      super(var1);
      this.setIsGroundCover();
   }

   public boolean isSolid() {
      return false;
   }

   public boolean getCanBlockGrass() {
      return false;
   }

   public boolean getIsSolid() {
      return false;
   }
}
