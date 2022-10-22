package me.utils.render;



import java.awt.Color;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

public class UiUtils2
{
    public UiUtils2() {
        super();
    }
    
    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }
    
    public static int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }
    public static int anima(int target,int speed) {
    	int a=0;
    	if(a<target) {
    		a+=speed;
    	}
    	if(a>target) {
    		a-=speed;
    	}
    	return a;
    }
    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((int)x, (int)y, (float)0.0f, (float)0.0f, (int)width, (int)height, (float)width, (float)height);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
    }
    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, Color color) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getRed() / 255.0f), (float)1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((int)x, (int)y, (float)0.0f, (float)0.0f, (int)width, (int)height, (float)width, (float)height);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2929);
    }
    public static void drawRoundRect(double d, double e, double g, double h, int color)
    {
    	drawRect(d+1, e, g-1, h, color);
    	drawRect(d, e+1, d+1, h-1, color);
    	drawRect(d+1, e+1, d+0.5, e+0.5, color);
    	drawRect(d+1, e+1, d+0.5, e+0.5, color);
    	drawRect(g-1, e+1, g-0.5, e+0.5, color);
    	drawRect(g-1, e+1, g, h-1, color);
    	drawRect(d+1, h-1, d+0.5, h-0.5, color);
    	drawRect(g-1, h-1, g-0.5, h-0.5, color);
    }
    public static void drawRoundRectWithRect(double d, double e, double g, double h, int color)
    {
    	drawRect(d+1, e, g-1, h, color);
    	drawRect(d, e+1, d+1, h-1, color);
    	drawRect(d+1, e+1, d+0.5, e+0.5, color);
    	drawRect(d+1, e+1, d+0.5, e+0.5, color);
    	drawRect(g-1, e+1, g-0.5, e+0.5, color);
    	drawRect(g-1, e+1, g, h-1, color);
    	drawRect(d+1, h-1, d+0.5, h-0.5, color);
    	drawRect(g-1, h-1, g-0.5, h-0.5, color);
    }
    public static void startGlScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        GL11.glScissor((int)(x * scaleFactor), (int)(mc.displayHeight - (y + height) * scaleFactor), (int)(width * scaleFactor), (int)(height * scaleFactor));
    }
    public static void stopGlScissor(){
        GL11.glDisable(3089);
        GL11.glPopMatrix(); 
    }
    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (float)(col1 >> 24 & 255) / 255.0F;
        float f1 = (float)(col1 >> 16 & 255) / 255.0F;
        float f2 = (float)(col1 >> 8 & 255) / 255.0F;
        float f3 = (float)(col1 & 255) / 255.0F;
        float f4 = (float)(col2 >> 24 & 255) / 255.0F;
        float f5 = (float)(col2 >> 16 & 255) / 255.0F;
        float f6 = (float)(col2 >> 8 & 255) / 255.0F;
        float f7 = (float)(col2 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
     }

     public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 255) / 255.0F;
        float f1 = (float)(col1 >> 16 & 255) / 255.0F;
        float f2 = (float)(col1 >> 8 & 255) / 255.0F;
        float f3 = (float)(col1 & 255) / 255.0F;
        float f4 = (float)(col2 >> 24 & 255) / 255.0F;
        float f5 = (float)(col2 >> 16 & 255) / 255.0F;
        float f6 = (float)(col2 >> 8 & 255) / 255.0F;
        float f7 = (float)(col2 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
     }
     public static void outlineRect(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
    	 drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawRect(x + width, y, x1 - width, y + width, borderColor);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawRect(x, y, x + width, y1, borderColor);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawRect(x1 - width, y, x1, y1, borderColor);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
     public static void drawRect(double x, double y, double x2, double y2, int color) {
         glEnable(GL_BLEND);
         glDisable(GL_TEXTURE_2D);
         glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
         glEnable(GL_LINE_SMOOTH);
         glPushMatrix();
         RenderUtils.glColor(new Color(color));
         glBegin(GL_QUADS);
         glVertex2d(x2, y);
         glVertex2d(x, y);
         glVertex2d(x, y2);
         glVertex2d(x2, y2);
         glEnd();
         glPopMatrix();
         glEnable(GL_TEXTURE_2D);
         glDisable(GL_BLEND);
         glDisable(GL_LINE_SMOOTH);
      }
}
