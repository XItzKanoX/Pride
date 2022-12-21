/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements


import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityPlayerSP
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.features.module.modules.color.Gident

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer


import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.inventory.IInventory
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD Armor element
 *
 * Shows a horizontal display of current armor
 */

@ElementInfo(name = "Inventory")
class Inventory(x: Double = -1.0, y: Double = 121.0, scale: Float = 1F) : Element(x, y, scale) {
    private var inventoryRows = 0
    private val lowerInv: IInventory? = null


    private val blur = BoolValue("Blur", true)
    private val title  = BoolValue("Title", false)
    private val modeValue = ListValue("Background", arrayOf("Default", "Shadow"), "Default")
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)

    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val newRainbowIndex = IntegerValue("NewRainbowOffset", 1, 1, 50)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoclient = IntegerValue("AstolfoRange", 109, 1, 765)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom", "Rainbow", "Fade", "Astolfo", "NewRainbow","Gident"), "Custom")
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.fontSFUI35)

    /**
     * Draw element
     */

    private var mc1: Minecraft? = Minecraft.getMinecraft()
    override fun drawElement(): Border {
        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        if (blur.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            BlurBuffer.blurArea(floatX + 7f, floatY + 18  , 166F, 70F )
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        when (modeValue.get().toLowerCase()) {
            "default" ->  RenderUtils.drawRect(7f, 21f, 172f , 90f, Color(20, 19, 18, 170).rgb)
        }

        val barLength1 = (163f).toDouble()
        if (modeValue.get().equals("Default", true)) {
            RenderUtils.drawShadowWithCustomAlpha(7f, 21f, 165f, 68f, 200F)
            for (i in 0 until gradientAmountValue.get()) {
                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * barLength1
                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * barLength1

                RenderUtils.drawGradientSideways(
                    8 + barStart, 20.0, 8 + barEnd, 21.0,
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                            Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        else -> color
                    },
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                            Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(),1),
                            (Math.abs(
                                System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )
                        else -> color
                    }
                )
            }
        }
        if (modeValue.get().equals("Shadow", true)) {
            RenderUtils.drawShadowWithCustomAlpha(+7F, 18.0F, 166F, 70F,255F)
        }

        if (title.get()) {
            fontValue.get().drawString("Inventory", 10, 23, Color(0xFFFFFF).rgb)
        }
            if (lowerInv != null) {
                this.inventoryRows = lowerInv.getSizeInventory()
            }
            renderInventory1(mc.thePlayer)
            renderInventory2(mc.thePlayer)
            renderInventory3(mc.thePlayer)



        return Border(+8F, this.inventoryRows * 18F + 17F, 172f , 90f)



    }

    private fun renderInventory1(player: IEntityPlayerSP?) {
        var armourStack: IItemStack?
        var renderStack = player!!.inventory.mainInventory
        var xOffset = 8
        renderStack = player.inventory.mainInventory
        for (index in 9..17) {
            armourStack = renderStack[index]
            if (armourStack != null) this.renderItemStack(armourStack, xOffset, 30)
            xOffset += 18
        }
    }

    private fun renderInventory2(player: IEntityPlayerSP?) {
        var armourStack: IItemStack?
        var renderStack = player!!.inventory.mainInventory
        var xOffset = 8
        renderStack = player.inventory.mainInventory
        for (index in 18..26) {
            armourStack = renderStack[index]
            if (armourStack != null) this.renderItemStack(armourStack, xOffset, 48)
            xOffset += 18
        }
    }

    private fun renderInventory3(player: IEntityPlayerSP?) {
        var armourStack: IItemStack?
        var renderStack = player!!.inventory.mainInventory
        var xOffset = 8
        renderStack = player.inventory.mainInventory
        for (index in 27..35) {
            armourStack = renderStack[index]
            if (armourStack != null) this.renderItemStack(armourStack, xOffset, 66)
            xOffset += 18
        }
    }

    private fun renderItemStack(stack: IItemStack, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

}