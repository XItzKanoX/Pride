package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.CPacketEntityAction

import java.util.*



@ModuleInfo (
        "KeepSprint",
        category = ModuleCategory.MOVEMENT,
        description = "das"
)
class KeepSprint : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Motion"), "Vanilla")
    private val motionValue = FloatValue("SlowDown", 100f, 20f, 100f)

    @EventTarget
    fun onAttack(attackEvent : AttackEvent) {
        val entity : IEntityLivingBase? = if(attackEvent.targetEntity is IEntityLivingBase) attackEvent.targetEntity else null
        if(modeValue.get().equals("Vanilla"))
            return

        if (entity != null) {
            val dist: Double
            if (!mc.thePlayer!!.capabilities.isCreativeMode) {
                dist = mc.objectMouseOver!!.hitVec.distanceTo(mc.renderViewEntity!!.getPositionEyes(1.0f))
                val `val`: Double
                `val` = if (dist > 3.0) {
                    (100.0 - (motionValue.get())) / 100.0
                } else {
                    0.6
                }
                mc.thePlayer!!.motionX *= `val`
                mc.thePlayer!!.motionZ *= `val`
            } else {
                dist = (100.0 - motionValue.get()) / 100.0
                mc.thePlayer!!.motionX *= dist
                mc.thePlayer!!.motionZ *= dist
            }
        }
    }
    @EventTarget
    fun onPacket(event : PacketEvent) {
        val packet = event.packet.unwrap()
        if(!modeValue.get().equals("Vanilla"))
            return
        if (packet is CPacketEntityAction) {
            if (packet.action == CPacketEntityAction.Action.STOP_SPRINTING) {
                event.cancelEvent()
            }
        }
    }
    override val tag: String
        get() = when(modeValue.get().toLowerCase(Locale.getDefault())){
            "vanilla" -> {
                "Vanilla"
            }
            "motion" -> {
                "% ${motionValue.get()}"
            }
            else -> modeValue.get()
        }
}