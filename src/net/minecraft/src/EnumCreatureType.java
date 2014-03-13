package net.minecraft.src;

import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityWaterMob;
import net.minecraft.src.IMob;
import net.minecraft.src.Material;

public enum EnumCreatureType {

   monster("monster", 0, IMob.class, 70, Material.air, false),
   creature("creature", 1, EntityAnimal.class, 15, Material.air, true),
   waterCreature("waterCreature", 2, EntityWaterMob.class, 5, Material.water, true);
   private final Class creatureClass;
   private final int maxNumberOfCreature;
   private final Material creatureMaterial;
   private final boolean isPeacefulCreature;
   // $FF: synthetic field
   private static final EnumCreatureType[] allCreatureTypes = new EnumCreatureType[]{monster, creature, waterCreature};


   private EnumCreatureType(String var1, int var2, Class var3, int var4, Material var5, boolean var6) {
      this.creatureClass = var3;
      this.maxNumberOfCreature = var4;
      this.creatureMaterial = var5;
      this.isPeacefulCreature = var6;
   }

   public Class getCreatureClass() {
      return this.creatureClass;
   }

   public int getMaxNumberOfCreature() {
      return this.maxNumberOfCreature;
   }

   public Material getCreatureMaterial() {
      return this.creatureMaterial;
   }

   public boolean getPeacefulCreature() {
      return this.isPeacefulCreature;
   }

}
