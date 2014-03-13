package net.minecraft.src;


public enum EnumSkyBlock {

   Sky("Sky", 0, 15),
   Block("Block", 1, 0);
   public final int field_1722_c;
   // $FF: synthetic field
   private static final EnumSkyBlock[] allSkyBlocks = new EnumSkyBlock[]{Sky, Block};


   private EnumSkyBlock(String var1, int var2, int var3) {
      this.field_1722_c = var3;
   }

}
