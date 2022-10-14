/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.player.EntityPlayer

import net.ccbluex.liquidbounce.utils.CodeTool

@ModuleInfo(name = "Teams", description = "Prevents Killaura from attacking team mates.", category = ModuleCategory.MISC)
class Teams : Module() {

    private val scoreboardValue = BoolValue("ScoreboardTeam", true)
    private val colorValue = BoolValue("Color", true)
    private val gommeSWValue = BoolValue("GommeSW", false)
    private val armorColorValue = BoolValue("ArmorColor", false)
    private val hytValue = BoolValue("HytFixed", false)

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(entity: IEntityLivingBase): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        if (scoreboardValue.get() && thePlayer.team != null && entity.team != null &&
            thePlayer.team!!.isSameTeam(entity.team!!))
            return true

        val displayName = thePlayer.displayName

        if(armorColorValue.get()){
            val entityPlayer = entity.asEntityPlayer()
            if(thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null){
                val myHead = thePlayer.inventory.armorInventory[3]
                val myItemArmor = myHead!!.item!!.asItemArmor()


                val entityHead = entityPlayer.inventory.armorInventory[3]
                val entityItemArmor = myHead.item!!.asItemArmor()

                if(myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead!!)){
                    return true
                }
            }
        }

        if(hytValue.get()){
            val entityPlayer = entity as EntityPlayer
            val networkPlayerInfo = mc.netHandler.getPlayerInfo(entityPlayer.uniqueID)
            if (networkPlayerInfo != null) {
                var startString = CodeTool.guiIngame.tabList.getPlayerName(mc.netHandler.getPlayerInfo(mc.thePlayer!!.uniqueID) as NetworkPlayerInfo).substring(0,4)
                startString = startString.replace("§","&")
                if (CodeTool.guiIngame.tabList.getPlayerName(networkPlayerInfo as NetworkPlayerInfo).replace("§","&").substring(0,4) == startString)
                    return true
            }
        }

        if (gommeSWValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (targetName[1].isDigit() && clientName[1].isDigit())
                    return targetName[1] == clientName[1]
        }

        if (colorValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }

}
