package net.minecraft.src;


public enum EnumToolMaterial {

   WOOD("WOOD", 0, 0, 59, 2.0F, 0),
   STONE("STONE", 1, 1, 131, 4.0F, 1),
   IRON("IRON", 2, 2, 250, 6.0F, 2),
   EMERALD("EMERALD", 3, 3, 1561, 8.0F, 3),
   GOLD("GOLD", 4, 0, 32, 12.0F, 0);
   private final int harvestLevel;
   private final int maxUses;
   private final float efficiencyOnProperMaterial;
   private final int damageVsEntity;
   // $FF: synthetic field
   private static final EnumToolMaterial[] allToolMaterials = new EnumToolMaterial[]{WOOD, STONE, IRON, EMERALD, GOLD};


   private EnumToolMaterial(String var1, int var2, int var3, int var4, float var5, int var6) {
      this.harvestLevel = var3;
      this.maxUses = var4;
      this.efficiencyOnProperMaterial = var5;
      this.damageVsEntity = var6;
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public float getEfficiencyOnProperMaterial() {
      return this.efficiencyOnProperMaterial;
   }

   public int getDamageVsEntity() {
      return this.damageVsEntity;
   }

   public int getHarvestLevel() {
      return this.harvestLevel;
   }

}
