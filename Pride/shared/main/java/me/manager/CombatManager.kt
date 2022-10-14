package me.manager

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer


class CombatManager : Listenable, MinecraftInstance() {
    var inCombat=false
    private val lastAttackTimer=MSTimer()
    var target: IEntityLivingBase? = null

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if(mc.thePlayer==null) return
        MovementUtils.updateBlocksPerSecond()

        inCombat=false

        if(!lastAttackTimer.hasTimePassed(1000)){
            inCombat=true
            return
        }

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (entity is IEntityLivingBase
                && entity.getDistanceToEntity(mc.thePlayer!!) < 7 && EntityUtils.isSelected(entity, true)) {
                inCombat = true
                break
            }
        }

        if(target!=null){
            if(mc.thePlayer!!.getDistanceToEntity(target!!)>7||!inCombat||target!!.isDead){
                target=null
            }
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent){
        if(event.targetEntity is IEntityLivingBase && EntityUtils.isSelected(event.targetEntity,true)){
            target= event.targetEntity as IEntityLivingBase?
        }
        lastAttackTimer.reset()

    }
    fun getNearByEntity(radius: Float):IEntityLivingBase?{
        return try {
            mc.theWorld!!.loadedEntityList
                .filter { mc.thePlayer!!.getDistanceToEntity(it)<radius&&EntityUtils.isSelected(it,true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer!!) }[0] as IEntityLivingBase?
        }catch (e: Exception){
            null
        }
    }

    override fun handleEvents(): Boolean {
        return true
    }
}