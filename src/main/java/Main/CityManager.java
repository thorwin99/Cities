package Main;

import Serilazibles.City;
import Serilazibles.Vector2;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This class manages all cities for the server. It loads them and saves them if wanted.
 */
public class CityManager {

    public static CityManager Static;

    private HashSet<City> cities;
    private HashMap<Chunk, ChunkData> ownedChunks;
    private Path cityFolder;

    public CityManager(JavaPlugin plugin){
        if(Static == null)
            Static = this;

        cityFolder = plugin.getDataFolder().toPath().resolve("Cities");
        cities = new HashSet<>();
        ownedChunks = new HashMap<>();
    }

    /**
     * Creates a new City with the given player as its creator
     * @param cityName Name of the city
     * @param creator Creator of the city
     * @return The created city
     */
    public City createCity(String cityName, Player creator, HashSet<Chunk> cityChunks){
        City city = new City(cityName, creator.getUniqueId(), creator.getWorld());

        for(Chunk c : cityChunks){
            city.addChunk(c);

            if(ownedChunks.containsKey(c))continue;

            ownedChunks.put(c, new ChunkData(c, city));
        }
        cities.add(city);
        SaveCities();
        return city;
    }

    /**
     * Returns the chunk data for the chunk, if any
     * @param chunk Chunk to retreive data for
     * @return The chunk data or null if not existent
     */
    public ChunkData getChunkData(Chunk chunk){
        if(ownedChunks.containsKey(chunk))return ownedChunks.get(chunk);
        return null;
    }

    /**
     * Tries to get the city of a chunk
     * @param chunk Chunk to check
     * @return The City of the chunk or null
     */
    public City getCity(Chunk chunk){
        if(!ownedChunks.containsKey(chunk))return null;

        return ownedChunks.get(chunk).getCity();
    }

    /**
     * Loads all cities from the city data folder
     */
    public void LoadCities(){
        File folder = cityFolder.toFile();
        if(!folder.exists())return;
        if(folder.listFiles() == null)return;

        for(File file : folder.listFiles()){
            City data = City.LoadData(file.getAbsolutePath());
            if(data == null){
                //Error handling goes here
                continue;
            }

            World w = CitiesPlugin.PluginInstance.getServer().getWorld(data.getCityWorld());
            if(w == null)continue;

            cities.add(data);

            for(Vector2 coordinates : data.getChunks()){
                Chunk c = w.getChunkAt(coordinates.X, coordinates.Y);
                if(c == null) continue;

                if(!ownedChunks.containsKey(c)){
                    ChunkData d = new ChunkData(c, data);
                    ownedChunks.put(c, d);
                }
            }
        }
    }

    /**
     * Saves all known cities.
     */
    public void SaveCities(){
        File folder = cityFolder.toFile();
        if(!folder.exists()){
            try {
                Files.createDirectories(cityFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(City city : cities){
            String path = cityFolder.resolve(city.getName()).toAbsolutePath().toString();
            File f = new File(path);
            if(!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(!city.SaveData(path)){
                //Handle error
            }
        }
    }

}
