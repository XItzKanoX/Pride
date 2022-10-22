
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Items


@ModuleInfo(name = "Gapple", description = "Eat Gapples.", category = ModuleCategory.PLAYER)
class Gapple : Module() {
    val modeValue = ListValue("Mode", arrayOf("Auto", "LegitAuto", "Once", "Head"), "Once")
    // Auto Mode
    private val healthValue = FloatValue("Health", 10F, 1F, 20F)
    private val delayValue = IntegerValue("Delay", 150, 0, 1000)
    private val noAbsorption = BoolValue("NoAbsorption",true)
    private val timer = MSTimer()

    private var eating = -1

    override fun onEnable() {
        eating = -1
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        when(modeValue.get().toLowerCase()){
            "once" -> {
                doEat(true)
                state = false
            }
            "auto" -> {
                if (!timer.hasTimePassed(delayValue.get().toLong()))
                    return
                if (mc.thePlayer!!.health <= healthValue.get()){
                    doEat(false)
                    timer.reset()
                }
            }
            "legitauto" -> {
                if (eating == -1) {
                    val gappleInHotbar = InventoryUtils.findItem2(36, 45, Items.GOLDEN_APPLE)
                    if(gappleInHotbar == -1) return
                    mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(gappleInHotbar - 36))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.heldItem))
                    eating = 0
                } else if (eating > 35) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
                    timer.reset()
                }
            }
            "head" -> {
                if (!timer.hasTimePassed(delayValue.get().toLong()))
                    return
                if (mc.thePlayer!!.health <= healthValue.get()){
                    val headInHotbar = InventoryUtils.findItem2(36, 45, Items.SKULL)
                    if(headInHotbar != -1) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(headInHotbar - 36))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.heldItem))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
                        timer.reset()
                    }
                }
            }
        }
    }

    private fun doEat(warn: Boolean){
        if(noAbsorption.get()&&!warn){
                return
        }

        val gappleInHotbar = InventoryUtils.findItem2(36, 45, Items.GOLDEN_APPLE)
        if(gappleInHotbar != -1){
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(gappleInHotbar - 36))
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.heldItem))
            repeat(35) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(mc.thePlayer!!.onGround))
            }
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
        }else if(warn){
        }
    }

    override val tag: String
        get() = modeValue.get()
}