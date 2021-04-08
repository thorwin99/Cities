package Commands;

import Main.CitiesPlugin;
import Main.CityManager;
import Serilazibles.Vector2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CityTpSubCommand extends CitySubCommand {

    public CityTpSubCommand() {
        super("tp");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.tp";
    }

    @Override
    public String getUsage() {
        return "/city tp <city>";
    }

    @Override
    public boolean isAdminExecutable() {
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings, boolean isAdminExec) {
        if(commandSender instanceof Player){
            if(strings.length == 2){
                String city = strings[1];
                if(CityManager.Static.cityExists(city)){
                    List<Vector2> chunks = CityManager.Static.getCityChunks(city);
                    if(chunks.size() > 0){
                        String world = CityManager.Static.getCityWorld(city);
                        World w = Bukkit.getWorld(world);
                        Player p = (Player) commandSender;

                        if(w == null){
                            commandSender.sendMessage(ChatColor.RED + "The world of the city doesnt exist");
                            return true;
                        }

                        for(Vector2 coord : chunks){
                            Chunk chunk = w.getChunkAt(coord.X, coord.Y);
                            Location tp = findSuitableTPSpot(chunk);
                            if(tp != null){
                                p.teleport(tp);
                                commandSender.sendMessage(ChatColor.GREEN + "Teleported to city " + ChatColor.YELLOW + city);
                                return true;
                            }
                        }

                        commandSender.sendMessage(ChatColor.RED + "Cant find suitable spot to teleport in the city.");
                    }
                    else{
                        commandSender.sendMessage(ChatColor.RED + "The city does not have any area associated. Cant teleport");
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "The city " + ChatColor.YELLOW + " doesn't exist");
                }

                return true;
            }
            return false;
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "You must be a player to teleport to a city");
        }

        return true;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args, int startIndex) {
        List<String> suggestions = new LinkedList<>();
        if(args.length == startIndex + 1){
            String start = args[1];
            for(String city : CityManager.Static.getCities()){
                if(city.startsWith(start))
                    suggestions.add(city);
            }
        }
        return suggestions;
    }

    /**
     * Tries to find a suitable TP spot in a chunk
     * @param chunk Chunk to get TP Spot
     * @return Location or null
     */
    private Location findSuitableTPSpot(Chunk chunk){
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(int y = 255; y > 50; y--){
                    Block b = chunk.getBlock(x, y, z);
                    Block b1 = chunk.getBlock(x, y - 1, z);
                    Block b2 = chunk.getBlock(x, y - 2, z);
                    if(b.getType() == Material.AIR && b.getType() == b1.getType() && b2.getType().isSolid()){
                        return b1.getLocation();
                    }
                }
            }
        }
        return null;
    }
}
