package Commands;

import Main.CityManager;
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
    private final HashMap<String, CitySubCommand> subCommands;

    public CityCommand(){
        subCommands = new HashMap<>();
        registerSubCommand(new CityCreateSubCommand());
        registerSubCommand(new CityMapSubCommand());
        registerSubCommand(new CityChunkSubCommand());
        registerSubCommand(new CityListSubCommand());
        registerSubCommand(new CityResidentCommand());
        registerSubCommand(new CityDeleteSubCommand());
        registerSubCommand(new CityInfoSubCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length < 1 || (!subCommands.containsKey(strings[0]) && !strings[0].equals("admin"))){
            commandSender.sendMessage(ChatColor.GREEN + "Invalid arguments. Possible arguments are: ");
            for(CitySubCommand c : subCommands.values()){
                String perm = c.getNeededPermission();
                if(perm == null)perm = "cities.city";
                if(commandSender.hasPermission(perm))
                    commandSender.sendMessage(ChatColor.GREEN + c.getName() + ": " + ChatColor.GRAY + c.getUsage());
            }
            if(commandSender.hasPermission("cities.city.admin")){
                commandSender.sendMessage(ChatColor.GREEN + "admin: " + ChatColor.GRAY + "/city admin <cityName> <subcommand>");
            }
        }
        else{
            if(strings[0].equals("admin")){
                if(!commandSender.hasPermission("cities.city.admin")){
                    commandSender.sendMessage(ChatColor.RED + "You do not have the permission to do that.");
                    return true;
                }
                if(strings.length < 3){
                    commandSender.sendMessage(ChatColor.RED + "You need to provide at least a city and a subcommand as arguments.");
                    for(CitySubCommand c : subCommands.values()){
                        if(c.isAdminExecutable())
                            commandSender.sendMessage(ChatColor.GREEN + c.getName() + ": " + ChatColor.GRAY + c.getUsage());
                    }
                    return true;
                }
                String city = strings[1];
                if(!CityManager.Static.cityExists(city)){
                    commandSender.sendMessage(ChatColor.RED + "The city " + city + " does not exist");
                    return true;
                }
                String subCommand = strings[2];
                if(!subCommands.containsKey(subCommand) || !subCommands.get(subCommand).isAdminExecutable()){
                    commandSender.sendMessage(ChatColor.RED + subCommand + " is no valid sub command.");
                    return true;
                }
                if(!subCommands.get(subCommand).execute(commandSender, command, s, strings, true)){
                    commandSender.sendMessage(ChatColor.RED + "/city admin <cityname> <subcommand>");
                    return true;
                }
                return true;
            }
            else{
                CitySubCommand c = subCommands.get(strings[0]);
                String perm = c.getNeededPermission();
                if(perm == null)perm = "cities.city";
                if(commandSender.hasPermission(perm)){
                    if(!c.execute(commandSender, command, s, strings, false)){
                        commandSender.sendMessage(ChatColor.RED + c.getUsage());
                    }
                }
                else{
                    commandSender.sendMessage(ChatColor.RED + "You dont have the required permissions for this command.");
                }
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
            if(commandSender.hasPermission("cities.city.admin") && "admin".startsWith(start)){
                suggestions.add("admin");
            }
        }
        else{
            String subCommand = strings[0];
            if(subCommand.equals("admin") && commandSender.hasPermission("cities.city.admin")) {
                if(strings.length == 2){
                    String start = strings[1];
                    for(String city : CityManager.Static.getCities())
                    {
                        if(city.startsWith(start)){
                            suggestions.add(city);
                        }
                    }
                }
                else if(strings.length == 3){
                    String start = strings[2];
                    for(String cmd : subCommands.keySet())
                    {
                        if(subCommands.get(cmd).isAdminExecutable() && cmd.startsWith(start)){
                            suggestions.add(cmd);
                        }
                    }
                }
                else if(strings.length > 3){
                    String subcmd = strings[2];
                    if(subCommands.containsKey(subcmd) && subCommands.get(subcmd).isAdminExecutable()){
                        return subCommands.get(subcmd).getTabCompletion(commandSender, command, s, strings, 2);
                    }
                }
            }
            else if(subCommands.containsKey(subCommand)){
                return subCommands.get(subCommand).getTabCompletion(commandSender, command, s, strings, 0);
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
