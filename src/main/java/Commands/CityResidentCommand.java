package Commands;

import Main.CitiesPlugin;
import Main.CityManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
        return "/city residents <<add|remove> <player> | <list [page]>";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length < 2 || strings.length > 3)return false;

            Player player = (Player) commandSender;
            String city = CityManager.Static.getPlayerCity(player);

            if(city == null){
                commandSender.sendMessage(ChatColor.RED + "You are currently no resident of a city.");
                return true;
            }

            if(strings.length == 2){
                if(strings[1].equals("list")){
                    printResidentListPage(commandSender, city, 1);
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You need to specify a player.");
                }
            }
            else{
                String action = strings[1];

                if(action.equals("list")){
                    try{
                        int page = Integer.parseInt(strings[2]);
                        printResidentListPage(commandSender, city, page);
                    }catch(NumberFormatException e){
                        commandSender.sendMessage(ChatColor.RED + strings[2] + " is not a valid page number");
                    }
                    return true;
                }

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
            if("list".startsWith(args[1])) suggestions.remove("list");
        }
        else if(args.length == 3 && !args[1].equals("list")){
            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
            String startName = args[2];

            for(Player p : players){
                if(p.getDisplayName().startsWith(startName))
                    suggestions.add(p.getDisplayName());
            }
        }
        return suggestions;
    }

    private void printResidentListPage(CommandSender commandSender, String city, int page) {
        List<UUID> playerIds = CityManager.Static.getCityResidents(city);

        if(page < 0 || page > playerIds.size() / 10 + 1){
            commandSender.sendMessage(ChatColor.RED + "The page " + page + " does not exist.");
        }

        commandSender.sendMessage(ChatColor.BLUE + "=== " + ChatColor.YELLOW + "Residents of " + ChatColor.GREEN+ city + ChatColor.BLUE + " ===");
        for(int i = (page - 1) * 10; i < (page * 10) % playerIds.size(); i++){
            Player p = Bukkit.getServer().getPlayer(playerIds.get(i));
            if(p == null)continue;

            commandSender.sendMessage(ChatColor.GREEN + "* " + ChatColor.GRAY + p.getDisplayName());
        }

        commandSender.sendMessage(ChatColor.BLUE + "=== " + ChatColor.YELLOW + "Page [" + page + "/" + (playerIds.size() / 10 + 1) + "]" + ChatColor.BLUE);
    }
}
