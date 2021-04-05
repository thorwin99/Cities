package Commands;

import Main.CityManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * City sub command used to add or remove residents from your city.
 */
public class CityResidentCommand extends CitySubCommand{

    public CityResidentCommand() {
        super("residents");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.residents";
    }

    @Override
    public String getUsage() {
        return "/city residents <add|remove> <player>";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length < 2 || strings.length > 3)return false;
            if(strings.length == 2){
                commandSender.sendMessage(ChatColor.RED + "You need to specify a player.");
            }
            else{
                Player player = (Player) commandSender;
                String city = CityManager.Static.getPlayerCity(player);

                if(city == null){
                    commandSender.sendMessage(ChatColor.RED + "You are currently no resident of a city.");
                    return true;
                }

                String action = strings[1];
                String playerName = strings[2];
                Player player2 = Bukkit.getServer().getPlayer(playerName);

                if(player2 == null){
                    commandSender.sendMessage(ChatColor.RED + "Player " + playerName + " not found");
                }
                else{
                    if(action.equals("add")){
                        if(CityManager.Static.addResidentToCity(city, player2)){
                            commandSender.sendMessage(ChatColor.GREEN + "Player added successfully to your city.");
                        }
                        else{
                            commandSender.sendMessage(ChatColor.RED + "The Player is already in a city");
                        }
                    }
                    else if(action.equals("remove")){
                        if(CityManager.Static.removeResidentFromCity(city, player2)){
                            commandSender.sendMessage(ChatColor.GREEN + "The Player was removed from your city.");
                        }
                        else{
                            commandSender.sendMessage(ChatColor.RED + "The Player is not in your city");
                        }
                    }
                }
            }
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "You need to be a player to use this command.");
        }
        return true;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(args.length == 2){
            if("add".startsWith(args[1])) suggestions.add("add");
            if("remove".startsWith(args[1])) suggestions.remove("remove");
        }
        else if(args.length == 3){
            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
            String startName = args[2];

            for(Player p : players){
                if(p.getDisplayName().startsWith(startName))
                    suggestions.add(p.getDisplayName());
            }
        }
        return suggestions;
    }
}
