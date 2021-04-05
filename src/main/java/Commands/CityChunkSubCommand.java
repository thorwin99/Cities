package Commands;

import Main.CitiesPlugin;
import Main.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * City sub command to add and remove chunks from a city.
 */
public class CityChunkSubCommand extends CitySubCommand {

    public CityChunkSubCommand() {
        super("chunk");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.chunks";
    }

    @Override
    public String getUsage() {
        return "/city chunk <add|remove>";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(strings.length != 2)return false;
            String action = strings[1];

            String city = CityManager.Static.getPlayerCity(player);

            if(city == null){
                commandSender.sendMessage(ChatColor.RED + "You are not resident of a city, you cant do that.");
                return true;
            }

            if(action.equals("add")){
                Chunk c = player.getLocation().getChunk();
                if(CityManager.Static.addChunkToCity(c, city)){
                    commandSender.sendMessage(ChatColor.GREEN + "Chunk added successfully to city " + ChatColor.YELLOW + city + ChatColor.GREEN + ".");
                    return true;
                }
                else{
                    commandSender.sendMessage(ChatColor.RED + "This chunk already belongs to a city.");
                    return true;
                }
            }
            else if(action.equals("remove")){
                Chunk c = player.getLocation().getChunk();
                if(CityManager.Static.removeChunkFromCity(c, city)){
                    commandSender.sendMessage(ChatColor.GREEN + "Chunk removed successfully from city " + ChatColor.YELLOW + city + ChatColor.GREEN + ".");
                    return true;
                }
                else{
                    commandSender.sendMessage(ChatColor.RED + "This chunk does not belong to a city.");
                    return true;
                }
            }
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "This command has to be run as a player.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args) {
        String start = args[1];
        List<String> suggestions = new ArrayList<>();
        if("add".startsWith(start))suggestions.add("add");
        if("remove".startsWith(start))suggestions.add("remove");
        return suggestions;
    }
}
