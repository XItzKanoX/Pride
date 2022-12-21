package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.max
import kotlin.math.pow

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications")
class Notifications(
    x: Double = 0.0,
    y: Double = 0.0,
    scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

//    private var fontValue = FontValue("Font", Fonts.jello35)
    /**
     * Example notification for CustomHUD designer
     */

    private val fontValue = FontValue("Font", Fonts.bold35)
    private val blur = BoolValue("Blur", false)
    private val exampleNotification = Notification("Notification",
        "This is an example notification.",
        NotifyType.INFO
    )
    /**
     * Draw element
     */
    override fun drawElement(): Border? {

        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()
        val notifications = mutableListOf<Notification>()

        //FUCK YOU java.util.ConcurrentModificationException
        for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
            GL11.glPushMatrix()

            if (notify.drawNotification(index, fontValue.get(),  blur.get(), renderX.toFloat(), renderY.toFloat(), scale.toFloat())) {
                notifications.add(notify)
            }

            GL11.glPopMatrix()
        }
        for (notify in notifications) {
            LiquidBounce.hud.notifications.remove(notify)
        }

        if (classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F)
        }

        return null
    }

}


class Notification(

    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    val animeTime: Int = 500
) {
    val width =  100.coerceAtLeast(
        Fonts.fontSFUI35.getStringWidth(this.title)
            .coerceAtLeast(Fonts.fontSFUI35.getStringWidth(this.content)) + 10
    )
    //    val width=30 + Fonts.font35.getStringWidth("${this.title} ${this.content}")
    val height=30

    var fadeState = FadeState.IN
    var nowY = -height
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(index: Int, font: IFontRenderer, blur: Boolean, x: Float, y: Float, scale: Float): Boolean {

        val realY = -(index + 1) * height
        val nowTime = System.currentTimeMillis()
        val image = MinecraftInstance.classProvider.createResourceLocation("pride/notification/" + type.name + ".png")





        //Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = easeOutBack(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime
        }
        GL11.glTranslated(0.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = easeOutBack(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - easeInBack(pct)
            }

            FadeState.END -> {
                return true
            }
        }

        val originalX =x
        val originalY =y
        val transX = width - (width * pct) - width
        var transY = nowY.toDouble()
        GL11.glTranslatef(-width.toFloat(), 0F, 0F)
        GL11.glTranslated(width - (width * pct), 0.0, 0.0)
        if (blur) {
            GL11.glTranslatef(-width.toFloat() - 183, 0F, 0F)
            GL11.glTranslated(width - (width * pct) +281, 0.0, 0.0)
            GL11.glTranslatef(-originalX + 121, -originalY  , 0F)
            GL11.glPushMatrix()
            BlurBuffer.blurAreacustomradius(x - 122, y, width + 22F, height.toFloat(), 7F)
            GL11.glPopMatrix()
            GL11.glTranslatef(originalX - 100, originalY+ 1, 0F)
        }
//        GL11.glTranslated(width - (width * pct), 0.0, 0.0)
//        GL11.glTranslatef(-width.toFloat(), 0F, 0F)



        //Draw Noti
//        blur.draw(0f,-1f,width.toFloat(),height.toFloat(),1f)
//        Fonts.fontSFUI35.drawStringWithShadow(  BigDecimal(((time - time * ((nowTime - displayTime) / (animeTime * 2F + time))) / 1000).toDouble()).setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "s",
//            -19F.toInt(),  3F.toInt(), Color.WHITE.rgb)

//                    BlurBuffer.blurArea(width.toFloat(), height.toFloat(), 1111F, 1111F    )
        RenderUtils.drawShadowWithCustomAlpha(-22F, 0F, width + 22F, height.toFloat(), 200F)
//        RenderUtils.drawGradientSideways(-22.0, 0.0, width.toDouble(), height.toDouble(),  Color(0, 0, 0, 200).rgb, Color( 255, 255, 255, 100).rgb)
//        RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(0, 0, 0, 150))
        RenderUtils.drawRect(
            -22.0f,
            0.0f,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22.0F),
            height.toFloat(),
            Color(255, 255, 255, 60) )
        font.drawString(title, 7F, 4F, -1, true)
        font.drawString(content, 7F, 17F, -1, true)
        RenderUtils.drawImage(image, -19,  3, 22, 22)
//        MinecraftInstance.classProvider.getGlStateManager().resetColor()
//        RenderUtils.drawRect(0F,0F, width.toFloat(),height.toFloat(),Color(0,0,0, 170).rgb)
//        RenderUtils.drawRect(0F,height-0.5F, max(width*((nowTime-displayTime)/(animeTime*2F+time)),0F),height.toFloat(),type.renderColor.rgb)
//        RenderUtils.drawShadowWithCustomAlpha(0F, 0F, width.toFloat().toInt().toFloat(),
//            height.toFloat().toInt().toFloat(),255F )
//        RenderUtils.drawImage(image,6,6,10,10)
//
//        val ColorTxet = when(type.name) {
//            "SUCCESS","ERROR" -> Color(155,155,155).rgb
//            else -> Color(230,230,230).rgb
//        }
//        Fonts.font35.drawString(title, 22F, height / 2F - 1F, Color.WHITE.rgb, false)
//        Fonts.font35.drawString(content, 24F + Fonts.font35.getStringWidth(title), height / 2F - 1F, ColorTxet, false)

        return false
    }
}
fun easeInBack(x: Double): Double {
    val c1 = 1.70158
    val c3 = c1 + 1

    return c3 * x * x * x - c1 * x * x
}

fun easeOutBack(x: Double): Double {
    val c1 = 1.70158
    val c3 = c1 + 1

    return 1 + c3 * (x - 1).pow(3) + c1 * (x - 1).pow(2)
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0x60E066)),
    ERROR(Color(0xFF2F3A)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(0x6490A7));
}
enum class FadeState { IN, STAY, OUT, END }
