package Events;

import Serilazibles.City;
import Serilazibles.CityManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBreakEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnBlockBreak(BlockBreakEvent event){
        Player p = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        boolean previousCancelled = event.isCancelled();

        City city = CityManager.Static.getCity(chunk);
        if(city != null){
            if(!city.isResident(p.getUniqueId())){
                event.setCancelled(true);
            }
            else{
                event.setCancelled(previousCancelled);
            }
        }
    }
}
