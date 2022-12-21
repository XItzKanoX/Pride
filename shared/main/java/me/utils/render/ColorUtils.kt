/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package me.utils.render

import net.ccbluex.liquidbounce.features.module.modules.color.Rainbow
import net.minecraft.util.ChatAllowedCharacters
import java.awt.Color
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ColorUtils2 {

    private val COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]")
    private val startTime = System.currentTimeMillis()

    @JvmField
    val hexColors = IntArray(16)

    init {
        repeat(16) { i ->
            val baseColor = (i shr 3 and 1) * 85

            val red = (i shr 2 and 1) * 170 + baseColor + if (i == 6) 85 else 0
            val green = (i shr 1 and 1) * 170 + baseColor
            val blue = (i and 1) * 170 + baseColor

            hexColors[i] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
        }
    }

    @JvmStatic
    fun stripColor(input: String): String {
        return COLOR_PATTERN.matcher(input).replaceAll("")
    }

    fun mixColors(color1: Color, color2: Color, percent: Float): Color {
        return Color(color1.red + ((color2.red - color1.red) * percent).toInt(), color1.green + ((color2.green - color1.green) * percent).toInt(), color1.blue + ((color2.blue - color1.blue) * percent).toInt(), color1.alpha + ((color2.alpha - color1.alpha) * percent).toInt())
    }
    fun getRainbow(speed: Int, offset: Int): Int {
        var hue = ((System.currentTimeMillis() + offset.toLong()) % speed.toLong()).toFloat()
        return Color.getHSBColor(speed.toFloat().let { hue /= it; hue }, 0.4f, 1.0f).rgb
    }

    @JvmStatic
    fun translateAlternateColorCodes(textToTranslate: String): String {
        val chars = textToTranslate.toCharArray()

        for (i in 0 until chars.size - 1) {
            if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(chars[i + 1], true)) {
                chars[i] = '§'
                chars[i + 1] = Character.toLowerCase(chars[i + 1])
            }
        }

        return String(chars)
    }

    fun randomMagicText(text: String): String {
        val stringBuilder = StringBuilder()
        val allowedCharacters =
            "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"

        for (c in text.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                val index = Random().nextInt(allowedCharacters.length)
                stringBuilder.append(allowedCharacters.toCharArray()[index])
            }
        }

        return stringBuilder.toString()
    }

    @JvmStatic
    fun black(alpha: Int): Int {
        return Color(0, 0, 0, alpha).rgb
    }

    @JvmStatic
    fun white(alpha: Int): Int {
        return Color(255, 255, 255, alpha).rgb
    }

    @JvmStatic
    fun normalRainbow(delay: Int, sat: Float, brg: Float): Int {
        var rainbowState = ceil((System.currentTimeMillis() + delay) / 20.0)
        rainbowState %= 360.0
        return Color.getHSBColor((rainbowState / 360.0f).toFloat(), sat, brg).rgb
    }

    @JvmStatic
    fun skyRainbow2(var2: Int, st: Float, bright: Float): Color? {
        var v1 = ceil((System.currentTimeMillis() + (var2 * 109).toLong()).toDouble()) / 5
        return Color.getHSBColor(if ((360.0.also { v1 %= it } / 360.0).toFloat()
                .toDouble() < 0.5) -(v1 / 360.0).toFloat() else (v1 / 360.0).toFloat(), st, bright)
    }

    @JvmStatic
    fun colorCode(code: String, alpha: Int = 255): Color {
        when (code.toLowerCase()) {
            "0" -> {
                return Color(0, 0, 0, alpha)
            }
            "1" -> {
                return Color(0, 0, 170, alpha)
            }
            "2" -> {
                return Color(0, 170, 0, alpha)
            }
            "3" -> {
                return Color(0, 170, 170, alpha)
            }
            "4" -> {
                return Color(170, 0, 0, alpha)
            }
            "5" -> {
                return Color(170, 0, 170, alpha)
            }
            "6" -> {
                return Color(255, 170, 0, alpha)
            }
            "7" -> {
                return Color(170, 170, 170, alpha)
            }
            "8" -> {
                return Color(85, 85, 85, alpha)
            }
            "9" -> {
                return Color(85, 85, 255, alpha)
            }
            "a" -> {
                return Color(85, 255, 85, alpha)
            }
            "b" -> {
                return Color(85, 255, 255, alpha)
            }
            "c" -> {
                return Color(255, 85, 85, alpha)
            }
            "d" -> {
                return Color(255, 85, 255, alpha)
            }
            "e" -> {
                return Color(255, 255, 85, alpha)
            }
            else -> {
                return Color(255, 255, 255, alpha)
            }
        }
    }

    @JvmStatic
    fun hslRainbow(
        index: Int,
        lowest: Float = Rainbow.rainbowStart.get(),
        bigest: Float = Rainbow.rainbowStop.get(),
        indexOffset: Int = 300,
        timeSplit: Int = Rainbow.rainbowSpeed.get(),
        saturation: Float = Rainbow.rainbowSaturation.get(),
        brightness: Float = Rainbow.rainbowBrightness.get()
    ): Color {
        return Color.getHSBColor(
            (abs(((((System.currentTimeMillis() - startTime).toInt() + index * indexOffset) / timeSplit.toFloat()) % 2) - 1) * (bigest - lowest)) + lowest,
            saturation,
            brightness
        )
    }

    @JvmStatic
    fun rainbow(): Color {
        return hslRainbow(1)
    }

    @JvmStatic
    fun rainbow(index: Int): Color {
        return hslRainbow(index)
    }

    @JvmStatic
    fun rainbow(alpha: Float) = reAlpha(hslRainbow(1), alpha)

    @JvmStatic
    fun rainbowWithAlpha(alpha: Int) = reAlpha(hslRainbow(1), alpha)

    @JvmStatic
    fun rainbow(index: Int, alpha: Int) = reAlpha(hslRainbow(index), alpha)

    @JvmStatic
    fun rainbow(index: Int, alpha: Float) = reAlpha(hslRainbow(index), alpha)

    @JvmStatic
    fun reAlpha(color: Color, alpha: Int): Color {
        return Color(color.red, color.green, color.blue, alpha)
    }

    @JvmStatic
    fun reAlpha(color: Color, alpha: Float): Color {
        return Color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha)
    }

    @JvmStatic
    fun slowlyRainbow(time: Long, count: Int, qd: Float, sq: Float): Color {
        val color = Color(Color.HSBtoRGB((time.toFloat() + count * -3000000f) / 2 / 1.0E9f, qd, sq))
        return Color(color.red / 255.0f * 1, color.green / 255.0f * 1, color.blue / 255.0f * 1, color.alpha / 255.0f)
    }

    @JvmStatic
    fun twoRainbow(offset: Long, alpha: Float): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 8.9999999E10F % 1, 0.75F, 0.8F))
        return Color(
            currentColor.red / 255.0F * 1.0F,
            currentColor.green / 255.0F * 1.0F,
            currentColor.blue / 255.0F * 1.0F,
            alpha
        )
    }

    @JvmStatic
    fun skyRainbow(var2: Int, bright: Float, st: Float, speed: Double): Color {
        var v1 = ceil(System.currentTimeMillis() / speed + var2 * 109L) / 5
        return Color.getHSBColor(if ((360.0.also { v1 %= it } / 360.0) < 0.5) {
            -(v1 / 360.0).toFloat()
        } else {
            (v1 / 360.0).toFloat()
        }, st, bright)
    }

    @JvmStatic
    fun fade(color: Color, index: Int, count: Int): Color {
        val hsb = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsb)
        var brightness =
            abs(((System.currentTimeMillis() % 2000L).toFloat() / 1000.0f + index.toFloat() / count.toFloat() * 2.0f) % 2.0f - 1.0f)
        brightness = 0.5f + 0.5f * brightness
        hsb[2] = brightness % 2.0f
        return Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]))
    }

    @JvmStatic
    fun reverseColor(color: Color) = Color(255 - color.red, 255 - color.green, 255 - color.blue, color.alpha)

    @JvmStatic
    fun healthColor(hp: Float, maxHP: Float, alpha: Int = 255): Color {
        val pct = ((hp / maxHP) * 255F).toInt()
        return Color(max(min(255 - pct, 255), 0), max(min(pct, 255), 0), 0, alpha)
    }

    @JvmStatic
    fun darker(color: Color, percentage: Float): Color {
        return Color(
            (color.red * percentage).toInt(),
            (color.green * percentage).toInt(),
            (color.blue * percentage).toInt(),
            (color.alpha * percentage).toInt()
        )
    }
}
