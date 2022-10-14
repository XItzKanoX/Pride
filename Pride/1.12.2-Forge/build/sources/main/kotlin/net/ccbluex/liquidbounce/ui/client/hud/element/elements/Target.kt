/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.Colors
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
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
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val amountValue = IntegerValue("Amount", 25, 1, 50)
    private var easingHealth: Float = 0F
    private var lastTarget: IEntity? = null

    override fun drawElement(): Border {
        val target = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target

        if (classProvider.isEntityPlayer(target) && target != null) {
            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                    abs(easingHealth - target.health) < 0.01) {
                easingHealth = target.health
            }

            val width = (38 + (target.name?.let(Fonts.font40::getStringWidth) ?: 0))
                    .coerceAtLeast(118)
                    .toFloat()

            // Draw rect box
            RenderUtils.drawShadowWithCustomAlpha(-2F, 0F, width+ 11, 40F,114514F)
            RenderUtils.drawRoundRect(-2.1F, 0F, width+ 10, 40F, 4F, Color(255,  255,  255, 100).rgb)

            // Damage animation
            if (easingHealth > target.health)
                RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                        36F, Color(252, 185, 65).rgb)

            // Health bar
            for (i in 0..(amountValue.get() - 1))
            RenderUtils.drawGradientSideways(3.0, 34.0, ((target.health / target.maxHealth) * width).toDouble(), 37.0, VisualUtils.getGradientOffset2(
                    Color(redValue.get(), greenValue.get(), blueValue.get()),
                    Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb,
                    VisualUtils.getGradientOffset2(
                    Color(0, 111, 255),
                    Color(255, 255 , 255), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb)




            // Heal animation
            if (easingHealth < target.health)
                RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                        (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { Fonts.fontSFUI40.drawString(it, 36, 3, 0xffffff) }
            Fonts.fontSFUI35.drawString("Distance: ${decimalFormat.format(mc.thePlayer!!.getDistanceToEntityBox(target))}", 36, 15, 0xffffff)

            // Draw info
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {
                Fonts.fontSFUI35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                        36, 24, 0xffffff)

                // Draw head
                val locationSkin = playerInfo.locationSkin
                drawHead(locationSkin, 30, 30)
            }
        }

        lastTarget = target
        return Border(0F, 0F, 120F, 36F)
    }

    private fun drawHead(skin: IResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        RenderUtils.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
                64F, 64F)
    }

}