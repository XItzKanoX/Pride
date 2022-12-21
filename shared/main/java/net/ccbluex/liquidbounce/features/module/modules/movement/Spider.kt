
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.event.BlockBBEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.AxisAlignedBB

import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "Spider", category = ModuleCategory.MOVEMENT, description = "sda")
class Spider : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Collide", "Motion", "AAC4"), "Collide")
    private val heightValue = IntegerValue("Height", 2, 0, 10)
    private val motionValue = FloatValue("Motion", 0.42F, 0.1F, 1F)

    private var startHeight = 0.0
    private var groundHeight = 0.0
    private var modifyBB = false
    private var glitch = false
    private var wasTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(wasTimer) {
            mc.timer.timerSpeed = 1.0f
        }
        if (!mc.thePlayer!!.isCollidedHorizontally || !mc.gameSettings.keyBindForward.pressed || (mc.thePlayer!!.posY - heightValue.get()> startHeight && heightValue.get()> 0)) {
            if (mc.thePlayer!!.onGround) {
                startHeight = mc.thePlayer!!.posY
                groundHeight = mc.thePlayer!!.posY
            }
            modifyBB = false
            return
        }
        if(modeValue.get()=="AAC4" && (mc.thePlayer!!.motionY < 0.0 || mc.thePlayer!!.onGround)) {
            glitch = true
        }

        modifyBB = true

        when (modeValue.get().toLowerCase()) {
            "collide","aac4" -> {
                if (mc.thePlayer!!.onGround) {
                    mc.thePlayer!!.jump()
                    groundHeight = mc.thePlayer!!.posY
                    if(modeValue.get()=="AAC4") {
                        wasTimer = true
                        mc.timer.timerSpeed = 0.4f
                    }
                }
            }
            "motion" -> {
                mc.thePlayer!!.motionY = motionValue.get().toDouble()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (classProvider.isCPacketPlayer(packet) && glitch) {
            glitch = false
            val yaw = MovementUtils.direction.toFloat()
            packet.asCPacketPlayer().x = packet.asCPacketPlayer().x - sin(yaw) * 0.00000001
            packet.asCPacketPlayer().z = packet.asCPacketPlayer().z + cos(yaw) * 0.00000001
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        wasTimer = false
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (!mc.thePlayer!!.isCollidedHorizontally || !mc.gameSettings.keyBindForward.pressed || (mc.thePlayer!!.posY - heightValue.get() > startHeight && heightValue.get() > 0)) {
            return
        }
        if (!modifyBB || mc.thePlayer!!.motionY > 0.0) return

        when (modeValue.get().toLowerCase()) {
            "collide","aac4" -> {
                if (event.block != null && mc.thePlayer != null && classProvider.isBlockAir(event.block) && event.y < mc.thePlayer!!.posY && mc.thePlayer!!.isCollidedHorizontally && mc.thePlayer!!.isOnLadder && mc.thePlayer!!.isInWater && mc.thePlayer!!.isInLava) event.boundingBox = classProvider.createAxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(
                    mc.thePlayer!!.posX, mc.thePlayer!!.posY.toInt() - 1.0, mc.thePlayer!!.posZ)
            }

            }

    }
}
