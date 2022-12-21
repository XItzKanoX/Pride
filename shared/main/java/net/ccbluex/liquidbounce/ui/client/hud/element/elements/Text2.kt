/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.utils.render.VisualUtils.skyRainbow
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CPSCounter
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.extensions.getPing
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
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
@ElementInfo(name = "Text2")
class Text2(
    x: Double = 10.0,
    y: Double = 10.0,
    scale: Float = 1F,
    side: Side = Side.default()
) : Element(x, y, scale, side) {

    companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("#.##")
    }

    val displayString = TextValue("DisplayText", "")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "AnotherRainbow", "SkyRainbow"), "Custom")
    private val shadow = BoolValue("Shadow", false)
    private val rectRedValue = IntegerValue("RectRed", 0, 0, 255)
    private val rectGreenValue = IntegerValue("RectGreen", 0, 0, 255)
    private val rectBlueValue = IntegerValue("RectBlue", 0, 0, 255)
    private val rectAlphaValue = IntegerValue("RectAlpha", 255, 0, 255)
    val rectColorModeValue = ListValue("RectColor", arrayOf("Custom", "Rainbow", "AnotherRainbow", "SkyRainbow"), "Custom")
    val rectValue = ListValue("Rect", arrayOf("Normal", "RNormal", "OneTap", "Skeet", "None"), "None")
    private val rectExpandValue = FloatValue("RectExpand", 0.3F, 0F, 1F)
    private val rainbowSpeed = IntegerValue("RainbowSpeed", 10, 1, 10)
    private val rainbowIndex = IntegerValue("RainbowIndex", 1, 1, 20)
    private val fontValue = FontValue("Font", Fonts.font40)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode) {
                "Text Element"
            } else {
                displayString.get()
            }

            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        if (mc.thePlayer!! != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posX)
                "y" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posZ)
                "xdp" -> return mc.thePlayer!!.posX.toString()
                "ydp" -> return mc.thePlayer!!.posY.toString()
                "zdp" -> return mc.thePlayer!!.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ))
                "ping" -> return mc.thePlayer!!.getPing().toString()
            }
        }

        return when (str) {
            "clientName" -> LiquidBounce.CLIENT_NAME
            "clientVersion" -> LiquidBounce.CLIENT_VERSION.toString()
            "clientCreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverIp" -> ServerUtils.getRemoteIp()
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
    fun getClientName(i: Int,i2: Int): String{
        return "SadNess".substring(i,i2);
    }
    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get())

        val fontRenderer = fontValue.get()

        val rectColor = when (rectColorModeValue.get().toLowerCase()) {
            "rainbow" -> ColorUtils.hslRainbow(rainbowIndex.get(), indexOffset = 100 * rainbowSpeed.get()).rgb
            "skyrainbow" -> RenderUtils.SkyRainbow(rainbowIndex.get(), 1F, 1F)
            "anotherrainbow" -> ColorUtils.fade(Color(rectRedValue.get(), rectGreenValue.get(), rectBlueValue.get(), rectAlphaValue.get()), 100, rainbowIndex.get()).rgb
            else -> Color(rectRedValue.get(), rectGreenValue.get(), rectBlueValue.get(), rectAlphaValue.get()).rgb
        }
        val expand = fontRenderer.fontHeight * rectExpandValue.get()
        when (rectValue.get().toLowerCase()) {
            "normal" -> {
                RenderUtils.drawRect(-expand, -expand, fontRenderer.getStringWidth(displayText) + expand, fontRenderer.fontHeight + expand, rectColor)
            }
            "rnormal" -> {
                RenderUtils.drawRect(-expand, -expand - 1, fontRenderer.getStringWidth(displayText) + expand, -expand, ColorUtils.rainbow())
                RenderUtils.drawRect(-expand, -expand, fontRenderer.getStringWidth(displayText) + expand, fontRenderer.fontHeight + expand, rectColor)
            }
            "onetap" -> {
                RenderUtils.drawRect(-4.0f, -8.0f, (fontRenderer.getStringWidth(displayText) + 3).toFloat(), fontRenderer.fontHeight.toFloat(), Color(43, 43, 43).rgb)
                RenderUtils.drawGradientSidewaysH(-3.0, -7.0, fontRenderer.getStringWidth(displayText) + 2.0, -3.0, Color(rectColor).darker().rgb, rectColor)
            }
            "skeet" -> {
                RenderUtils.drawRect(-11, -11, (fontRenderer.getStringWidth(displayText) + 10), fontRenderer.fontHeight + 8, Color(0, 0, 0).rgb)
                RenderUtils.drawOutlinedRect(-10, -10, (fontRenderer.getStringWidth(displayText) + 9), fontRenderer.fontHeight + 7, 8, Color(59, 59, 59), Color(59, 59, 59))
                RenderUtils.drawOutlinedRect(-9, -9, (fontRenderer.getStringWidth(displayText) + 8), fontRenderer.fontHeight + 6, 4, Color(59, 59, 59), Color(40, 40, 40))
                RenderUtils.drawOutlinedRect(-4, -4, (fontRenderer.getStringWidth(displayText) + 3), fontRenderer.fontHeight + 1, 1, Color(18, 18, 18), Color(0, 0, 0))
            }
            "logo" -> {

            }
        }
        if(!rectValue.get().contains("Logo")) {
            fontRenderer.drawString(
                displayText, 0F, 0F, when (colorModeValue.get().toLowerCase()) {
                    "rainbow" -> ColorUtils.hslRainbow(rainbowIndex.get(), indexOffset = 100 * rainbowSpeed.get()).rgb
                    "skyrainbow" -> RenderUtils.SkyRainbow(rainbowIndex.get(), 1F, 1F)
                    "anotherrainbow" -> ColorUtils.fade(color, 100, rainbowIndex.get()).rgb
                    else -> color.rgb
                }, shadow.get()
            )
        }else{
            Fonts.font40.drawString(

                getClientName(0,3), 5.0f, 0.0f,Color(255,255,255,220).rgb
            )
            Fonts.font25.drawString(
                "CN", 5F + Fonts.font40.getStringWidth("Pride"), 13.0f,Color(255,255,255,220).rgb
            )
            RenderUtils.drawRect(5f,22.5f,70f,22.8f,Color(200,200,200,220).rgb)
            Fonts.font25.drawString(
                LiquidBounce.CLIENT_VERSION.toString(), 5.0f, 27.0f,Color(255,255,255,220).rgb
            )
            Fonts.font25.drawString(
                "By Happy & Jiemo", 5.0f, 37.0f,Color(255,255,255,220).rgb
            )
        }

        if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40) {
            fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + 2F,
                0F, Color.WHITE.rgb, shadow.get())
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
            if (System.currentTimeMillis() - prevClick <= 250L) {
                editMode = true
            }

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

    fun setColor(c: Color): Text2 {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }
}
