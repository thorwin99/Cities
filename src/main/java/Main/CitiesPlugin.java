import Events.BlockEventListener;
import Serilazibles.CityManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CitiesPlugin extends JavaPlugin {

    private Logger logger;

    public static CitiesPlugin PluginInstance;

    public CityManager cityManager;

    @Override
    public void onEnable(){
        logger = getLogger();

        PluginInstance = this;

        cityManager = new CityManager(this);
        cityManager.LoadCities();

        logger.info("Enabled Cities");
    }

    @Override
    public void onDisable() {
        cityManager.SaveCities();
        logger.info("Disabled Cities");
    }

    private void RegisterEvents(){
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new BlockEventListener(), this);
    }

    private void RegisterCommands(){

    }
}
