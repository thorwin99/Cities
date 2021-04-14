package Commands;

import Main.ChunkManager;
import Main.CitiesPlugin;
import Main.CityManager;
import Serilazibles.City;
import Serilazibles.Vector2;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * City sub command to add and remove chunks from a city.
 */
public class CityChunkSubCommand extends CitySubCommand {

    private HashMap<UUID, Chunk> chunkStartMap;
    private HashMap<UUID, Chunk> chunkEndMap;

    public CityChunkSubCommand() {
        super("chunk");
        chunkStartMap = new HashMap<>();
        chunkEndMap = new HashMap<>();
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.chunks";
    }

    @Override
    public String getUsage() {
        return "/city chunk <add|remove|start|end|cancel>";
    }

    @Override
    public boolean isAdminExecutable() {
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings, boolean isAdminExec) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            int argOffset = 1;
            if(isAdminExec){
                argOffset = 3;
            }

            if(strings.length != argOffset + 1)return false;
            String action = strings[argOffset];

            String city = isAdminExec ? strings[1] : CityManager.Static.getPlayerCity(player);

            return runCommand(commandSender, player, action, city);
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "This command has to be run as a player.");
            return true;
        }
    }

    private boolean runCommand(CommandSender commandSender, Player player, String action, String city) {
        if(city == null){
            commandSender.sendMessage(ChatColor.RED + "You are not resident of a city, you cant do that.");
            return true;
        }

        switch (action) {
            case "add":
                actionAdd(player, city);
                return true;
            case "remove":
                actionRemove(player, city);
                return true;
            case "start":
                actionStart(player, city);
                return true;
            case "end":
                actionEnd(player, city);
                return true;
            case "cancel":
                actionCancel(player);
                return true;
        }
        return false;
    }

    /**
     * Cancels area add for the player
     * @param player Player to cancel area select
     */
    private void actionCancel(Player player){
        chunkStartMap.remove(player.getUniqueId());
        chunkEndMap.remove(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "Chunk selection canceled");
    }

    /**
     * Ends area add for the player
     * @param player Player to end area
     * @param city City of the player
     */
    private void actionEnd(Player player, String city){
        Chunk chunk = player.getLocation().getChunk();
        String chunkCity = CityManager.Static.getCity((chunk));
        if(chunkCity != null && !chunkCity.equals(city)){
            player.sendMessage(ChatColor.RED + "This chunk already is part of another city.");
            return;
        }

        chunkEndMap.put(player.getUniqueId(), chunk);
        player.sendMessage(ChatColor.GREEN + "Chunk selected as end.");
    }

    /**
     * Starts area add for the player
     * @param player Player to start area
     * @param city City of the player
     */
    private void actionStart(Player player, String city){
        Chunk chunk = player.getLocation().getChunk();
        String chunkCity = CityManager.Static.getCity((chunk));
        if(chunkCity != null && !chunkCity.equals(city)){
            player.sendMessage(ChatColor.RED + "This chunk already is part of another city.");
            return;
        }

        chunkStartMap.put(player.getUniqueId(), chunk);
        player.sendMessage(ChatColor.GREEN + "Chunk selected as start.");
    }

    /**
     * The action to remove a chunk from a city.
     * @param player Player that run the command
     * @param city City of the player
     */
    private void actionRemove(Player player, String city) {
        if(chunkStartMap.containsKey(player.getUniqueId())){
            if(chunkEndMap.containsKey(player.getUniqueId())){
                Chunk start = chunkStartMap.get(player.getUniqueId());
                Chunk end = chunkEndMap.get(player.getUniqueId());
                if(start.getWorld() != end.getWorld()){
                    player.sendMessage(ChatColor.RED + "Chunks have to be in the same world.");
                    return;
                }

                List<Chunk> chunks = getValidChunksOfArea(start, end, city);
                int removed = 0;
                for(Chunk chunk : chunks){
                    if(CityManager.Static.removeChunkFromCity(chunk, city)) removed++;
                }
                player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.GRAY + removed + ChatColor.GREEN + " to the city " + ChatColor.YELLOW + city);
                chunkStartMap.remove(player.getUniqueId());
                chunkEndMap.remove(player.getUniqueId());
                return;
            }
            else{
                player.sendMessage(ChatColor.RED + "You have to select an end chunk with /city chunk end.");
                return;
            }
        }
        Chunk c = player.getLocation().getChunk();
        if (CityManager.Static.removeChunkFromCity(c, city)) {
            player.sendMessage(ChatColor.GREEN + "Chunk removed successfully from city " + ChatColor.YELLOW + city + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.RED + "This chunk does not belong to a city.");
        }
    }

    /**
     * The action to add a chunk to a city.
     * @param player Player that run the command
     * @param city City of the player
     */
    private void actionAdd(Player player, String city) {
        int limit = CitiesPlugin.PluginInstance.getConfig().getInt("settings.cityChunkLimit");

        if(chunkStartMap.containsKey(player.getUniqueId())){
            if(chunkEndMap.containsKey(player.getUniqueId())){
                Chunk start = chunkStartMap.get(player.getUniqueId());
                Chunk end = chunkEndMap.get(player.getUniqueId());
                if(start.getWorld() != end.getWorld()){
                    player.sendMessage(ChatColor.RED + "Chunks have to be in the same world.");
                    return;
                }
                List<Chunk> chunks = getValidChunksOfArea(start, end, city);
                int added = 0;if(chunks.size() > limit || chunks.size() + CityManager.Static.getCityChunks(city).size() > limit){
                    player.sendMessage(ChatColor.RED + "Cannot add selected chunks, because adding those will exceed the cities chunk limit of " + limit + ".");
                    return;
                }

                for(Chunk chunk : chunks){
                    if(CityManager.Static.addChunkToCity(chunk, city)) added++;
                }
                player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.GRAY + added + ChatColor.GREEN + " to the city " + ChatColor.YELLOW + city);
                chunkStartMap.remove(player.getUniqueId());
                chunkEndMap.remove(player.getUniqueId());
                return;
            }
            else{
                player.sendMessage(ChatColor.RED + "You have to select an end chunk with /city chunk end.");
                return;
            }
        }

        Chunk c = player.getLocation().getChunk();

        if(CityManager.Static.getCityChunks(city).size() + 1 > limit){
            player.sendMessage(ChatColor.RED + "Cannot add the chunk to the city, because adding it will exceed the cities chunk limit of " + limit + ".");
            return;
        }

        if (CityManager.Static.addChunkToCity(c, city)) {
            player.sendMessage(ChatColor.GREEN + "Chunk added successfully to city " + ChatColor.YELLOW + city + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.RED + "This chunk already belongs to a city or is to close to another city.");
        }
    }

    /**
     * Returns a list of all valid chunks, that can be added to city between start and end
     * @param start Start chunk
     * @param end End chunk
     * @param city City name
     * @return List of chunks
     */
    private List<Chunk> getValidChunksOfArea(Chunk start, Chunk end, String city){
        int xStart = Math.max(start.getX(), end.getX());
        int xEnd = Math.min(start.getX(), end.getX());

        int zStart = Math.max(start.getZ(), end.getZ());
        int zEnd = Math.min(start.getZ(), end.getZ());

        List<Chunk> coords = new LinkedList<>();

        if(start == end){
            CitiesPlugin.PluginInstance.getLogger().info("Start equals end");
            if(ChunkManager.Static.isClaimable(city, start)){
                coords.add(start);
            }
            return coords;
        }

        for(int x = xStart; x >= xEnd; x--){
            for(int z = zStart; z >= zEnd; z--){
                CitiesPlugin.PluginInstance.getLogger().info("Chunk " + x + " " + z);
                Chunk c = start.getWorld().getChunkAt(x, z);
                if(ChunkManager.Static.isClaimable(city, c)){
                    coords.add(c);
                }
            }
        }

        return coords;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args, int startIndex) {
        if(args.length != startIndex + 2)return new LinkedList<>();

        String start = args[startIndex + 1];
        List<String> suggestions = new ArrayList<>();
        if("add".startsWith(start))suggestions.add("add");
        if("remove".startsWith(start))suggestions.add("remove");
        if("start".startsWith(start))suggestions.add("start");
        if("end".startsWith(start))suggestions.add("end");
        if("cancel".startsWith(start))suggestions.add("cancel");
        return suggestions;
    }
}
