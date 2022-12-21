/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import me.utils.PacketUtils
import me.utils.player.PlayerUtil.isMoving
import me.utils.render.ColorUtils2
import me.utils.render.GLUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketUseEntity
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.IMovingObjectPosition
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.world.IWorldSettings
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.color.Gident
import net.ccbluex.liquidbounce.features.module.modules.color.Rainbow
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.features.module.modules.render.OldHitting
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.injection.backend.CPacketPlayerBlockPlacementImpl
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer.Companion.getColorIndex
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.extensions.isAnimal
import net.ccbluex.liquidbounce.utils.extensions.isClientFriend
import net.ccbluex.liquidbounce.utils.extensions.isMob
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils.LiquidSlowly
import net.ccbluex.liquidbounce.utils.render.ColorUtils.fade
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import java.awt.Color
import java.awt.Robot
import java.awt.event.InputEvent
import java.util.*
import kotlin.math.*

@ModuleInfo(name = "KillAura", description = "Automatically attacks targets around you.",
    category = ModuleCategory.COMBAT, keyBind = Keyboard.KEY_R)
class KillAura : Module() {

    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val cooldownValue = FloatValue("Cooldown", 1f, 0f, 1f)

    // Range
    private val rangeValue = FloatValue("Range", 3.7f, 1f, 8f)
    private val throughWallsRangeValue = FloatValue("ThroughWallsRange", 3f, 0f, 8f)
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0f, 0f, 0.4f)

    // Modes
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime", "HurtResitanTime"), "Distance")
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")

    // Bypass
    private val swingValue = BoolValue("Swing", true)
    private val keepSprintValue = BoolValue("KeepSprint", true)
    private val stopSprintAir = BoolValue("StopSprintOnAir",true)

    // AutoBlock

    private val autoBlockValue = ListValue("AutoBlock", arrayOf("AllTime","Range","Off"),"Off")
    private val BlockRangeValue = FloatValue("BlockRange", 3f, 0f, 8f)

    private val autoBlockPacketValue = ListValue("AutoBlockPacket", arrayOf("Vanilla",  "Fake", "Mouse", "GameSettings", "UseItem"),"Simple")
    private  val vanillamode =  ListValue("VanillaMode", arrayOf("TryUseItem", "UseItem", "C08", "OldC08"), "TryUseItem")
    private val interactAutoBlockValue = BoolValue("InteractAutoBlock", true)
    private val delayedBlockValue = BoolValue("AutoBlock-AfterTck", false)
    private val afterAttackValue = BoolValue("AutoBlock-AfterAttack", false)
    private val autoBlockFacing = BoolValue("AutoBlockFacing",false)
    private val blockRate = IntegerValue("BlockRate", 100, 1, 100)

    // Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false)
    private val livingRaycastValue = BoolValue("LivingRayCast", true)

    // Bypass
    private val aacValue = BoolValue("AAC", false)

    // Turn Speed
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    // Lighting
    private val lightingValue = BoolValue("Lighting", false)
    private val lightingModeValue = ListValue("Lighting-Mode", arrayOf("Dead", "Attack"), "Dead")
    private val lightingSoundValue = BoolValue("Lighting-Sound", true)
    private val randomCenterValue = BoolValue("RandomCenter", true)
    private val rotations = ListValue("RotationMode", arrayOf("None", "New", "Liquidbounce","BackTrack", "Test1", "Test2", "HytRotation"), "New")
    private val outborderValue = BoolValue("Outborder", false)
    private val silentRotationValue = BoolValue("SilentRotation", true)
    private val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Off")
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)
    private val hitableValue = BoolValue("AlwaysHitable",true)
    // Predict
    private val switchDelayValue = IntegerValue("SwitchDelay",300 ,1, 2000)
    private val predictValue = BoolValue("Predict", true)

    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }
    }

    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }

    // Bypass
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BoolValue("FakeSwing", true)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500)
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50)

    // Visuals
    private val markValue = ListValue("Mark", arrayOf("Liquid","FDP","Block","Jello", "Plat", "Red", "Sims", "None"),"FDP")
    private val colorModeValue =
        ListValue("JelloColor", arrayOf("Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Health", "Gident"), "Custom")
    private val colorRedValue = IntegerValue("JelloRed", 255, 0, 255)
    private val colorGreenValue = IntegerValue("JelloGreen", 255, 0, 255)
    private val colorBlueValue = IntegerValue("JelloBlue", 255, 0, 255)

    private val colorAlphaValue = IntegerValue("JelloAlpha", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 1f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)

    private val colorTeam = BoolValue("JelloTeam", false)

    private val jelloAlphaValue =
        FloatValue("JelloEndAlphaPercent", 0.4f, 0f, 1f)
    private val jelloWidthValue =
        FloatValue("JelloCircleWidth", 3f, 0.01f, 5f)
    private val jelloGradientHeightValue =
        FloatValue("JelloGradientHeight", 3f, 1f, 8f)
    private val jelloFadeSpeedValue =
        FloatValue("JelloFadeSpeed", 0.1f, 0.01f, 0.5f)
    private val fakeSharpValue = BoolValue("FakeSharp", true)
    private val circleValue=BoolValue("Circle",true)
    private val circleRed = IntegerValue("CircleRed", 255, 0, 255)
    private val circleGreen = IntegerValue("CircleGreen", 255, 0, 255)
    private val circleBlue = IntegerValue("CircleBlue", 255, 0, 255)
    private val circleAlpha = IntegerValue("CircleAlpha", 255, 0, 255)
    private val circleAccuracy = IntegerValue("CircleAccuracy", 15, 0, 60)
    /**
     * MODULE
     */

    val blockKey: Int = mc.gameSettings.keyBindUseItem.keyCode
    private val switchTimer = MSTimer()
    // Target
    var target: IEntityLivingBase? = null
    private var currentTarget: IEntityLivingBase? = null
    private var hitable = false

    private val prevTargetEntities = mutableListOf<Int>()
    private var lastTarget: IEntityLivingBase? = null
    private var direction = 1.0
    private var yPos = 0.0
    private  var progress:kotlin.Double = 0.0
    private var lastMS = System.currentTimeMillis()
    private var lastDeltaMS = 0L
    private var al = 0f
    // Attack delay
    private val attackTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0
    private var markEntity: EntityLivingBase? = null
    private val markTimer = MSTimer()

    // Container Delay
    private var containerOpen = -1L
    private var displayText: String = ""
    // Fake block status
    private var bb: IAxisAlignedBB? = null
    private var entity: IEntityLivingBase? = null
    var blockingStatus = false
    private var espAnimation = 0.0
    private var isUp = true
    var syncEntity: IEntityLivingBase? = null

    companion object {
        @JvmStatic
        var killCounts = 0
    }

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        lastTarget = null
        hitable = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (this.stopSprintAir.get()) {
            if (mc.thePlayer!!.onGround) {
                this.keepSprintValue.set(true)
            } else {
                this.keepSprintValue.set(false)
            }
        }
        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            // AutoBlock
            if (!autoBlockValue.get().equals("off",true) && delayedBlockValue.get()  && canBlock())
                startBlocking(currentTarget!!, interactAutoBlockValue.get())

            return

        }

        if (rotationStrafeValue.get().equals("Off", true))
            update()
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (rotationStrafeValue.get().equals("Off", true))
            return

        update()

        if (currentTarget != null && RotationUtils.targetRotation != null) {
            when (rotationStrafeValue.get().toLowerCase()) {
                "strict" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = sqrt(f)

                        if (f < 1.0F)
                            f = 1.0F

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = cos((yaw * Math.PI / 180F).toFloat())

                        val player = mc.thePlayer!!

                        player.motionX += strafe * yawCos - forward * yawSin
                        player.motionZ += forward * yawCos + strafe * yawSin
                    }
                    event.cancelEvent()
                }
                "silent" -> {
                    update()

                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
                    event.cancelEvent()
                }
            }
        }
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())))
            return
        // Update target
        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }



        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }

    @EventTarget
    fun onTick(event: TickEvent?) {
        if (markValue.get().equals("jello", ignoreCase = true)
        ) al = AnimationUtils.changer(
            al,
            if (target != null) jelloFadeSpeedValue.get() else -jelloFadeSpeedValue.get(),
            0f,
            colorAlphaValue.get() / 255.0f
        )
    }
    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (lightingValue.get()) {
            when (lightingModeValue.get().toLowerCase()) {
                "dead" -> {
                    if (target != null) {
                        lastTarget = if (lastTarget == null) {
                            target
                        } else {
                            if (lastTarget!!.health <= 0) {
                                mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                                    lastTarget!!.posX, lastTarget!!.posY, lastTarget!!.posZ, true)))
                                if (lightingSoundValue.get()) mc    .soundHandler.playSound("entity.lightning.impact", 0.5f)
                            } //ambient.weather.thunder
                            target
                        }
                    } else {
                        if (lastTarget != null && lastTarget!!.health <= 0) {
                            mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                                lastTarget!!.posX, lastTarget!!.posY, lastTarget!!.posZ, true)))
                            if (lightingSoundValue.get()) mc.soundHandler.playSound("entity.lightning.impact", 0.5f)
                            lastTarget = target
                        }
                    }
                }

                "attack" -> {
                    mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                        target!!.posX, target!!.posY, target!!.posZ, true)))
                    if (lightingSoundValue.get()) mc.soundHandler.playSound("entity.lightning.impact", 0.5f)
                }
            }
        }

        if (syncEntity != null && syncEntity!!.isDead) {
            ++killCounts
            syncEntity = null
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }


        if (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            currentTarget = null
            hitable = false
            if (classProvider.isGuiContainer(mc.currentScreen)) containerOpen = System.currentTimeMillis()
            return
        }


        if (target != null && currentTarget != null && (Backend.MINECRAFT_VERSION_MINOR == 8 || mc.thePlayer!!.getCooledAttackStrength(0.0F) >= cooldownValue.get())) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }



    private fun esp(entity : IEntityLivingBase, partialTicks : Float, radius : Float) {
        GL11.glPushMatrix()
        GL11.glDisable(3553)
        GLUtils.startSmooth()
        GL11.glDisable(2929)
        GL11.glDepthMask(false)
        GL11.glLineWidth(1.0F)
        GL11.glBegin(3)
        val x: Double = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
        val y: Double = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
        val z: Double = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ
        for (i in 0..360) {
            val rainbow = Color(Color.HSBtoRGB((mc.thePlayer!!.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
            GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
            GL11.glVertex3d(x + radius * cos(i * 6.283185307179586 / 45.0), y + espAnimation, z + radius * sin(i * 6.283185307179586 / 45.0))
        }
        GL11.glEnd()
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        GLUtils.endSmooth()
        GL11.glEnable(3553)
        GL11.glPopMatrix()
    }

    private fun drawESP(entity: IEntityLivingBase, color: Int, e: Render3DEvent) {
        val x: Double =
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.partialTicks.toDouble() - mc.renderManager.renderPosX
        val y: Double =
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.partialTicks.toDouble() - mc.renderManager.renderPosY
        val z: Double =
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.partialTicks.toDouble() - mc.renderManager.renderPosZ
        val radius = 0.15f
        val side = 4
        GL11.glPushMatrix()
        GL11.glTranslated(x, y + 2, z)
        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f)
        RenderUtils.glColor1(color)
        RenderUtils.enableSmoothLine(1.5F)
        val c = Cylinder()
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
        c.drawStyle = 100012
        RenderUtils.glColor(Color(80,255,80,200))
        c.draw(0F, radius, 0.3f, side, 1)
        c.drawStyle = 100012
        GL11.glTranslated(0.0, 0.0, 0.3)
        c.draw(radius, 0f, 0.3f, side, 1)
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
        c.drawStyle = 100011
        GL11.glTranslated(0.0, 0.0, -0.3)
        RenderUtils.glColor1(color)
        c.draw(0F, radius, 0.3f, side, 1)
        c.drawStyle = 100011
        GL11.glTranslated(0.0, 0.0, 0.3)
        c.draw(radius, 0F, 0.3f, side, 1)
        RenderUtils.disableSmoothLine()
        GL11.glPopMatrix()
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        fun post3D() {
            GL11.glDepthMask(true)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glPopMatrix()
            GL11.glColor4f(1f, 1f, 1f, 1f)
        }

        fun drawCircle(
            x: Double,
            y: Double,
            z: Double,
            width: Float,
            radius: Double,
            red: Float,
            green: Float,
            blue: Float,
            alp: Float
        ) {
            GL11.glLineWidth(width)
            GL11.glBegin(GL11.GL_LINE_LOOP)
            GL11.glColor4f(red, green, blue, alp)
            var i = 0
            while (i <= 360) {
                val posX = x - Math.sin(i * Math.PI / 180) * radius
                val posZ = z + Math.cos(i * Math.PI / 180) * radius
                GL11.glVertex3d(posX, y, posZ)
                i += 1
            }
            GL11.glEnd()
        }
        fun pre3D() {
            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LIGHTING)
            GL11.glDepthMask(false)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
            GL11.glDisable(2884)
        }
        fun getColor(ent: IEntityLivingBase?): Color? {
            val counter = intArrayOf(0)
            if (ent is EntityLivingBase) {
                val entityLivingBase = ent

                if (colorModeValue.get().equals("Health", ignoreCase = true)) return BlendUtils.getHealthColor(
                    entityLivingBase.health,
                    entityLivingBase.maxHealth
                )
                if (colorTeam.get()) {

                    val chars = entityLivingBase.displayName!!.formattedText.toCharArray()
                    var color = Int.MAX_VALUE
                    for (i in chars.indices) {
                        if (chars[i] != '§' || i + 1 >= chars.size) continue
                        val index = getColorIndex(chars[i + 1])
                        if (index < 0 || index > 15) continue
                        color = ColorUtils.hexColors[index]
                        break
                    }
                    return Color(color)
                }
            }
            return when (colorModeValue.get()) {
                "Gident" -> RenderUtils.getGradientOffset(Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                    Color (Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get()),   (Math.abs(
                        System.currentTimeMillis() / Gident.gidentspeed.get().toDouble()
                    )))

                "Custom" -> Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
                "Rainbow" -> ColorUtils2.hslRainbow(counter[0] * 100 + 1, indexOffset = 100 * Rainbow.rainbowSpeed.get())

                "Sky" -> RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
                "LiquidSlowly" -> LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())
                else -> fade(Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100)
            }
        }
        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer!!.lastTickPosX + (mc.thePlayer!!.posX - mc.thePlayer!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer!!.lastTickPosY + (mc.thePlayer!!.posY - mc.thePlayer!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer!!.lastTickPosZ + (mc.thePlayer!!.posZ - mc.thePlayer!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(1F)
            GL11.glColor4f(circleRed.get().toFloat() / 255.0F, circleGreen.get().toFloat() / 255.0F, circleBlue.get().toFloat() / 255.0F, circleAlpha.get().toFloat() / 255.0F)
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 61 - circleAccuracy.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(), (sin(i * Math.PI / 180.0).toFloat() * rangeValue.get()))
            }
            GL11.glVertex2f(cos(360 * Math.PI / 180.0).toFloat() * rangeValue.get(), (sin(360 * Math.PI / 180.0).toFloat() * rangeValue.get()))

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            currentTarget = null
            hitable = false
            if (classProvider.isGuiContainer(mc.currentScreen)) containerOpen = System.currentTimeMillis()
            return
        }

        target ?: return

        when (markValue.get().toLowerCase()) {
            "liquid" -> {
                RenderUtils.drawPlatform(target!!, if (target!!.hurtTime <= 0) Color(37, 126, 255, 170) else Color(255, 0, 0, 170))
            }
            "plat" -> RenderUtils.drawPlatform(
                target!!,
                if (hitable) Color(37, 126, 255, 70) else Color(255, 0, 0, 70)
            )
            "block" -> {
                val bb = target!!.entityBoundingBox
                target!!.entityBoundingBox = bb.expand(0.2, 0.2, 0.2)
                RenderUtils.drawEntityBox(target!!, if (target!!.hurtTime <= 0) Color.GREEN else Color.RED, true)
                target!!.entityBoundingBox = bb
            }
            "red" -> {
                RenderUtils.drawPlatform(target!!, if (target!!.hurtTime <= 0) Color(255, 255, 255, 255) else Color(124, 215, 255, 255))
            }
            "sims" -> {
                val radius = 0.15f
                val side = 4
                GL11.glPushMatrix()
                GL11.glTranslated(
                    target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX,
                    (target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + target!!.height * 1.1,
                    target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                )
                GL11.glRotatef(-target!!.width, 0.0f, 1.0f, 0.0f)
                GL11.glRotatef((mc.thePlayer!!.ticksExisted + mc.timer.renderPartialTicks) * 5, 0f, 1f, 0f)
                RenderUtils.glColor(if (target!!.hurtTime <= 0) Color(80, 255, 80) else Color(255, 0, 0))
                RenderUtils.enableSmoothLine(1.5F)
                val c = Cylinder()
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                c.draw(0F, radius, 0.3f, side, 1)
                c.drawStyle = 100012
                GL11.glTranslated(0.0, 0.0, 0.3)
                c.draw(radius, 0f, 0.3f, side, 1)
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
                GL11.glTranslated(0.0, 0.0, -0.3)
                c.draw(0F, radius, 0.3f, side, 1)
                GL11.glTranslated(0.0, 0.0, 0.3)
                c.draw(radius, 0F, 0.3f, side, 1)
                RenderUtils.disableSmoothLine()
                GL11.glPopMatrix()
            }
            "fdp" -> {
                val drawTime = (System.currentTimeMillis() % 1500).toInt()
                val drawMode = drawTime > 750
                var drawPercent = drawTime / 750.0
                //true when goes up
                if (!drawMode) {
                    drawPercent = 1 - drawPercent
                } else {
                    drawPercent -= 1
                }
                drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                GL11.glPushMatrix()
                GL11.glDisable(3553)
                GL11.glEnable(2848)
                GL11.glEnable(2881)
                GL11.glEnable(2832)
                GL11.glEnable(3042)
                GL11.glBlendFunc(770, 771)
                GL11.glHint(3154, 4354)
                GL11.glHint(3155, 4354)
                GL11.glHint(3153, 4354)
                GL11.glDisable(2929)
                GL11.glDepthMask(false)

                val bb = target!!.entityBoundingBox
                val radius = (bb.maxX - bb.minX) + 0.3
                val height = bb.maxY - bb.minY
                val x = target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX
                val y = (target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + height * drawPercent
                val z = target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                GL11.glLineWidth((radius * 5f).toFloat())
                GL11.glBegin(3)
                for (i in 0..360) {
                    val rainbow = Color(Color.HSBtoRGB((mc.thePlayer!!.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
                    GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
                    GL11.glVertex3d(x + radius * cos(i * 6.283185307179586 / 45.0), y, z + radius * sin(i * 6.283185307179586 / 45.0))
                }
                GL11.glEnd()

                GL11.glDepthMask(true)
                GL11.glEnable(2929)
                GL11.glDisable(2848)
                GL11.glDisable(2881)
                GL11.glEnable(2832)
                GL11.glEnable(3553)
                GL11.glPopMatrix()
            }
            "jello" -> {
                val lastY: Double = yPos
                fun easeInOutQuart(x: Double): Double {
                    return if (x < 0.5) 8 * x * x * x * x else 1 - Math.pow(-2 * x + 2, 4.0) / 2
                }
                if (al > 0f) {
                    if (System.currentTimeMillis() - lastMS >= 1000L) {
                        direction = -direction
                        lastMS = System.currentTimeMillis()
                    }
                    val weird: Long =
                        if (direction > 0) System.currentTimeMillis() - lastMS else 1000L - (System.currentTimeMillis() - lastMS)
                    progress = weird.toDouble() / 1000.0
                    lastDeltaMS = System.currentTimeMillis() - lastMS
                } else { // keep the progress
                    lastMS = System.currentTimeMillis() - lastDeltaMS
                }

                if (target != null) {
                    entity = target
                    bb = entity!!.entityBoundingBox
                }

                if (bb == null || entity == null) return

                val radius: Double = bb!!.maxX - bb!!.minX
                val height: Double = bb!!.maxY - bb!!.minY
                val posX: Double =
                    entity!!.lastTickPosX + (entity!!.posX - entity!!.lastTickPosX) * mc.timer.renderPartialTicks
                val posY: Double =
                    entity!!.lastTickPosY + (entity!!.posY - entity!!.lastTickPosY) * mc.timer.renderPartialTicks
                val posZ: Double =
                    entity!!.lastTickPosZ + (entity!!.posZ - entity!!.lastTickPosZ) * mc.timer.renderPartialTicks

                yPos = easeInOutQuart(progress) * height

                val deltaY: Double =
                    (if (direction > 0) yPos - lastY else lastY - yPos) * -direction * jelloGradientHeightValue.get()

                if (al <= 0 && entity != null) {
                    entity = null
                    return
                }

                val colour: Color? = getColor(entity)
                val r = colour!!.red / 255.0f
                val g = colour!!.green / 255.0f
                val b = colour!!.blue / 255.0f

                pre3D()
                //post circles
                //post circles
                GL11.glTranslated(
                    -mc.renderManager.viewerPosX,
                    -mc.renderManager.viewerPosY,
                    -mc.renderManager.viewerPosZ
                )

                GL11.glBegin(GL11.GL_QUAD_STRIP)

                for (i in 0..360) {
                    val calc = i * Math.PI / 180
                    val posX2 = posX - Math.sin(calc) * radius
                    val posZ2 = posZ + Math.cos(calc) * radius
                    GL11.glColor4f(r, g, b, 0f)
                    GL11.glVertex3d(posX2, posY + yPos + deltaY, posZ2)
                    GL11.glColor4f(r, g, b, al * jelloAlphaValue.get())
                    GL11.glVertex3d(posX2, posY + yPos, posZ2)
                }

                GL11.glEnd()

                drawCircle(posX, posY + yPos, posZ, jelloWidthValue.get(), radius, r, g, b, al)

                post3D()
//                val drawTime = (System.currentTimeMillis() % 2000).toInt()
//                val drawMode = drawTime > 1000
//                var drawPercent = drawTime / 1000.0
//
//                //true when goes up
//                if (!drawMode) {
//                    drawPercent = 1 - drawPercent
//                } else {
//                    drawPercent -= 1
//                }
//                drawPercent = EaseUtils.easeInOutQuad(drawPercent)
//                val points = mutableListOf<WVec3>()
//                val bb = target!!.entityBoundingBox
//                val radius = bb.maxX - bb.minX
//                val height = bb.maxY - bb.minY
//                val posX = target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * mc.timer.renderPartialTicks
//                var posY = target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * mc.timer.renderPartialTicks
//
//                if (drawMode) {
//                    posY -= 0.5
//                } else {
//                    posY += 0.5
//                }
//                val posZ = target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * mc.timer.renderPartialTicks
//                for (i in 0..360 step 7) {
//                    points.add(WVec3(posX - sin(i * Math.PI / 180F) * radius, posY + height * drawPercent, posZ + cos(i * Math.PI / 180F) * radius))
//                }
//                points.add(points[0])
//                //draw
//                mc.entityRenderer.disableLightmap()
//                GL11.glPushMatrix()
//                GL11.glDisable(GL11.GL_TEXTURE_2D)
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
//                GL11.glEnable(GL11.GL_LINE_SMOOTH)
//                GL11.glEnable(GL11.GL_BLEND)
//                GL11.glDisable(GL11.GL_DEPTH_TEST)
//                GL11.glBegin(GL11.GL_LINE_STRIP)
//                val baseMove = (if (drawPercent > 0.5) {
//                    1 - drawPercent
//                } else {
//                    drawPercent
//                }) * 2
//                val min = (height / 60) * 20 * (1 - baseMove) * (if (drawMode) {
//                    -1
//                } else {
//                    1
//                })
//                for (i in 0..20) {
//                    var moveFace = (height / 60F) * i * baseMove
//                    if (drawMode) {
//                        moveFace = -moveFace
//                    }
//                    val firstPoint = points[0]
//                    GL11.glVertex3d(firstPoint.xCoord - mc.renderManager.viewerPosX, firstPoint.yCoord - moveFace - min - mc.renderManager.viewerPosY, firstPoint.zCoord - mc.renderManager.viewerPosZ)
//                    GL11.glColor4f(1F, 1F, 1F, 0.7F * (i / 20F))
//                    for (vec3 in points) {
//                        GL11.glVertex3d(
//                            vec3.xCoord - mc.renderManager.viewerPosX, vec3.yCoord - moveFace - min - mc.renderManager.viewerPosY,
//                            vec3.zCoord - mc.renderManager.viewerPosZ
//                        )
//                    }
//                    GL11.glColor4f(0F, 0F, 0F, 0F)
//                }
//                GL11.glEnd()
//                GL11.glEnable(GL11.GL_DEPTH_TEST)
//                GL11.glDisable(GL11.GL_LINE_SMOOTH)
//                GL11.glDisable(GL11.GL_BLEND)
//                GL11.glEnable(GL11.GL_TEXTURE_2D)
//                GL11.glPopMatrix()
            }
        }

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) &&
            currentTarget!!.hurtTime <= hurtTimeValue.get()) {
            clicks++
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }
    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return
        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return

        // Settings
        val failRate = failRateValue.get()
        val swing = swingValue.get()
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val openInventory = aacValue.get() && classProvider.isGuiContainer(mc.currentScreen)
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open
        if (openInventory)
            mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())

        // Check is not hitable or check failrate

        if (!hitable || failHit) {
            if (swing && (fakeSwingValue.get() || failHit))
                thePlayer.swingItem()
        } else {
            // Attack
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in theWorld.loadedEntityList) {
                    val distance = thePlayer.getDistanceToEntityBox(entity)

                    if (classProvider.isEntityLivingBase(entity) && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity.asEntityLivingBase())

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            if(targetModeValue.get().equals("Switch", true)){
                if(switchTimer.hasTimePassed(switchDelayValue.get().toLong())){
                    prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
                    switchTimer.reset()
                }
            }else{
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
            }

            if (target == currentTarget)
                target = null
        }

        // Open inventory
        if (openInventory)
            mc.netHandler.addToSendQueue(createOpenInventoryPacket())
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        target = null

        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("Switch", ignoreCase = true)

        // Find possible targets
        val targets = mutableListOf<IEntityLivingBase>()

        val theWorld = mc.theWorld!!
        val thePlayer = mc.thePlayer!!

        for (entity in theWorld.loadedEntityList) {
            if (!classProvider.isEntityLivingBase(entity) || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId)))
                continue

            val distance = thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.asEntityLivingBase().hurtTime <= hurtTime)
                targets.add(entity.asEntityLivingBase())
        }

        // Sort targets by priority
        when (priorityValue.get().toLowerCase()) {
            "distance" -> targets.sortBy { thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> targets.sortBy { it.health } // Sort by health
            "direction" -> targets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> targets.sortBy { -it.ticksExisted } // Sort by existence
            "HurtResitanTime" -> targets.sortBy { it.hurtResistantTime } // Sort by armor
        }

        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            return
        }

        // Cleanup last targets when no target found and try again
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    private fun isEnemy(entity: IEntity?): Boolean {
        if (classProvider.isEntityLivingBase(entity) && entity != null && (EntityUtils.targetDead || isAlive(entity.asEntityLivingBase())) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.invisible)
                return false

            if (EntityUtils.targetPlayer && classProvider.isEntityPlayer(entity)) {
                val player = entity.asEntityPlayer()

                if (player.spectator || AntiBot.isBot(player))
                    return false

                if (player.isClientFriend() && !LiquidBounce.moduleManager[NoFriends::class.java].state)
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity.asEntityLivingBase())
            }

            return EntityUtils.targetMobs && entity.isMob() || EntityUtils.targetAnimals && entity.isAnimal()
        }

        return false
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: IEntityLivingBase) {
        // Stop blocking
        val thePlayer = mc.thePlayer!!

            if (!autoBlockPacketValue.get().equals("Vanilla",true)&&(mc.thePlayer!!.isBlocking || blockingStatus)) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)))
            if (afterAttackValue.get()) blockingStatus = false
        }

        // Call attack event
        LiquidBounce.eventManager.callEvent(AttackEvent(entity))

        // Attack target
        if (swingValue.get() && Backend.MINECRAFT_VERSION_MINOR == 8)
            thePlayer.swingItem()

        mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(entity, ICPacketUseEntity.WAction.ATTACK))

        if (swingValue.get() && Backend.MINECRAFT_VERSION_MINOR != 8)
            thePlayer.swingItem()

        if (keepSprintValue.get()) {
            // Critical Effect
            if (thePlayer.fallDistance > 0F && !thePlayer.onGround && !thePlayer.isOnLadder &&
                !thePlayer.isInWater && !thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.BLINDNESS)) && !thePlayer.isRiding)
                thePlayer.onCriticalHit(entity)

            // Enchant Effect
            if (functions.getModifierForCreature(thePlayer.heldItem, entity.creatureAttribute) > 0F)
                thePlayer.onEnchantmentCritical(entity)
        } else {
            if (mc.playerController.currentGameType != IWorldSettings.WGameType.SPECTATOR)
                thePlayer.attackTargetEntityWithCurrentItem(entity)
        }

        // Extra critical effects
        val criticals = LiquidBounce.moduleManager[Criticals::class.java] as Criticals

        for (i in 0..2) {
            // Critical Effect
            if (thePlayer.fallDistance > 0F && !thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.BLINDNESS)) && thePlayer.ridingEntity == null || criticals.state && criticals.msTimer.hasTimePassed(criticals.delayValue.get().toLong()) && !thePlayer.isInWater && !thePlayer.isInLava && !thePlayer.isInWeb)
                thePlayer.onCriticalHit(target!!)

            // Enchant Effect
            if (functions.getModifierForCreature(thePlayer.heldItem, target!!.creatureAttribute) > 0.0f || fakeSharpValue.get())
                thePlayer.onEnchantmentCritical(target!!)
        }

        // Start blocking after attack
        if (mc.thePlayer!!.isBlocking || (!autoBlockValue.get().equals("off",true) && canBlock())) {
            if (delayedBlockValue.get())
                return

            if (!(blockRate.get() > 0 && Random().nextInt(100) <= blockRate.get()))
                return

            startBlocking(entity, interactAutoBlockValue.get())
        }

        @Suppress("ConstantConditionIf")
        if (Backend.MINECRAFT_VERSION_MINOR != 8) {
            thePlayer.resetCooldown()
        }
    }

    /**
     * Update killaura rotations to enemy
     */

    private fun updateRotations(entity: IEntity): Boolean {
        var boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("test1", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (vec, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation =  RotationUtils.limitAngleChange(RotationUtils.serverRotation, RotationUtils.getNCPRotations(RotationUtils.getCenter(entity.entityBoundingBox),false),(Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }

        if (rotations.get().equals("test2", ignoreCase = true)){
            //用这个test2看看
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (vec, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation =   RotationUtils.limitAngleChange(RotationUtils.serverRotation, RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox),false),(Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (rotations.get().equals("HytRotation", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.lockView(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }

        if (rotations.get().equals("New", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (vec, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation =  RotationUtils.limitAngleChange(RotationUtils.serverRotation, RotationUtils.getNewRotations(RotationUtils.getCenter(entity.entityBoundingBox),false),(Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (rotations.get().equals("LiquidBounce", ignoreCase = true)){
            //九用这个吧
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (vec, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (rotations.get().equals("BackTrack", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )


            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                RotationUtils.OtherRotation(boundingBox,RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),maxRange), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            }else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }
        return true
    }
    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if(hitableValue.get()){
            hitable = true
            return
        }
        // Disable hitable check if turn speed is zero
        if (maxTurnSpeed.get() <= 0F) {
            hitable = true
            return
        }

        val reach = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(target!!)) + 1

        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach, object : RaycastUtils.EntityFilter {
                override fun canRaycast(entity: IEntity?): Boolean {
                    return (!livingRaycastValue.get() || (classProvider.isEntityLivingBase(entity) && !classProvider.isEntityArmorStand(entity))) &&
                            (isEnemy(entity) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld!!.getEntitiesWithinAABBExcludingEntity(entity, entity!!.entityBoundingBox).isNotEmpty())
                }

            })

            if (raycastValue.get() && raycastedEntity != null && classProvider.isEntityLivingBase(raycastedEntity)
                && (LiquidBounce.moduleManager[NoFriends::class.java].state || !(classProvider.isEntityPlayer(raycastedEntity) && raycastedEntity.asEntityPlayer().isClientFriend())))
                currentTarget = raycastedEntity.asEntityLivingBase()

            hitable = if (maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: IEntity, interact: Boolean) {
        if (LiquidBounce.moduleManager[OldHitting::class.java].state){
            if (autoBlockValue.equals("Range") && mc.thePlayer!!.getDistanceToEntityBox(interactEntity)> BlockRangeValue.get()) {
                return
            }

            if (blockingStatus) {
                return
            }

            if (interact) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(interactEntity, interactEntity.positionVector))
                mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(interactEntity, ICPacketUseEntity.WAction.INTERACT))
            }
            if(autoBlockPacketValue.get().equals("UseItem", true)) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)
            }
            if(autoBlockPacketValue.get().equals("GameSettings", true)) {
                mc.gameSettings.keyBindUseItem.pressed = true
            }
            if(autoBlockPacketValue.get().equals("Mouse", true)) {
                Robot().mousePress(InputEvent.BUTTON3_DOWN_MASK)
            }
            if(autoBlockPacketValue.get().equals("Vanilla", true)) {

                if (vanillamode.get().equals("TryUseItem", true)){



                    mc.netHandler.addToSendQueue(classProvider.createCPacketTryUseItem(WEnumHand.MAIN_HAND))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketTryUseItem(WEnumHand.OFF_HAND))
                }
                if (vanillamode.get().equals("UseItem", true)){


                    mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.MAIN_HAND))
                mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.OFF_HAND))
                    }
                if (vanillamode.get().equals("OldC08", true)){

                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(
                        WBlockPos(
                            -0.5534147541,
                            -0.5534147541,
                            -0.5534147541
                        ), 255, mc.thePlayer!!.inventory.getCurrentItemInHand(), 0.0f, 0.0f, 0.0f
                    )
                    )
                }

                }
                    if (vanillamode.get().equals("C08", true)){
                        mc2.connection!!.sendPacket(CPacketPlayerTryUseItemOnBlock(
                            BlockPos(-0.5534147541, -0.5534147541, -0.5534147541),
                            EnumFacing.WEST, EnumHand.OFF_HAND, 0F,0F,0F)
                        )
                        mc2.connection!!.sendPacket(CPacketPlayerTryUseItemOnBlock(
                            BlockPos(-0.5534147541, -0.5534147541, -0.5534147541),
                            EnumFacing.WEST, EnumHand.MAIN_HAND, 0F,0F,0F)
                        )
//                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand()))
                    }


            }
            blockingStatus = true

    }


    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        if (blockingStatus) {

            if(autoBlockPacketValue.get().equals("Vanilla", true)) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, if (isMoving()) WBlockPos(-1, -1, -1) else WBlockPos.ORIGIN,                         classProvider.getEnumFacing(EnumFacingType.DOWN)))
            }
            if(autoBlockPacketValue.get().equals("UseItem", true)) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)
            }

            if(autoBlockPacketValue.get().equals("GameSettings", true)) {
                mc.gameSettings.keyBindUseItem.pressed = false
            }
            if(autoBlockPacketValue.get().equals("Mouse", true)) {
                Robot().mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
            }
            if(autoBlockPacketValue.get().equals("Vanilla", true)) {
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                        WBlockPos.ORIGIN,
                        classProvider.getEnumFacing(EnumFacingType.DOWN)
                    )
                )
            }

            blockingStatus = false
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        inline get() = mc.thePlayer!!.spectator || !isAlive(mc.thePlayer!!)
                || LiquidBounce.moduleManager[Blink::class.java].state || LiquidBounce.moduleManager[FreeCam::class.java].state

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: IEntityLivingBase) = entity.entityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 5


    /**
     * Check if player is able to block
     */
//    private val canBlock: Boolean
//        inline get() = mc.thePlayer!!.heldItem != null && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)
    private fun canBlock(): Boolean {
        return if(mc.thePlayer!!.heldItem != null && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)){
            if(autoBlockFacing.get()&&(target!!.getDistanceToEntityBox(mc.thePlayer!!)<maxRange)){
                target!!.rayTrace(maxRange.toDouble(),1F)!!.typeOfHit != IMovingObjectPosition.WMovingObjectType.MISS
            }else{
                true
            }
        }else{
            false
        }
    }

    /**
     * Range
     */

    private val maxRange: Float
        get() = max(rangeValue.get(), throughWallsRangeValue.get())

    private fun getRange(entity: IEntity) =
        (if (mc.thePlayer!!.getDistanceToEntityBox(entity) >= throughWallsRangeValue.get()) rangeValue.get() else rangeValue.get()) - if (mc.thePlayer!!.sprinting) rangeSprintReducementValue.get() else 0F

    /**
     * HUD Tag
     */
    override val tag: String
        get() = targetModeValue.get()

    val isBlockingChestAura: Boolean
        get() = state && target != null
}