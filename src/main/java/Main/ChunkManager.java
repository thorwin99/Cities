package Main;

import Serilazibles.Vector2;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

/**
 * Manager used for chunk data associated to chunks by the plugin.
 */
public class ChunkManager {

    /**
     * A map that maps the worlds to a map for the chunks coordinate to the associated chunk data.
     */
    private HashMap<String, HashMap<Vector2, ChunkData>> recordedChunks;

    /**
     * Singleton instance
     */
    public static ChunkManager Static;

    public ChunkManager()
    {
        if(Static == null){
            Static = this;
            recordedChunks = new HashMap<>();
        }else{
            recordedChunks = Static.recordedChunks;
        }
    }

    /**
     * Checks if the given chunk has any data attached.
     * @param chunk The chunk to check
     * @return True if it has chunk data attached.
     */
    public boolean hasChunkData(Chunk chunk){
        if(recordedChunks.containsKey(chunk.getWorld().getName())){
            return recordedChunks.get(chunk.getWorld().getName()).containsKey(new Vector2(chunk.getX(), chunk.getZ()));
        }
        return false;
    }

    /**
     * Returns the attached chunk data
     * @param chunk The chunk to get data for
     * @return The chunk data or null
     */
    public ChunkData getChunkData(Chunk chunk){
        if(hasChunkData(chunk)){
            return recordedChunks.get(chunk.getWorld().getName()).get(new Vector2(chunk.getX(), chunk.getZ()));
        }
        return null;
    }

    /**
     * Creates new chunk data for the given chunk, if it has none
     * @param chunk Chunk to create data for
     * @return The new data if the chunk data was created, or the old data if it already has data attached
     */
    public ChunkData createChunkData(Chunk chunk){
        if(hasChunkData(chunk)) return getChunkData(chunk);
        ChunkData data = new ChunkData(chunk, null);

        if(!recordedChunks.containsKey(chunk.getWorld().getName())){
            recordedChunks.put(chunk.getWorld().getName(), new HashMap<Vector2, ChunkData>());
        }
        recordedChunks.get(chunk.getWorld().getName()).put(new Vector2(chunk.getX(), chunk.getZ()), data);

        return data;
    }

    /**
     * Checks if the given chunk can be claimed for the given city
     * @param chunk Chunk to be claimed
     * @param city City to check
     * @return True if it can be claimed, false otherwise.
     */
    public boolean isClaimable(String city, Chunk chunk){
        if(hasChunkData(chunk)){
            if(getChunkData(chunk).getCity() != null)return false;
        }
        if(!recordedChunks.containsKey(chunk.getWorld().getName()))return true;

        Vector2 chunkCoords = new Vector2(chunk.getX(), chunk.getZ());
        int buf = CitiesPlugin.PluginInstance.getConfig().getInt("settings.chunkBufferRadius");
        for(Vector2 coords : recordedChunks.get(chunk.getWorld().getName()).keySet()){
            int dx = coords.X - chunkCoords.X;
            int dy = coords.Y - chunkCoords.Y;
            if(Math.abs(dx) <= buf && Math.abs(dy) <= buf){
                ChunkData data = getChunkData(chunk);

                if(data != null && data.getCity() != null && data.getCity().getName().equals(city)) continue;

                CitiesPlugin.PluginInstance.getLogger().warning("Chunk is in buffer zone, cant claim it.");
                return false;
            }
        }

        return true;
    }
}
