package net.minecraft.src;

public final class StepSoundStone extends StepSound {

    public StepSoundStone(String var1, float var2, float var3) {
        super(var1, var2, var3);
    }

    @Override
    public String stepSoundDir() {
        return "random.glass";
    }
}
