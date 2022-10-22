/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.utils.BlurUtils
import me.utils.BlurUtils.blurArea
import me.utils.render.VisualUtils
import me.utils.render.VisualUtils.skyRainbow
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer
import net.ccbluex.liquidbounce.utils.Colors
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.utils.render.Colors.getHealthColor
import net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val colorRedValue2 = IntegerValue("R2", 0, 0, 255)
    private val colorGreenValue2 = IntegerValue("G2", 111, 0, 255)
    private val colorBlueValue2 = IntegerValue("B2", 255, 0, 255)
    private val sb = BoolValue("Blur", true)
    private val riseShadow = BoolValue("Rise-Shadow", true)
    private val riseShadowLegacy = BoolValue("Rise-Shadow-Legacy", true)
    private val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 30F   )
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val amountValue = IntegerValue("Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.jello35)
    private val x1 = IntegerValue("x1", 2, 0, 300)
    private val x2 = IntegerValue("x2", 170, 0, 200)
    private val y1 = IntegerValue("y1", 10, 0, 200)
    private val y2 = IntegerValue("y2", 7, 0, 300)
    private val x3 = IntegerValue("x3", 7, 0, 200)
    private val y3 = IntegerValue("y3", 7, 0, 300)
    private val y4 = IntegerValue("y4", 7, 0, 300)
    private val x4 = IntegerValue("x4", 7, 0, 300)
    private var easingHealth: Float = 0F
    private var lastTarget: IEntity? = null

    override fun drawElement(): Border {
        val font = Fonts.font40
        val target = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
//        val playerInfo = mc.netHandler.getPlayerInfo(target!!.uniqueID)
        val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info.toString())).toFloat() + 40F).coerceAtLeast(125F)
        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()


        if (classProvider.isEntityPlayer(target) && target != null) {
            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                    abs(easingHealth - target.health) < 0.01) {
                easingHealth = target.health
            }

            val width = (38 + (target.name?.let(  fontValue.get()::getStringWidth) ?: 0))
                    .coerceAtLeast(118)
                    .toFloat()

            // Draw rect box
//            RenderUtils.drawShadowWithCustomAlpha(-2F, 0F, width+ 11, 40F,114514F)
//            RenderUtils.drawRoundRect(-2.1F, 0F, width+ 10, 40F, 4F, Color(255,  255,  255, 100).rgb)

            // Damage animation
            if (easingHealth > target.health)
                RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                        36F, Color(252, 185, 65).rgb)
            //Blur
            if (sb.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
//                GL11.glPushMatrix()
                if (riseShadow.get() && !riseShadowLegacy.get())
                    BlurUtils.blurArea(floatX, floatY, floatX + 10F + length, floatY + 55F, blurStrength.get())
                else
                    BlurUtils.blurAreaRounded(floatX, floatY, floatX + 10F + length, floatY + 55F, 3F, blurStrength.get())

                GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }

            // Health bar
            RenderUtils.drawRect( 1f, y1.get().toFloat(), x2.get().toFloat(),39f ,Color(106, 106, 106, 80))
            RenderUtils.drawRoundRect( 29f, 2f, 9f, 36f, 8F, Color(106, 106, 106, 80).rgb)
//            RenderUtils.drawRect(+2.0F, 35.0F, (target.health / target.maxHealth) * width,
//                38.0F, if(target.health >= 6)
//                    Color(10, 243, 10 ,150)
//            else Color(255, 10 , 15, 200))
            for (i in 0..(amountValue.get() - 1))
                RenderUtils.drawGradientSideways(+3.0,41.3, ((target.health / target.maxHealth) * width).toDouble() -10, 43.0, VisualUtils.getGradientOffset2(
                    Color(redValue.get(), greenValue.get(), blueValue.get()),
                    Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb,
                    VisualUtils.getGradientOffset2(
                        Color(0, 111, 255),
                        Color(255, 255 , 255), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb)






            // Heal animation
            if (easingHealth < target.health)
                RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                        (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
            RenderUtils.drawRect(109f, 37f, 39f,2f ,Color(106, 106, 106, 80))
            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let {   fontValue.get().drawStringWithShadow(it, 42, 5, 0xffffff) }
          fontValue.get().drawStringWithShadow("Distance: ${decimalFormat.format(mc.thePlayer!!.getDistanceToEntityBox(target))}", 42, 18, 0xffffff)

            // Draw info
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {
                fontValue.get().drawStringWithShadow("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                        42, 26, 0xffffff)
                //Draw headRect
                RenderUtils.drawRect(-1f, 39f, 35f, -4f ,Color(106, 106, 106, 80))
                // Draw head
                val locationSkin = playerInfo.locationSkin
                    Stencil.write(false)
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                    RenderUtils.fastRoundedRect(4F, 4F, 34F, 34F, 8F)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    Stencil.erase(true)
                    //GL11.glTranslated(renderX, renderY, 0.0)
                    drawHead(playerInfo.locationSkin, 4, 4, 30, 30, 1F - 0f)
                    //GL11.glTranslated(-renderX, -renderY, 0.0)
                    Stencil.dispose()
//                drawHead(locationSkin, 30, 30)
            }
        }

        lastTarget = target
        return Border(0F, 0F, 120F, 36F)
    }


        private fun drawHead(skin: IResourceLocation, x: Int, y: Int, width: Int, height: Int, alpha: Float) {
            GL11.glColor4f(1F, 1F, 1F, alpha)
            mc.textureManager.bindTexture(skin)
            Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
                64F, 64F)
        }

    }

