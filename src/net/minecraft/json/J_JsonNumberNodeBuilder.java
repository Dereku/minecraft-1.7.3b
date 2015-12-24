package net.minecraft.json;

import net.minecraft.json.J_JsonNode;
import net.minecraft.json.J_JsonNodeBuilder;
import net.minecraft.json.J_JsonNodeFactories;

final class J_JsonNumberNodeBuilder implements J_JsonNodeBuilder {

   private final J_JsonNode field_27239_a;


   J_JsonNumberNodeBuilder(String var1) {
      this.field_27239_a = J_JsonNodeFactories.func_27311_b(var1);
   }

   public J_JsonNode buildNode() {
      return this.field_27239_a;
   }
}
