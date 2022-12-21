/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements


import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.color.Gident
import net.ccbluex.liquidbounce.features.module.modules.render.ESP
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.util.vector.Vector2f
import java.awt.Color
import kotlin.math.*

@ElementInfo(name = "NewRadar")
class Radar(x: Double = 5.0, y: Double = 130.0) : Element(x, y) {

    companion object {
        private val SQRT_OF_TWO = sqrt(2f)
    }

    private val sizeValue = FloatValue("Size", 90f, 30f, 500f)
    private val viewDistanceValue = FloatValue("View Distance", 4F, 0.5F, 32F)

    private val playerShapeValue = ListValue("Player Shape", arrayOf("Rectangle", "Circle"), "Triangle")
    private val playerSizeValue = FloatValue("Player Size", 2.0F, 0.5f, 20F)
    private val fovSizeValue = FloatValue("FOV Size", 10F, 0F, 50F)

    private val exhiValue = BoolValue("Use Exhi Rect", false)
    private val blur = BoolValue("Blur", true)

    private val lineValue = BoolValue("Line", false)
    private val rainbowList = ListValue("Line-Rainbow", arrayOf("Off", "CRainbow", "Sky", "LiquidSlowly", "Fade", "Gident"), "Off")

    private val redValue = IntegerValue("Line-Red", 255, 0, 255)
    private val greenValue = IntegerValue("Line-Green", 255, 0, 255)
    private val blueValue = IntegerValue("Line-Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Line-Alpha", 255, 0, 255)
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val cRainbowSecValue = IntegerValue("Seconds", 2, 1, 10)
    private val distanceValue = IntegerValue("Line-Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)

    private val backgroundRedValue = IntegerValue("Background Red", 0, 0, 255)
    private val backgroundGreenValue = IntegerValue("Background Green", 0, 0, 255)
    private val backgroundBlueValue = IntegerValue("Background Blue", 0, 0, 255)
    private val backgroundAlphaValue = IntegerValue("Background Alpha", 50, 0, 255)

    private val borderValue = BoolValue("Border", false)

    private val borderStrengthValue = FloatValue("Border Strength", 2F, 1F, 5F)
    private val borderRedValue = IntegerValue("Border Red", 0, 0, 255)
    private val borderGreenValue = IntegerValue("Border Green", 0, 0, 255)
    private val borderBlueValue = IntegerValue("Border Blue", 0, 0, 255)
    private val borderAlphaValue = IntegerValue("Border Alpha", 150, 0, 255)
    private val shadowValue = BoolValue("Shadow", true)
    override fun drawElement(): Border? {
        val renderViewEntity = mc.renderViewEntity

        val size = sizeValue.get()

        val viewDistance = viewDistanceValue.get() * 16.0F

        val maxDisplayableDistanceSquare = ((viewDistance + fovSizeValue.get().toDouble()) *
                (viewDistance + fovSizeValue.get().toDouble()))
        val halfSize = size / 2f

        val rainbowType = rainbowList.get()

        val cColor = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()).rgb


        //blur
        if (blur.get()){
            glTranslated(-renderX, -renderY, 0.0)
            BlurBuffer.blurArea(renderX.toFloat(), renderY.toFloat(), size, size  )
            BlurBuffer.blurArea(renderX.toFloat(), renderY.toFloat(), size, size  )
            glTranslated(renderX, renderY, 0.0)

        }

        if (exhiValue.get()) 
            RenderUtils.drawExhiRect(0F, if (lineValue.get()) -1F else 0F, size, size)
        else
            RenderUtils.drawRect(0F, 0F, size, size, Color(backgroundRedValue.get(), backgroundGreenValue.get(),
                    backgroundBlueValue.get(), backgroundAlphaValue.get()).rgb)

        if(shadowValue.get()) {
            RenderUtils.drawShadowWithCustomAlpha(0F, 0F, size, size, 255F)
        }

        if (lineValue.get()) {
            val barLength = size.toDouble()

            for (i in 0..(gradientAmountValue.get()-1)) {
                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * barLength
                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * barLength
                RenderUtils.drawGradientSideways(barStart, -1.0, barEnd, 0.0, 
                when (rainbowType) {
                    "CRainbow" -> RenderUtils.getRainbowOpaque(cRainbowSecValue.get(), saturationValue.get(), brightnessValue.get(), i * distanceValue.get())
                    "Sky" -> RenderUtils.SkyRainbow(i * distanceValue.get(), saturationValue.get(), brightnessValue.get())
                    "LiquidSlowly" -> ColorUtils.LiquidSlowly(System.nanoTime(), i * distanceValue.get(), saturationValue.get(), brightnessValue.get())!!.rgb
                    "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), 100).rgb
                    "Gident" -> VisualUtils.getGradientOffset(                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                        Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(),1), (Math.abs(System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                    else -> cColor
                },
                when (rainbowType) {
                    "CRainbow" -> RenderUtils.getRainbowOpaque(cRainbowSecValue.get(), saturationValue.get(), brightnessValue.get(), (i + 1) * distanceValue.get())
                    "Sky" -> RenderUtils.SkyRainbow((i + 1) * distanceValue.get(), saturationValue.get(), brightnessValue.get())
                    "LiquidSlowly" -> ColorUtils.LiquidSlowly(System.nanoTime(), (i + 1) * distanceValue.get(), saturationValue.get(), brightnessValue.get())!!.rgb
                    "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), (i + 1) * distanceValue.get(), 100).rgb
                    "Gident" -> VisualUtils.getGradientOffset(                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                        Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(),1), (Math.abs(System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                    else -> cColor
                })
            }
        }
                    
        // border section
        if (borderValue.get()) {
            val strength = borderStrengthValue.get() / 2F
            val borderColor = Color(borderRedValue.get(), borderGreenValue.get(), borderBlueValue.get(), borderAlphaValue.get()).rgb

            // vertical
            RenderUtils.drawRect(halfSize - strength, 0F, halfSize + strength, size, borderColor)

            // horizontal
            RenderUtils.drawRect(0F, halfSize - strength, halfSize - strength, halfSize + strength, borderColor)
            RenderUtils.drawRect(halfSize + strength, halfSize - strength, size, halfSize + strength, borderColor)

            glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        }
        // end

        RenderUtils.makeScissorBox(x.toFloat(), y.toFloat(), x.toFloat() + ceil(size), y.toFloat() + ceil(size))

        glEnable(GL_SCISSOR_TEST)

        glPushMatrix()

        glTranslatef(halfSize, halfSize, 0f)
        glRotatef(renderViewEntity!!.rotationYaw, 0f, 0f, -1f)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        glDisable(GL_TEXTURE_2D)
        glEnable(GL_LINE_SMOOTH)

        val circleMode = playerShapeValue.get().equals("circle", true)

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.buffer

        if (circleMode)
            glEnable(GL_POINT_SMOOTH)

        var playerSize = playerSizeValue.get()

        glEnable(GL_POLYGON_SMOOTH)

        worldRenderer.begin(GL_POINTS, DefaultVertexFormats.POSITION_COLOR)
        glPointSize(playerSize)

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (entity != null && entity !== mc.thePlayer && EntityUtils.isSelected(entity, false)) {
                val positionRelativeToPlayer = Vector2f((renderViewEntity.posX - entity.posX).toFloat(),
                        (renderViewEntity.posZ - entity.posZ).toFloat())

                if (maxDisplayableDistanceSquare < positionRelativeToPlayer.lengthSquared())
                    continue

                val transform = fovSizeValue.get() > 0F

                if (transform) {
                    glPushMatrix()

                    glTranslatef((positionRelativeToPlayer.x / viewDistance) * size,
                            (positionRelativeToPlayer.y / viewDistance) * size, 0f)
                    glRotatef(entity.rotationYaw, 0f, 0f, 1f)
                }

                val color = (LiquidBounce.moduleManager[ESP::class.java] as ESP).getColor(entity)

                worldRenderer.pos(((positionRelativeToPlayer.x / viewDistance) * size).toDouble(),
                        ((positionRelativeToPlayer.y / viewDistance) * size).toDouble(), 0.0)
                        .color(color.red / 255.0f, color.green / 255.0f,
                                color.blue / 255.0f, 1.0f).endVertex()
                

                if (transform)
                    glPopMatrix()
            }
        }

        tessellator.draw()

        if (circleMode) {
            glDisable(GL_POINT_SMOOTH)
        }

        glDisable(GL_POLYGON_SMOOTH)

        glEnable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)

        glDisable(GL_SCISSOR_TEST)

        glPopMatrix()

        return Border(0F, 0F, size, size)
    }

}