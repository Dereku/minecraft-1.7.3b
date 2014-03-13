package net.minecraft.src;


public enum EnumOptions {

   MUSIC("MUSIC", 0, "options.music", true, false),
   SOUND("SOUND", 1, "options.sound", true, false),
   INVERT_MOUSE("INVERT_MOUSE", 2, "options.invertMouse", false, true),
   SENSITIVITY("SENSITIVITY", 3, "options.sensitivity", true, false),
   RENDER_DISTANCE("RENDER_DISTANCE", 4, "options.renderDistance", false, false),
   VIEW_BOBBING("VIEW_BOBBING", 5, "options.viewBobbing", false, true),
   ANAGLYPH("ANAGLYPH", 6, "options.anaglyph", false, true),
   ADVANCED_OPENGL("ADVANCED_OPENGL", 7, "options.advancedOpengl", false, true),
   FRAMERATE_LIMIT("FRAMERATE_LIMIT", 8, "options.framerateLimit", false, false),
   DIFFICULTY("DIFFICULTY", 9, "options.difficulty", false, false),
   GRAPHICS("GRAPHICS", 10, "options.graphics", false, false),
   AMBIENT_OCCLUSION("AMBIENT_OCCLUSION", 11, "options.ao", false, true),
   GUI_SCALE("GUI_SCALE", 12, "options.guiScale", false, false);
   private final boolean enumFloat;
   private final boolean enumBoolean;
   private final String enumString;
   // $FF: synthetic field
   private static final EnumOptions[] allOptions = new EnumOptions[]{MUSIC, SOUND, INVERT_MOUSE, SENSITIVITY, RENDER_DISTANCE, VIEW_BOBBING, ANAGLYPH, ADVANCED_OPENGL, FRAMERATE_LIMIT, DIFFICULTY, GRAPHICS, AMBIENT_OCCLUSION, GUI_SCALE};


   public static EnumOptions getEnumOptions(int var0) {
      EnumOptions[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumOptions var4 = var1[var3];
         if(var4.returnEnumOrdinal() == var0) {
            return var4;
         }
      }

      return null;
   }

   private EnumOptions(String var1, int var2, String var3, boolean var4, boolean var5) {
      this.enumString = var3;
      this.enumFloat = var4;
      this.enumBoolean = var5;
   }

   public boolean getEnumFloat() {
      return this.enumFloat;
   }

   public boolean getEnumBoolean() {
      return this.enumBoolean;
   }

   public int returnEnumOrdinal() {
      return this.ordinal();
   }

   public String getEnumString() {
      return this.enumString;
   }

}
