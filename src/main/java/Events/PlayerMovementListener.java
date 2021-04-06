package Events;

import Main.CitiesPlugin;
import Main.CityManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerChangeChunk(PlayerMoveEvent event){
        Chunk old = event.getFrom().getChunk();
        Chunk now = event.getTo().getChunk();

        if(old != now){
            String newCity = CityManager.Static.getCity(now);
            String oldCity = CityManager.Static.getCity(old);

            if(newCity == null)return;

            if(CitiesPlugin.PluginInstance.getConfig().getBoolean("settings.showCityWelcomeMessage")){
                String msg = CitiesPlugin.PluginInstance.getConfig().getString("settings.cityWelcomeMessage");
                if(msg == null) msg = ChatColor.GREEN + "Welcome to " + ChatColor.YELLOW + newCity;
                msg = msg.replace("{city}", newCity);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        }
    }
}
