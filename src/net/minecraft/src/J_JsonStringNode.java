package net.minecraft.src;

import java.util.List;
import java.util.Map;
import net.minecraft.src.EnumJsonNodeType;
import net.minecraft.src.J_JsonNode;

public final class J_JsonStringNode extends J_JsonNode implements Comparable {

   private final String field_27224_a;


   J_JsonStringNode(String var1) {
      if(var1 == null) {
         throw new NullPointerException("Attempt to construct a JsonString with a null value.");
      } else {
         this.field_27224_a = var1;
      }
   }

   public EnumJsonNodeType getType() {
      return EnumJsonNodeType.STRING;
   }

   public String getText() {
      return this.field_27224_a;
   }

   public Map getFields() {
      throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
   }

   public List getElements() {
      throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
   }

   public boolean equals(Object var1) {
      if(this == var1) {
         return true;
      } else if(var1 != null && this.getClass() == var1.getClass()) {
         J_JsonStringNode var2 = (J_JsonStringNode)var1;
         return this.field_27224_a.equals(var2.field_27224_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_27224_a.hashCode();
   }

   public String toString() {
      return "JsonStringNode value:[" + this.field_27224_a + "]";
   }

   public int func_27223_a(J_JsonStringNode var1) {
      return this.field_27224_a.compareTo(var1.field_27224_a);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.func_27223_a((J_JsonStringNode)var1);
   }
}
