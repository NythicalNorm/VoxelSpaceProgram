package com.nythicalnorm.voxelspaceprogram.mixin;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.nythicalnorm.voxelspaceprogram.util.VSPNotCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.renderer.block.model.multipart.Selector.Deserializer.class)
public class SelectorDeserializerNotMixin { // copied from Zero Point Systems
    @WrapMethod(method = "getCondition")
    private static Condition zps$wrapGetCondition(JsonObject pJson, Operation<Condition> original) {
        // Handle NOT as a single-key condition type, just like OR and AND
        if (pJson.size() == 1) {
            if (pJson.has(VSPNotCondition.TOKEN)) {
                JsonObject innerJson = GsonHelper.getAsJsonObject(pJson, VSPNotCondition.TOKEN);
                Condition innerCondition = original.call(innerJson);
                return new VSPNotCondition(innerCondition);
            }
        }
        // Delegate to original for all other cases (OR, AND, property conditions)
        return original.call(pJson);
    }
}