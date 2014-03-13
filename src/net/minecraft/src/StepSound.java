package net.minecraft.src;


public class StepSound {

   public final String stepSoundName;
   public final float stepSoundVolume;
   public final float stepSoundPitch;


   public StepSound(String var1, float var2, float var3) {
      this.stepSoundName = var1;
      this.stepSoundVolume = var2;
      this.stepSoundPitch = var3;
   }

   public float getVolume() {
      return this.stepSoundVolume;
   }

   public float getPitch() {
      return this.stepSoundPitch;
   }

   public String stepSoundDir() {
      return "step." + this.stepSoundName;
   }

   public String stepSoundDir2() {
      return "step." + this.stepSoundName;
   }
}
