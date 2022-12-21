/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import me.utils.Debug;
import me.utils.render.RoundedUtil;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.features.module.modules.render.NoScoreboard;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.utils.ClassUtils;
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiIngame.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiInGame extends MixinGui {

    @Shadow
    @Final
    protected static ResourceLocation WIDGETS_TEX_PATH;
    @Shadow
    @Final
    protected Minecraft mc;

    @Shadow
    protected abstract void renderHotbarItem(int xPos, int yPos, float partialTicks, EntityPlayer player, ItemStack stack);


    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        if (LiquidBounce.moduleManager.getModule(HUD.class).getState() || NoScoreboard.INSTANCE.getState())
            callbackInfo.cancel();
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        final HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);

        if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && hud.getState() && (hud.getBlackHotbarValue().get() || hud.getAnimHotbarValue().get())) {
            final Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer entityPlayer = (EntityPlayer) mc.getRenderViewEntity();

            boolean blackHB = hud.getBlackHotbarValue().get();
            int middleScreen = sr.getScaledWidth() / 2;
            float posInv = hud.getAnimPos(entityPlayer.inventory.currentItem * 20F);

            GlStateManager.resetColor();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);

            float f = this.zLevel;
            this.zLevel = -90.0F;
            Color var10000 = me.utils.render.RenderUtils.interpolateColorsBackAndForth(8, 1, new Color(255, 255, 255, 255), new Color(0, 0, 0, 0), false);
            GuiIngame.drawRect(middleScreen - 91  + entityPlayer.inventory.currentItem * 20 + 1+2, sr.getScaledHeight() - 2, middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 22, sr.getScaledHeight() - 22 - 1 + 1, var10000.getRGB());
            BlurBuffer.blurArea2(middleScreen - 91, sr.getScaledHeight() - 24, middleScreen + 91, sr.getScaledHeight());
            GuiIngame.drawRect(middleScreen - 91  + entityPlayer.inventory.currentItem * 20 + 1+2, sr.getScaledHeight() - 24, middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 22, sr.getScaledHeight() - 22 - 1 + 1, var10000.getRGB());


            GlStateManager.resetColor();
            RenderUtils.originalRoundedRect(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 91, sr.getScaledHeight() - 22, 3F, Integer.MIN_VALUE);
            RenderUtils.originalRoundedRect(middleScreen - 91 + posInv, sr.getScaledHeight() - 2, middleScreen - 91 + posInv + 22, sr.getScaledHeight() - 22, 3F, Integer.MAX_VALUE);








            this.zLevel = f;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int l = 0; l < 9; ++l) {
                int i1 = middleScreen - 90 + l * 20 + 2;
                int j1 = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(i1, j1, partialTicks, entityPlayer, entityPlayer.inventory.mainInventory.get(l));
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.resetColor();
            LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
            callbackInfo.cancel();
        }
    }


    @Overwrite
    protected void renderPotionEffects(ScaledResolution p_renderPotionEffects_1_) {
        //去掉1.12.2脑残HUD药水显示
    }


    @Inject(method = "renderHotbar", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        callRender2DEvent(partialTicks);
    }

    private void callRender2DEvent(float partialTicks) {
        if (!ClassUtils.hasClass("net.labymod.api.LabyModAPI")) {
            LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if (antiBlind.getState() && antiBlind.getPumpkinEffect().get())
            callbackInfo.cancel();
    }
}