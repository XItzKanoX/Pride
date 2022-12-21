package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos;
import net.minecraft.client.renderer.chunk.RenderChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {
    @Inject(method = "setPosition", at = @At(value = "HEAD"))
    public void setPosition(int p_setPosition_1_, int p_setPosition_2_, int p_setPosition_3_, CallbackInfo ci) {
        LiquidBounce.animationHandler.setPosition((RenderChunk) (Object) this, new WBlockPos(p_setPosition_1_, p_setPosition_2_, p_setPosition_3_));
    }
}