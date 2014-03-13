package net.minecraft.src;

import net.minecraft.src.EnumOS1;

// $FF: synthetic class
class OsMap {

   // $FF: synthetic field
   static final int[] allOSes = new int[EnumOS1.values().length];


   static {
      try {
         allOSes[EnumOS1.linux.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
         ;
      }

      try {
         allOSes[EnumOS1.solaris.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
         ;
      }

      try {
         allOSes[EnumOS1.windows.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
         ;
      }

      try {
         allOSes[EnumOS1.macos.ordinal()] = 4;
      } catch (NoSuchFieldError var1) {
         ;
      }

   }
}
