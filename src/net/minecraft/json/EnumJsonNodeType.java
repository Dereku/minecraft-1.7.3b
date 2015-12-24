package net.minecraft.json;


public enum EnumJsonNodeType {

   OBJECT("OBJECT", 0),
   ARRAY("ARRAY", 1),
   STRING("STRING", 2),
   NUMBER("NUMBER", 3),
   TRUE("TRUE", 4),
   FALSE("FALSE", 5),
   NULL("NULL", 6);
   // $FF: synthetic field
   private static final EnumJsonNodeType[] allJsonNodeTypes = new EnumJsonNodeType[]{OBJECT, ARRAY, STRING, NUMBER, TRUE, FALSE, NULL};


   private EnumJsonNodeType(String var1, int var2) {}

}
