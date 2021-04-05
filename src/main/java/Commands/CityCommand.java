package Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The basic city command, which runs its sub commands and manages permissions
 */
public class CityCommand implements CommandExecutor, TabCompleter {

    /**
     * Set of all sub commands with their name as key
     */
    private HashMap<String, CitySubCommand> subCommands;

    public CityCommand(){
        subCommands = new HashMap<>();
        registerSubCommand(new CityCreateSubCommand());
        registerSubCommand(new CityMapSubCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length < 1 || !subCommands.containsKey(strings[0])){
            commandSender.sendMessage(ChatColor.GREEN + "Please provide at least one argument. Possible arguments are: ");
            for(CitySubCommand c : subCommands.values()){
                String perm = c.getNeededPermission();
                if(perm == null)perm = "cities.city";
                if(commandSender.hasPermission(perm))
                    commandSender.sendMessage(ChatColor.GREEN + c.getName() + ": " + ChatColor.GRAY + c.getUsage());
            }
        }
        else{
            CitySubCommand c = subCommands.get(strings[0]);
            String perm = c.getNeededPermission();
            if(perm == null)perm = "cities.city";
            if(commandSender.hasPermission(perm)){
                if(!c.execute(commandSender, command, s, strings)){
                    commandSender.sendMessage(ChatColor.RED + c.getUsage());
                }
            }
            else{
                commandSender.sendMessage(ChatColor.RED + "You dont have the required permissions for this command.");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> suggestions = new ArrayList<>();
        if(strings.length <= 1 && !subCommands.containsKey(strings[0])){
            String start = strings.length < 1 ? "" : strings[0];
            for(CitySubCommand c : subCommands.values()){
                String perm = c.getNeededPermission();
                if(perm == null)perm = "cities.city";
                if(commandSender.hasPermission(perm) && c.getName().startsWith(start))
                    suggestions.add(c.getName());
            }
        }
        else{
            String subCommand = strings[0];
            if(subCommands.containsKey(subCommand)){
                return subCommands.get(subCommand).getTabCompletion(commandSender, command, s, strings);
            }
        }
        return suggestions;
    }

    /**
     * Registers a new sub command
     * @param command Command to register
     */
    private void registerSubCommand(CitySubCommand command){
        if(subCommands.containsKey(command.getName()))return;

        subCommands.put(command.getName(), command);
    }
}
