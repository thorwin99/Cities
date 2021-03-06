package Main;

import Commands.CityCommand;
import Events.BlockEventListener;
import Events.PlayerMovementListener;
import Serilazibles.Vector2;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main plugin instance
 */
public class CitiesPlugin extends JavaPlugin {

    /**
     * Logger
     */
    private Logger logger;

    /**
     * Instance of the plugin, accessible for other classes to use
     */
    public static CitiesPlugin PluginInstance;

    /**
     * The current city manager
     */
    public CityManager cityManager;

    @Override
    public void onEnable(){
        logger = getLogger();
        ConfigurationSerialization.registerClass(Vector2.class);

        PluginInstance = this;

        cityManager = new CityManager(this);
        cityManager.LoadCities();

        RegisterCommands();
        RegisterEvents();

        saveDefaultConfig();

        logger.info("Enabled Cities");
    }

    @Override
    public void onDisable() {
        logger.info("Disabled Cities");
    }

    /**
     * Register all events of this plugin
     */
    private void RegisterEvents(){
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new BlockEventListener(), this);
        manager.registerEvents(new PlayerMovementListener(), this);
    }

    /**
     * Register all commands of this plugin
     */
    private void RegisterCommands(){
        CityCommand city = new CityCommand();
        getCommand("city").setExecutor(city);
        getCommand("city").setTabCompleter(city);
    }
}
