package Commands;

import Main.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * City sub command used to list all cities
 */
public class CityListSubCommand extends CitySubCommand{

    public CityListSubCommand() {
        super("list");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.list";
    }

    @Override
    public String getUsage() {
        return "/city list";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1){
            List<String> cities = CityManager.Static.getCities();

            commandSender.sendMessage(ChatColor.BLUE + "=========" + ChatColor.YELLOW + "Cities" + ChatColor.BLUE + "=========");

            for(String name : cities){
                commandSender.sendMessage(ChatColor.GREEN + " * " + name);
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args) {
        return new ArrayList<>();
    }
}
