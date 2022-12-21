package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.value.FloatValue;

/**
 * Skid by Paimon.
 *
 * @Date 2022/8/27
 */
@ModuleInfo(name = "MemoryFix",description = "idk", category = ModuleCategory.MISC)
public class MemoryFix extends Module {
    private final FloatValue delay = new FloatValue("Delay", 120.0F, 10.0F, 600.0F);
    private final FloatValue limit = new FloatValue("Limit", 80.0F, 20.0F, 95.0F);

    private final FloatValue Speed = new FloatValue("Speed", 0.05F, 0F, 1F);
    public MSTimer timer = new MSTimer();

    @EventTarget
    public void onTick(TickEvent event) {
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        float pct = (float)(usedMem * 100L / maxMem);
        if (timer.hasReached(delay.get() * 1000.0D) && limit.get() <= (double)pct) {
            Runtime.getRuntime().gc();
            timer.resetTwo();
        }
    }
    @EventTarget
    public void onMotion(MotionEvent event){
        if(mc.getGameSettings().getKeyBindForward().getPressed() || mc.getGameSettings().getKeyBindLeft().getPressed() || mc.getGameSettings().getKeyBindRight().getPressed() || mc.getGameSettings().getKeyBindBack().getPressed()) {
            if(mc.getThePlayer().getOnGround()) {
                mc.getThePlayer().setCameraPitch(Speed.get());
            }
        } else {
            mc.getThePlayer().setCameraPitch(0.0f);
        }
    }
}
