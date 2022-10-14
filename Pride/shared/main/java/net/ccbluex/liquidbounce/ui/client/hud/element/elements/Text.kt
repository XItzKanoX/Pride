/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements


import me.utils.render.Palette2
import me.utils.render.UiUtils2
import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CPSCounter
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawGradientSideways
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRect
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.value.*
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F,
           side: Side = Side.default()) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("MMddyy")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")
        val Y_FORMAT = DecimalFormat("0.000000000")
        val DECIMAL_FORMAT = DecimalFormat("0.00")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 2.0, y = 2.0, scale = 1F)

            text.displayString.set(LiquidBounce.CLIENT_NAME + " b" + LiquidBounce.CLIENT_VERSION)
            text.shadow.set(true)
            text.fontValue.set(Fonts.minecraftFont)
            text.setColor(Color(200, 50, 50))
            return text
        }

    }

    private val displayString = TextValue("DisplayText", "")
    private val rectMode = ListValue("RectMode", arrayOf("None","Line","Background", "OneTap", "Skeet", "Slide"),"Line")
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom" , "Fade", "Gident"), "Fade")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val colorRedValue2 = IntegerValue("R2", 0, 0, 255)
    private val colorGreenValue2 = IntegerValue("G2", 111, 0, 255)
    private val colorBlueValue2 = IntegerValue("B2", 255, 0, 255)
    private val bgredValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bggreenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val bgblueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val bgalphaValue = IntegerValue("Background-Alpha", 120, 0, 255)
    private val amountValue = IntegerValue("Amount", 25, 1, 50)
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val shadow = BoolValue("Shadow", false)
    private var fontValue = FontValue("Font", Fonts.minecraftFont)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "Text Element"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        val thePlayer = mc.thePlayer

        if (thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(thePlayer.posX)
                "y" -> return DECIMAL_FORMAT.format(thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(thePlayer.posZ)
                "xdp" -> return thePlayer.posX.toString()
                "ydp" -> return thePlayer.posY.toString()
                "zdp" -> return thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(thePlayer.motionX * thePlayer.motionX + thePlayer.motionZ * thePlayer.motionZ))
                "ping" -> return EntityUtils.getPing(mc2.player).toString()
                "0" -> return "§0"
                "1" -> return "§1"
                "2" -> return "§2"
                "3" -> return "§3"
                "4" -> return "§4"
                "5" -> return "§5"
                "6" -> return "§6"
                "7" -> return "§7"
                "8" -> return "§8"
                "9" -> return "§9"
                "a" -> return "§a"
                "b" -> return "§b"
                "c" -> return "§c"
                "d" -> return "§d"
                "e" -> return "§e"
                "f" -> return "§f"
                "n" -> return "§n"
                "m" -> return "§m"
                "l" -> return "§l"
                "k" -> return "§k"
                "o" -> return "§o"
                "r" -> return "§r"
            }
        }

        return when (str) {
            "username" -> mc.session.username
            "clientname" -> LiquidBounce.CLIENT_NAME
            "clientversion" -> "b${LiquidBounce.CLIENT_VERSION}"
            "clientcreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> mc.debugFPS.toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()

            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val colorMode = colorModeValue.get()
        val counter = intArrayOf(0)
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        val colord = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb+Color(0,0,0,50).rgb
        val fontRenderer = fontValue.get()

        val counter2 = intArrayOf(0)
        val rainbow = colorMode.equals("Rainbow", ignoreCase = true)

        val charArray2 = displayText.toCharArray()
        var length2 = 4.5f


        when (rectMode.get()) {
            "Line" -> {
                for (i in 0..(amountValue.get() - 1)) {
                    RenderUtils.drawRect(
                        -2f,
                        -1f,
                        fontRenderer.getStringWidth(displayText).toFloat() + 2f,
                        fontRenderer.fontHeight.toFloat(),
                        Color(0, 0, 0, bgalphaValue.get())
                    )
                    RenderUtils.drawRect(
                        -2f,
                        -2f,
                        fontRenderer.getStringWidth(displayText).toFloat() + 2f,
                        -1f,
                        when (colorModeValue.get().toLowerCase()) {
                            "fade" -> Palette2.fade2(
                                Color(redValue.get(), greenValue.get(), blueValue.get()),
                                counter[0] * 100,
                                displayText.length * 200
                            ).rgb
                            "gident" -> VisualUtils.getGradientOffset(
                                Color(
                                    redValue.get(),
                                    greenValue.get(),
                                    blueValue.get()
                                ),
                                Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1),
                                (Math.abs(
                                    System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()
                                ) / 10)
                            ).rgb
                            else -> color
                        }
                    )
                }
            }

            "Background"-> {
                RenderUtils.drawRect(-2F, -2F, (length2), fontRenderer.fontHeight + 0F, Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()))
            }
            "OneTap"-> {
                RenderUtils.drawRect(-4.0f, -8.0f, (fontRenderer.getStringWidth(displayText) + 3).toFloat(), fontRenderer.fontHeight.toFloat(), Color(43,43,43).rgb)
                RenderUtils.drawGradientSideways(-3.0, -7.0, (fontRenderer.getStringWidth(displayText) +2.0).toDouble(), -3.0, if (rainbow)
                    ColorUtils.rainbow(400000000L).rgb+Color(0,0,0,40).rgb else colord,if (rainbow)
                    ColorUtils.rainbow(400000000L).rgb else color)
            }
            "Skeet"-> {
                UiUtils2.drawRect(-11.0, -9.5, (fontRenderer.getStringWidth(displayText) + 9).toDouble(), fontRenderer.fontHeight.toDouble()+6,Color(0,0,0).rgb)
                UiUtils2.outlineRect(-10.0, -8.5, (fontRenderer.getStringWidth(displayText) + 8).toDouble(), fontRenderer.fontHeight.toDouble()+5,8.0, Color(59,59,59).rgb,Color(59,59,59).rgb)
                UiUtils2.outlineRect(-9.0, -7.5, (fontRenderer.getStringWidth(displayText) + 7).toDouble(), fontRenderer.fontHeight.toDouble()+4,4.0, Color(59,59,59).rgb,Color(40,40,40).rgb)
                UiUtils2.outlineRect(-4.0, -3.0, (fontRenderer.getStringWidth(displayText) + 2).toDouble(), fontRenderer.fontHeight.toDouble()+0,1.0, Color(18,18,18).rgb,Color(0,0,0).rgb)
            }
            "Slide"-> {
                drawRect(-4.0f, -4.5f, (length2).toFloat(), fontRenderer.fontHeight.toFloat(), Color(bgredValue.get(), bgredValue.get(), blueValue.get(), bgalphaValue.get()).rgb)

                val barLength = (length2 + 1).toDouble()
                for (i in 0..(amountValue.get()-1)) {
                    val barStart = i.toDouble() / amountValue.get().toDouble() * barLength
                    val barEnd = (i + 1).toDouble() / amountValue.get().toDouble() * barLength
                    drawGradientSideways(-4.0 + barStart, -4.2, -1.0 + barEnd, -3.0,
                        when {
                            colorMode.equals("Fade", ignoreCase = true) -> Palette2.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), displayText.length * 200).rgb
                            colorMode.equals("Gident", ignoreCase = true) -> VisualUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(),1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                            else -> color
                        },
                        when {
                            colorMode.equals("Fade", ignoreCase = true) -> Palette2.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), displayText.length * 200).rgb
                            colorMode.equals("Gident", ignoreCase = true) -> VisualUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(),1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                            else -> color
                        })
                }
            }
        }

        RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
            fontRenderer.drawString(displayText, 0F, 0F,
                when {
                    rainbow -> 0
                    colorMode.equals("Fade", ignoreCase = true) -> Palette2.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                    colorMode.equals("Gident", ignoreCase = true) -> VisualUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + counter[0]) / 10)).rgb
                    else -> color
                }, shadow.get())

            if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + 2F,
                    0F,                 when {
                        rainbow -> 0
                        colorMode.equals("Fade", ignoreCase = true) -> Palette2.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                        colorMode.equals("Gident", ignoreCase = true) -> VisualUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + counter[0]) / 10)).rgb
                        else -> color
                    }, shadow.get())
        }

        if (editMode && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            editMode = false
            updateElement()
        }

        return Border(
            -2F,
            -2F,
            fontRenderer.getStringWidth(displayText) + 2F,
            fontRenderer.fontHeight.toFloat()
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ColorUtils.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }

}