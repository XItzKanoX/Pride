/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.manager.CombatManager
import me.sound.Sound
import me.utils.AnimationHandler
import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.cape.CapeAPI.registerCapeService
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.features.special.ClientRichPresence
import net.ccbluex.liquidbounce.features.special.DonatorCape
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.tabs.BlocksTab
import net.ccbluex.liquidbounce.tabs.ExploitsTab
import net.ccbluex.liquidbounce.tabs.HeadsTab
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClassUtils.hasForge
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.utils.misc.sound.TipSoundManager
import net.minecraft.client.gui.GuiScreen
import java.awt.AWTException
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.thread

object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "Pride"
    const val CLIENT_VERSION = 37
    const val IN_DEV = true
    const val CLIENT_CREATOR = "CCBlueX, JieMo"
    lateinit var mainMenu: GuiScreen
    const val MINECRAFT_VERSION = Backend.MINECRAFT_VERSION
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"
    public val UPDATE_LIST = arrayListOf( "QQ Group 462907867 ", "Update Logs : ", "Build 30" ,"1.Fix KillAura AutoBlock", "2. add KillAura AttackEffect", "3.add AttackEffects", "4.add OldHitting mode ", "5.add KillAura Rotations",
        "6.Better Visual" ,
        "Build 31",
        "Better Visula",
        "add Something Bypass",
        "Build 32",
        "Fix Blur",
        "New GameInfo",
        "New Inventory",
        "New GuiMainmenu",
        "Fix Killaura rotation",
        "Build 33",
        "add Killaura Rotation Mode",
        "add Autoplay",
        "add GameInfoMode",
        "add NewEffect blur",
        "add Fonts",
        "Custom Font Shadow",
        "Better Visual",
        "Build 34",
        "Better Visual",
        "FixBugg",
        "Build 34",
        "Better Visual",
        "FixBugg",
        "Build 34.5",
        "Fix startClient bug",
        "Add more tiankeng bypass",
        "Add New SessionInfo",
        "Add Killaura SwitchDelay",
        "Build 35",
        "Better Jello TargetMark",
        "Better Visual",
        "Fix Noslow AAC5",
        "KillAura SwitchDelay",
        "KillAura Hyt Rotation",
        "FakeLag Disabler",
        "Fix ChestStealer",
        "Fix AutoArmor",
        "Fix InvManger",
        "Better Play"
        )

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var combatManager: CombatManager
    lateinit var tipSoundManager: TipSoundManager
    public var toggleVolume = 0F
    val windows = System.getProperties().getProperty("os.name").toLowerCase().contains("windows")

    var trayIcon: TrayIcon? = null
    // HUD & ClickGUI
    lateinit var hud: HUD
    lateinit var animationHandler: AnimationHandler
    lateinit var clickGui: ClickGui

    // Update information
    var latestVersion = 0

    // Menu Background
    var background: IResourceLocation? = null



    // Discord RPC
    lateinit var clientRichPresence: ClientRichPresence

    lateinit var wrapper: Wrapper
    @Throws(AWTException::class)
    private fun displayTray(Title: String, Text: String, type: TrayIcon.MessageType) {
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("icon.png")
        val trayIcon = TrayIcon(image, "Tray Demo")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "System tray icon demo"
        tray.add(trayIcon)
        trayIcon.displayMessage(Title, Text, type)
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Starting $CLIENT_NAME b$CLIENT_VERSION, by $CLIENT_CREATOR")

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(DonatorCape())
        eventManager.registerListener(InventoryUtils())


        // Init SoundManager
        tipSoundManager = TipSoundManager()

        // Init Discord RPC
        clientRichPresence = ClientRichPresence()



        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()
        animationHandler = AnimationHandler()
        try {
            // Remapper
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig)

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)



        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        try {
            // Read versions json from cloud
            val jsonObj = JsonParser()
                .parse(HttpUtils.get("$CLIENT_CLOUD/versions.json"))

            // Check json is valid object and has current minecraft version
            if (jsonObj is JsonObject && jsonObj.has(MINECRAFT_VERSION)) {
                // Get official latest client version
                latestVersion = jsonObj[MINECRAFT_VERSION].asInt
            }
        } catch (exception: Throwable) { // Print throwable to console
            ClientUtils.getLogger().error("Failed to check for updates.", exception)
        }

        // Load generators
        GuiAltManager.loadGenerators()

        //System notification
        if (windows) {
            if (SystemTray.isSupported()) {
                try {
                    trayIcon =
                        TrayIcon(ImageIO.read(Objects.requireNonNull(javaClass.getResourceAsStream("/assets/minecraft/pride/icon128.png"))))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                trayIcon?.isImageAutoSize = true
                trayIcon?.toolTip = "Pride  "
                try {
                    SystemTray.getSystemTray().add(trayIcon)
                } catch (var7: AWTException) {
                    //this.log("Unable to add tray icon.")
                }
                trayIcon?.displayMessage("Pride ", "Thank you for using Pride", TrayIcon.MessageType.NONE)
                Sound.notificationsAllowed(true)
            }
        }
      Sound()

        // Setup Discord RPC
        if (clientRichPresence.showRichPresenceValue) {
            thread {
                try {
                    clientRichPresence.setup()
                } catch (throwable: Throwable) {
                    ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
                }
            }
        }
//        displayTray("Pride", "Thanks Use", TrayIcon.MessageType.NONE)
//      Sound.INSTANCE.Spec()

        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

        // Shutdown discord rpc
        clientRichPresence.shutdown()
    }

}