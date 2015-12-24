package net.minecraft.json;


interface J_Functor {

   boolean matchsNode(Object var1);

   Object applyTo(Object var1);

   String shortForm();
}
