package net.minecraft.src;


public enum EnumStatus {

   OK("OK", 0),
   NOT_POSSIBLE_HERE("NOT_POSSIBLE_HERE", 1),
   NOT_POSSIBLE_NOW("NOT_POSSIBLE_NOW", 2),
   TOO_FAR_AWAY("TOO_FAR_AWAY", 3),
   OTHER_PROBLEM("OTHER_PROBLEM", 4);
   // $FF: synthetic field
   private static final EnumStatus[] allStatuses = new EnumStatus[]{OK, NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW, TOO_FAR_AWAY, OTHER_PROBLEM};


   private EnumStatus(String var1, int var2) {}

}
