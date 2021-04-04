package Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;

/**
 * Abstract class for sub commands of the city command
 */
public abstract class CitySubCommand {

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
     * Executes the sub command
     * @param commandSender The command sender
     * @param command The command
     * @param s Command name
     * @param strings Arguments
     * @return true if executed successfully.
     */
    public abstract boolean execute(CommandSender commandSender, Command command, String s, String[] strings);

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
