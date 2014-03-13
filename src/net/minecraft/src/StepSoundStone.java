package net.minecraft.src;

import net.minecraft.src.StepSound;

final class StepSoundStone extends StepSound {

   StepSoundStone(String var1, float var2, float var3) {
      super(var1, var2, var3);
   }

   public String stepSoundDir() {
      return "random.glass";
   }
}
