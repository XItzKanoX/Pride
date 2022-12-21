
package net.ccbluex.liquidbounce.features.module.modules.combat

import me.utils.player.PlayerUtil.isMoving
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.CPacketEntityAction

@ModuleInfo(name = "SuperKnockback", description = "Fix by JIEMO", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("Normal", "MCYC", "ExtraPacket", "WTap", "Packet", "HYTPacket", "Tick" ), "Normal")
    private val onlyMoveValue = BoolValue("OnlyMove", false)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val delay = IntegerValue("Delay", 0, 0, 500)

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if (event.targetEntity.hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delay.get().toLong()) ||
                (!isMoving() && onlyMoveValue.get()) || (!mc.thePlayer!!.onGround && onlyGroundValue.get())) {
                return
            }
            when (modeValue.get()) {
                "extrapacket" -> {
                    if (mc2.player.isSprinting) {
                        mc2.player.isSprinting = true
                    }

                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.thePlayer!!.serverSprintState = true
                }
                "normal" -> {
                    if (mc2.player.isSprinting) {
                        mc2.player.isSprinting = true
                    }
                    mc.thePlayer!!.serverSprintState = true
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                }
                "mcyc" -> {
                    if (mc2.player.isSprinting) mc2.player.isSprinting = false
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc2.player.isSprinting = true
                    mc.thePlayer!!.serverSprintState = true
                }
                "hytpacket" -> {
                    if (mc2.player.isSprinting)
                        mc2.player.isSprinting = true
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.thePlayer!!.serverSprintState = true
                }

                "tick" -> {
                    if (mc2.player.isSprinting)
                        mc2.player.isSprinting = false
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.thePlayer!!.serverSprintState = true
                }
                "wtap" -> {
                    if (mc2.player.isSprinting) {
                        mc2.player.isSprinting = false
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.thePlayer!!.serverSprintState = true
                }
                "packet" -> {
                    if (mc2.player.isSprinting) {
                        mc2.player.isSprinting = true
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SPRINTING))
                    mc.thePlayer!!.serverSprintState = true
                }
            }
            timer.reset()
        }
    }
    override val tag: String
        get() = modeValue.get()
}
