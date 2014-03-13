package net.minecraft.src;

import java.util.List;
import java.util.Map;
import net.minecraft.src.EnumJsonNodeType;
import net.minecraft.src.J_JsonNode;

final class J_JsonConstants extends J_JsonNode {

   static final J_JsonConstants NULL = new J_JsonConstants(EnumJsonNodeType.NULL);
   static final J_JsonConstants TRUE = new J_JsonConstants(EnumJsonNodeType.TRUE);
   static final J_JsonConstants FALSE = new J_JsonConstants(EnumJsonNodeType.FALSE);
   private final EnumJsonNodeType jsonNodeType;


   private J_JsonConstants(EnumJsonNodeType var1) {
      this.jsonNodeType = var1;
   }

   public EnumJsonNodeType getType() {
      return this.jsonNodeType;
   }

   public String getText() {
      throw new IllegalStateException("Attempt to get text on a JsonNode without text.");
   }

   public Map getFields() {
      throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
   }

   public List getElements() {
      throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
   }

}
