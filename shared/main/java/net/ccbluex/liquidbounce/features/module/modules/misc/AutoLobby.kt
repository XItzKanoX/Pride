package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketClientStatus
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketCloseWindow
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ISPacketCloseWindow
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.CPacketClientStatus

@ModuleInfo(name = "AutoLobby", description = "Bypas", category = ModuleCategory.MISC)
class AutoLobby : Module(){
    var health = FloatValue("Health", 5F, 0F, 20F)
    var canhubchat = BoolValue("CanHubChat",false)
    var randomhub = BoolValue("RandomHub",false)
    var hubchattext = TextValue("HubChat","[Pride1.12.2] Bypass")
    var disabler = BoolValue("AutoDisable-KillAura-Velocity-Speed", true)
    var keepArmor = BoolValue("KeepArmor", true)
    var hubDelayTime = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        val velocity = LiquidBounce.moduleManager[Velocity::class.java] as Velocity
        val speed = LiquidBounce.moduleManager[Speed::class.java] as Speed
        if (mc.thePlayer!!.health < health.get()){
            if(keepArmor.get()) {
                for (i in 0..3) {
                    val armorSlot = 3 - i
                    move(8 - armorSlot, true)
                }
            }
            if(canhubchat.get()){
                mc.thePlayer!!.sendChatMessage(hubchattext.get())
            }
            if(randomhub.get()){
                if(hubDelayTime.hasTimePassed(300)) {
                    mc.thePlayer!!.sendChatMessage("/hub " + (Math.random() * 100 + 1).toInt())
                    hubDelayTime.reset()
                }
            }else{
                if(hubDelayTime.hasTimePassed(300)) {
                    mc.thePlayer!!.sendChatMessage("/hub")
                    hubDelayTime.reset()
                }
            }
            if (disabler.get()){
                killAura.state = false
                velocity.state = false
                speed.state = false
            }
        }
    }

    private fun move(item: Int, isArmorSlot: Boolean) { //By Gk
        if (item != -1) {
            val openInventory = mc.currentScreen !is GuiInventory
            if (openInventory) mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!,
                ICPacketEntityAction.WAction.OPEN_INVENTORY))
            mc.playerController.windowClick(
                mc.thePlayer!!.inventoryContainer.windowId, if (isArmorSlot) item else if (item < 9) item + 36 else item, 0, 1, mc.thePlayer!!
            )
            if (openInventory) mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())
        }
    }

    override val tag: String?
        get() =  "HuaYuTing"
}