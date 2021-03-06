package Events;

import Main.CityManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Event listener for block break, place and interact events, to prevent players,
 * who are not resident of a city, to build or interact with blocks there.
 */
public class BlockEventListener implements Listener {

    private static final String ADMIN_PERM = "cities.city.admin";

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnBlockBreak(BlockBreakEvent event){
        Player p = event.getPlayer();
        boolean previousCancelled = event.isCancelled();
        boolean canInteract = CanInteractWithBlock(event.getBlock(), p);

        event.setCancelled(!canInteract || previousCancelled);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnBlockBreak(BlockPlaceEvent event){
        Player p = event.getPlayer();
        boolean previousCancelled = event.isCancelled();
        boolean canInteract = CanInteractWithBlock(event.getBlock(), p);

        event.setCancelled(!canInteract || previousCancelled);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnBlockBreak(PlayerInteractEvent event){
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b == null || b.getType() == Material.AIR) return;

        boolean previousCancelled = event.isCancelled();
        boolean canInteract = CanInteractWithBlock(b, p);

        event.setCancelled(!canInteract || previousCancelled);
    }

    /**
     * Checks if player is allowed to interact with the given block
     * @param block The block the player breaks or places
     * @param player The player
     * @return True, if the player can interact ith the block.
     */
    private boolean CanInteractWithBlock(Block block, Player player){
        if(player.hasPermission(ADMIN_PERM))return true;

        Chunk chunk = block.getChunk();

        String city = CityManager.Static.getCity(chunk);
        if(city != null){
            if(!CityManager.Static.playerIsResident(city, player)){
                return false;
            }
            else{
                return true;
            }
        }
        return true;
    }
}
