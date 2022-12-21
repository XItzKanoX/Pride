package me.utils;

import net.ccbluex.liquidbounce.LiquidBounce;

import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.features.module.modules.render.OldHitting;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class Debug extends MinecraftInstance {



    public static Boolean thePlayerisBlocking = false;
    public static void render(ScaledResolution sr, int itemX, float partialTicks) {
        GL11.glPushMatrix();
        final HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        GL11.glPopMatrix();

        int middleScreen = sr.getScaledWidth() / 2;


//        RenderUtils.drawRect(itemX, sr.getScaledHeight() - 23, itemX + 22, sr.getScaledHeight() - 22, new Color(0, 165, 255));
    }
    public static void func_178096_b(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equippedProg * -0.6F, 0.0F);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());

    }
    public static void transformSideFirstPersonBlock(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(side * 0.56, -0.52 + equippedProg * -0.6, -0.72);
        GlStateManager.translate(side * -0.1414214, 0.08, 0.1414214);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        double f = Math.sin(swingProgress * swingProgress * Math.PI);
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        GlStateManager.rotate((float) (f * -20.0F), 0, 1, 0);
        GlStateManager.rotate((float) (f1 * -20.0F), 0, 0, 1);
        GlStateManager.rotate((float) (f1 * -80.0F), 1, 0, 0);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }

    public static void Push(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(side * 0.56, -0.52 + equippedProg * -0.6, -0.72);
        GlStateManager.translate(side * -0.1414214, 0.08, 0.1414214);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        double f = Math.sin(swingProgress * swingProgress * Math.PI);
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        GlStateManager.rotate((float) (f *  -10.0F), 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate((float) (f1 * -10.0F), 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate((float) (f1 * -10.0F), 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }
    public static void sigmaold(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equippedProg * -0.6F, 0.0F);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        float var3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
        GlStateManager.rotate(var3 * -15F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -10F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -30F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());

    }
    public static void jello(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        float var13 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var14 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var13 * -35.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 0.0F, 0.0F, 0.0f, 0.0F);
        GlStateManager.rotate(var14 * 20.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }
    public static void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

        public static void Test(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
            int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equippedProg * -0.6F, 0.0F);
            GlStateManager.rotate(-102.25F, 1, 0, 0);
            GlStateManager.rotate(side * 13.365F, 0, 1, 0);
            GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        float var5 = MathHelper.ceil(MathHelper.floor(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var5 * -80.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }
    private void transformFirstPersonItem1(EnumHandSide p_187459_1_, float equippedProg, float swingProgress)
    {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equippedProg * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        float var5 = MathHelper.ceil(MathHelper.floor(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }

    public static void ETB(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
       GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equippedProg * -0.6F, 0.0F);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
    float var3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
    float var4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
}

    public static void Zoom(EnumHandSide p_187459_1_, float equippedProg, float swingProgress) {
        int side = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, swingProgress * -0.6F, 0.0F);
        GlStateManager.rotate(-102.25F, 1, 0, 0);
        GlStateManager.rotate(side * 13.365F, 0, 1, 0);
        GlStateManager.rotate(side * 78.050003F, 0, 0, 1);
        float var3 = MathHelper.sin(equippedProg * equippedProg * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt(equippedProg) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }



}
