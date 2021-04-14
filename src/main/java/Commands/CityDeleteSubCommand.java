package Commands;

import Main.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;

/**
 * Command used to delete a city
 */
public class CityDeleteSubCommand extends CitySubCommand{

    public CityDeleteSubCommand() {
        super("delete");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.delete";
    }

    @Override
    public String getUsage() {
        return "/city delete <cityName>";
    }

    @Override
    public boolean isAdminExecutable() {
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings, boolean isAdminExec) {
        if(strings.length != 2)return false;

        String cityName = strings[1];

        if(CityManager.Static.cityExists(cityName)){
            if(CityManager.Static.removeCity(cityName)){
                commandSender.sendMessage(ChatColor.GREEN + "The City " + ChatColor.YELLOW + cityName + ChatColor.GREEN + " was deleted.");
                return true;
            }
        }
        commandSender.sendMessage(ChatColor.RED + "The City " + ChatColor.YELLOW + cityName + ChatColor.RED + " doesn't exist.");
        return true;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args, int startIndex) {
        List<String> suggestions = new LinkedList<>();

        if(args.length != startIndex + 2)return suggestions;

        for(String city : CityManager.Static.getCities()){
            if(city.startsWith(args[startIndex + 1]))
                suggestions.add(city);
        }

        return suggestions;
    }
}
