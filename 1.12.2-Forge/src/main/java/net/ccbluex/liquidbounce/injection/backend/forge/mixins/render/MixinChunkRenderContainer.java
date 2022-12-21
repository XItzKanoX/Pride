package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.WolrdAnim;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderContainer.class)
public class MixinChunkRenderContainer {
    @Inject(method = "preRenderChunk", at = @At(value = "HEAD"))
    public void preRenderChunk(RenderChunk renderChunkIn, CallbackInfo callbackInfo)
    {
        WolrdAnim world = (WolrdAnim) LiquidBounce.moduleManager.getModule(WolrdAnim.class);
        if(world.getState())
            LiquidBounce.animationHandler.preRender(renderChunkIn);
    }
}