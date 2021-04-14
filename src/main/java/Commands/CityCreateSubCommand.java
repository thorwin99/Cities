package Commands;

import Main.ChunkManager;
import Main.CitiesPlugin;
import Main.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * City sub command to create a city
 */
public class CityCreateSubCommand extends CitySubCommand {

    public CityCreateSubCommand(){
        super("create");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.create";
    }

    @Override
    public String getUsage() {
        return "/city create <name> [radius]";
    }

    @Override
    public boolean isAdminExecutable() {
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings, boolean isAdminExec) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(strings.length < 2)return false;

            if(CityManager.Static.getPlayerCity(p) != null && !p.hasPermission("cities.city.admin")){
                p.sendMessage(ChatColor.RED + "You are already in a city. You cant create a second one.");
                return true;
            }

            String name = strings[1];
            int radius = CitiesPlugin.PluginInstance.getConfig().getInt("settings.cityDefaultRadius");

            if(strings.length == 3){
                try {
                    radius = Integer.parseInt(strings[2]);
                }catch (NumberFormatException e){
                    commandSender.sendMessage(ChatColor.RED + strings[2] + " is not a valid radius. The radius must be an integer.");
                    return true;
                }
            }
            if(radius < 1){
                commandSender.sendMessage(ChatColor.RED + strings[2] + " is not a valid radius. The radius must be larger than 1.");
                return true;
            }

            int limit = CitiesPlugin.PluginInstance.getConfig().getInt("settings.cityChunkLimit");
            if(radius * radius > limit){
                commandSender.sendMessage(ChatColor.RED + "The radius is to large. A city can have at max " + limit + " chunks.");
                return true;
            }

            HashSet<Chunk> chunks = GetValidCityChunks(p.getLocation().getChunk(), radius);
            if(chunks.size() == (2 * radius - 1) * (2 * radius - 1)){
                boolean result = CityManager.Static.createCity(name, p, chunks);
                if(result){
                    commandSender.sendMessage(ChatColor.GREEN + "City " + ChatColor.YELLOW + name + ChatColor.GREEN + " created.");
                    if(CityManager.Static.getPlayerCity(p) == null){
                        CityManager.Static.addResidentToCity(name, p);
                        commandSender.sendMessage(ChatColor.GREEN + "You are now a resident of the city " + ChatColor.YELLOW + name);
                    }
                    //Spawn effect to see it
                }
                else{
                    commandSender.sendMessage(ChatColor.RED + "A city with that name already exists. Names are case insensitive.");
                }
            }
            else{
                commandSender.sendMessage(ChatColor.RED + "Cant create city, some chunks seem to be already owned, or are too close to another city. Please reduce the radius, or move to another location.");
            }

        }
        else{
            commandSender.sendMessage(ChatColor.RED + "You cant create a city, if you are not a player");
        }

        return true;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args, int startIndex) {
        return new ArrayList<>();
    }

    /***
     * Gets all valid chunks, those are which are not claimed yet, and returns them.
     * Chunks will only be checked in radius from center, where radius 1 equals the center chunk.
     * @param center The center chunk
     * @param radius The radius
     * @return A set of chunks that are yet to be claimed
     */
    private HashSet<Chunk> GetValidCityChunks(Chunk center, int radius){
        HashSet<Chunk> chunks = new HashSet<>();
        for(int x = -(radius - 1); x < radius; x++){
            for(int y = -(radius - 1); y < radius; y++){
                Chunk c = center.getWorld().getChunkAt(x + center.getX(), y + center.getZ());
                if(ChunkManager.Static.isClaimable(null, c)){
                    chunks.add(c);
                }
            }
        }

        return chunks;
    }
}
