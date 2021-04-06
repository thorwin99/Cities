package Commands;

import Main.CitiesPlugin;
import Main.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
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
    public boolean isAdminExecutable() {
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings, boolean isAdminExec) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            int argOffset = 1;
            if(isAdminExec){
                argOffset = 3;
            }

            if(strings.length != argOffset + 1)return false;
            String action = strings[argOffset];

            String city = isAdminExec ? strings[1] : CityManager.Static.getPlayerCity(player);

            return runCommand(commandSender, player, action, city);
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "This command has to be run as a player.");
            return true;
        }
    }

    private boolean runCommand(CommandSender commandSender, Player player, String action, String city) {
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
        return false;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args, int startIndex) {
        if(args.length != startIndex + 2)return new LinkedList<>();

        String start = args[startIndex + 1];
        List<String> suggestions = new ArrayList<>();
        if("add".startsWith(start))suggestions.add("add");
        if("remove".startsWith(start))suggestions.add("remove");
        return suggestions;
    }
}
