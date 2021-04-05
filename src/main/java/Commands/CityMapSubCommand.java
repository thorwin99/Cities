package Commands;

import Main.ChunkData;
import Main.CitiesPlugin;
import Main.CityManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * City sub command to show a scoreboard with chunks marked as city ones or unclaimed yet.
 */
public class CityMapSubCommand extends CitySubCommand {

    /**
     * The scoreboard display name
     */
    private static final String SCOREBOARDNAME = "City chunk map";

    /**
     * The objective name for the chunk map
     */
    private static final String OBJECTIVENAME = "CityChunkMap";

    /**
     * The unicode char for a square representing a chunk
     */
    private static final char CHUNK_SYM = '\u2588';

    /**
     * The unicode char sfor the square representing the chunk where the player is in and his direction
     */
    private static final char[] CHUNK_SYM_CENTER = {'\u25B2', '\u25E5', '\u25B6', '\u25E2', '\u25BC', '\u25E3', '\u25C0', '\u25E4'};

    /**
     * The size of the map in chunks
     */
    private static final int MAP_SIZE = 11;

    public CityMapSubCommand() {
        super("map");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.map";
    }

    @Override
    public String getUsage() {
        return "/city map";
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            final Player p = (Player) commandSender;

            if(!p.hasPermission("cities.city.map")){
                commandSender.sendMessage("You dont have the required permission to use it.");
            }

            Scoreboard scoreboard = p.getScoreboard();
            if(scoreboard.getObjective(OBJECTIVENAME) != null){
                scoreboard.getObjective(OBJECTIVENAME).unregister();
            }
            else if(scoreboard.getObjective(DisplaySlot.SIDEBAR) == null){
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective mapObjective = scoreboard.registerNewObjective(OBJECTIVENAME,"dummy", SCOREBOARDNAME);
                mapObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

                for(int i = 1; i <= MAP_SIZE; i++){
                    Team row = scoreboard.getTeam("r" + i);
                    if(row == null)
                        row = scoreboard.registerNewTeam("r" + i);

                    row.setPrefix(ChatColor.WHITE + "");
                    row.addEntry(ChatColor.RED + "" + (i - 1 - MAP_SIZE / 2));
                    mapObjective.getScore(ChatColor.RED + "" + (i - 1 - MAP_SIZE / 2)).setScore(i);
                }

                p.setScoreboard(scoreboard);
                new BukkitRunnable(){

                    @Override
                    public void run() {
                        if(p.getScoreboard().getObjective(OBJECTIVENAME) == null){
                            cancel();
                            return;
                        }
                        UpdateMap(p);
                    }
                }.runTaskTimer(CitiesPlugin.PluginInstance, 0, 5);
            }
            else{
                commandSender.sendMessage(ChatColor.RED + "Could not create scoreboard to visualize chunks :(");
            }
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "Only players can run this command");
        }
        return true;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, Command command, String s, String[] args) {
        return new ArrayList<>();
    }

    /**
     * Called when the scoreboard should update the map for player p
     * @param p The player
     */
    public void UpdateMap(Player p){
        Scoreboard s = p.getScoreboard();
        Objective chunkMap = s.getObjective(OBJECTIVENAME);
        if(chunkMap == null)return;

        Chunk center = p.getLocation().getChunk();
        int dir = getDirection(p);
        for(int y = 0; y < MAP_SIZE; y++){
            Team row = s.getTeam("r" + (y + 1));
            String suffix = "";

            for(int x = 0; x < MAP_SIZE; x++){
                Chunk c = center.getWorld().getChunkAt(center.getX() + (MAP_SIZE / 2) - x, center.getZ() - (MAP_SIZE / 2) + y);
                ChunkData data = CityManager.Static.getChunkData(c);
                char sym = c == center ? CHUNK_SYM_CENTER[dir] : CHUNK_SYM;
                if(data == null){
                    suffix += "" + ChatColor.GRAY + sym;
                }
                else if(data.getCity() != null){
                    suffix += "" + ChatColor.RED + sym;
                }
            }
            chunkMap.getScore(ChatColor.RED + "" + (y - MAP_SIZE / 2)).setScore(center.getZ() + (y - 1 - MAP_SIZE / 2));
            row.setPrefix(suffix);
        }
    }

    /**
     * Gets the viewing direction of the player, where 0 is south, and 3 is east
     * @param p The player
     * @return The direction as an integer value
     */
    private int getDirection(Player p){
        long angle = ((long)p.getLocation().getYaw()) % 360;
        angle = angle < 0 ? angle + 360 : angle;

        int result = Math.round((float)angle / 45f);
        result %= CHUNK_SYM_CENTER.length;
        result = result < 0 ? 0 : result;
        return result;
    }
}
