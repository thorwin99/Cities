package Commands;

import Main.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CityInfoSubCommand extends CitySubCommand {

    public CityInfoSubCommand() {
        super("info");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.info";
    }

    @Override
    public String getUsage() {
        return "/city info";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 1)return false;
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            String city = CityManager.Static.getPlayerCity(p);

            if(city == null){
                commandSender.sendMessage(ChatColor.RED + "You currently are not in any city.");
            }
            else{
                commandSender.sendMessage(ChatColor.BLUE + "======== " + ChatColor.YELLOW + "Info" + ChatColor.BLUE + " ========");
                commandSender.sendMessage(ChatColor.GREEN + "Name: " + ChatColor.GRAY + city);
                commandSender.sendMessage(ChatColor.GREEN + "Residents: " + ChatColor.GRAY + CityManager.Static.getCityResidents(city).size());
            }
        }

        return true;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args) {
        return new LinkedList<>();
    }
}
