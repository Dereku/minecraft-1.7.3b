package net.minecraft.src;

import net.minecraft.src.MapColor;
import net.minecraft.src.Material;

public class MaterialPortal extends Material {

   public MaterialPortal(MapColor var1) {
      super(var1);
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
