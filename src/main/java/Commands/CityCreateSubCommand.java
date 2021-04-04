package Commands;

import Main.CitiesPlugin;
import Main.CityManager;
import Serilazibles.City;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.HashSet;

public class CityCreateSubCommand extends CitySubCommand {

    public CityCreateSubCommand(){
        super("create");
    }

    @Override
    public String getNeededPermission() {
        return null;
    }

    @Override
    public String getUsage() {
        return "/city create <name> [radius]";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(strings.length < 2)return false;
            String name = strings[1];
            int radius = CitiesPlugin.PluginInstance.getConfig().getInt("settings.cityDefaultRadius");

            if(strings.length == 3){
                try {
                    radius = Integer.parseInt(strings[2]);
                }catch (NumberFormatException e){
                    commandSender.sendMessage(strings[2] + " is not a valid radius. The radius must be an integer.");
                }
            }
            HashSet<Chunk> chunks = GetValidCityChunks(p.getLocation().getChunk(), radius);
            if(chunks.size() == (2 * radius - 1) * (2 * radius - 1)){
                City c = CityManager.Static.createCity(name, p, chunks);
                if(c != null){
                    commandSender.sendMessage("City " + name + "created.");
                    //Spawn effect to see it
                }
            }
            else{
                commandSender.sendMessage("Cant create city, some chunks seem to be already owned. Please reduce the radius, or move to another location.");
            }

        }
        else{
            commandSender.sendMessage("You cant create a city, if you are not a player");
        }

        return true;
    }

    private HashSet<Chunk> GetValidCityChunks(Chunk center, int radius){
        HashSet<Chunk> chunks = new HashSet<>();
        for(int x = -(radius - 1); x < radius; x++){
            for(int y = -(radius - 1); y < radius; y++){
                Chunk c = center.getWorld().getChunkAt(x + center.getX(), y + center.getZ());
                if(CityManager.Static.getCity(c) == null){
                    chunks.add(c);
                }
            }
        }

        return chunks;
    }
}
