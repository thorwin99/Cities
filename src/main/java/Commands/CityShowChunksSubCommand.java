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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class CityShowChunksSubCommand extends CitySubCommand implements Listener {

    private static final String SCOREBOARDNAME = "City chunk map";
    private static final String OBJECTIVENAME = "CityChunkMap";
    private static final char CHUNK_SYM = '\u2588';
    private static final int MAP_SIZE = 11;

    public CityShowChunksSubCommand() {
        super("chunks");
    }

    @Override
    public String getNeededPermission() {
        return "cities.city.map";
    }

    @Override
    public String getUsage() {
        return "/city chunks";
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
                        OnPlayerMove(p);
                    }
                }.runTaskTimer(CitiesPlugin.PluginInstance, 0, 20);
            }
            else{
                commandSender.sendMessage(ChatColor.RED + "Could not create scoreboard to visualize chunks :(");
            }
        }
        else{
            commandSender.sendMessage(ChatColor.RED + "Only players can run this command");
        }
        return false;
    }

    //@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void OnPlayerMove(Player p){
        //Player p = e.getPlayer();

        Scoreboard s = p.getScoreboard();
        Objective chunkMap = s.getObjective(OBJECTIVENAME);

        if(chunkMap == null)return;

        Chunk center = p.getLocation().getChunk();

        for(int y = 0; y < MAP_SIZE; y++){
            Team row = s.getTeam("r" + (y + 1));
            String suffix = "";
            for(int x = 0; x < MAP_SIZE; x++){
                Chunk c = center.getWorld().getChunkAt(center.getX() - 2 + x, center.getZ() - 2 + y);
                ChunkData data = CityManager.Static.getChunkData(c);
                if(data == null){
                    suffix += "" + ChatColor.GRAY + CHUNK_SYM;
                }
                else if(data.getCity() != null){
                    suffix += "" + ChatColor.RED + CHUNK_SYM;
                }
            }
            chunkMap.getScore(ChatColor.RED + "" + (y - MAP_SIZE / 2)).setScore(center.getZ() + (y - 1 - MAP_SIZE / 2));
            row.setPrefix(suffix);
        }
    }
}
