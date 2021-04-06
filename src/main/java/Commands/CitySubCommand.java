package Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

/**
 * Abstract class for sub commands of the city command
 */
public abstract class CitySubCommand {

    /**
     * Name of the sub command
     */
    private String name;

    public CitySubCommand(String name){
        this.name = name.toLowerCase();
    }

    /**
     * Gets the name of this sub command
     * @return The name
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the required permission. If null, city command permission is needed
     * @return The permission or null
     */
    public abstract String getNeededPermission();

    /**
     * Returns the usage of the sub command
     * @return The usage
     */
    public abstract String getUsage();

    /**
     * Whether or not this command can be used in /city admin
     * @return True if it is, false if not
     */
    public abstract boolean isAdminExecutable();

    /**
     * Executes the sub command
     * @param commandSender The command sender
     * @param command The command
     * @param s Command name
     * @param strings Arguments
     * @param isAdminExec If the command was executed as a sub of /city admin cityName
     * @return true if executed. False if some syntax error occured and the usage should be printed
     */
    public abstract boolean execute(CommandSender commandSender, Command command, String s, String[] strings, boolean isAdminExec);

    /**
     * Returns a list of suggestions on tab complete, after the initial sub command name
     * @param commandSender The command sender
     * @param s The command name
     * @param args Entered arguments
     * @param startIndex Index of the start of the sub command
     * @return A list of suggestions
     */
    public abstract List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args, int startIndex);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CitySubCommand that = (CitySubCommand) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
