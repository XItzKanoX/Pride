package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.vulcan

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.settings.GameSettings

class VulcanHop : SpeedMode("VulcanHop") {
	
    private var wasTimer = false
    override fun onTick() {}
    override fun onMotion(event: MotionEvent) {}
    override fun onMove(event: MoveEvent) {}

    override fun onUpdate() {
        if (wasTimer) {
            mc.timer.timerSpeed = 1.00f
            wasTimer = false
        }
        if (Math.abs(mc.thePlayer!!.movementInput.moveStrafe) < 0.1f) {
            mc.thePlayer!!.jumpMovementFactor = 0.026499f
        }else {
            mc.thePlayer!!.jumpMovementFactor = 0.0244f
        }
        mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)

        if (MovementUtils.speed < 0.215f && !mc.thePlayer!!.onGround) {
            MovementUtils.strafe(0.215f)
        }
        if (mc.thePlayer!!.onGround && MovementUtils.isMoving) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer!!.jump()
	    if (!mc.thePlayer!!.isAirBorne) {
                return //Prevent flag with Fly
            }
	    mc.timer.timerSpeed = 1.25f
	    wasTimer = true
	    MovementUtils.strafe()
	    if(MovementUtils.speed < 0.5f) {
	        MovementUtils.strafe(0.4849f)
	    }
        }else if (!MovementUtils.isMoving) {
            mc.timer.timerSpeed = 1.00f
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        }
    }
}
