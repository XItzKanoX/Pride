var scriptName = "HYTCritcals"; 
var scriptVersion = 1.0; 
var scriptAuthor = "LvZiQiao";
var HYTCriticals = new HYTCriticals()
var HYTCriticalsClient

function HYTCriticals() {
    this.getName = function() {
        return "HYTCritcals";
    }
    this.getDescription = function() {
        return "LvZiQiao's God Critcals";
    }
    this.getCategory = function() {
        return "Combat";
    }
    this.getTag = function() {
        return "LvZiQiao";
    }
    this.onAttack = function(event) {
        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00001100134977413,
                        mc.thePlayer!!.posZ,
                        false
                    ));
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00000000013487744,
                        mc.thePlayer!!.posZ,
                        false
                    ));
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00000571003114589,
                        mc.thePlayer!!.posZ,
                        false
                    ));
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00000001578887744,
                        mc.thePlayer!!.posZ,
                        false
                    ));
    }
}
function onEnable() {
    HYTCriticalsClient = moduleManager.registerModule(HYTCriticals)
}

function onDisable() {
    moduleManager.unregisterModule(HYTCriticalsClient)
}