package Main;

import Serilazibles.City;
import Serilazibles.Vector2;
import org.bukkit.Bukkit;
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
import java.util.logging.Logger;

/**
 * This class manages all cities for the server. It loads them and saves them if wanted.
 */
public class CityManager {

    /**
     * Static instance of the city manager, should always be used to access it.
     */
    public static CityManager Static;

    /**
     * HashMap of cities, that currently exist on the server
     * with their name as key
     */
    private HashMap<String, City> cities;

    /**
     * Map of chunks and their data, used to store custom chunk data.
     * Only chunks which are part of a city are currently in here
     */
    private HashMap<Chunk, ChunkData> ownedChunks;

    /**
     * The path to the folder, where the cities are saved.
     */
    private Path cityFolder;

    /**
     * Reference to the plugins logger.
     */
    private Logger logger;

    public CityManager(JavaPlugin plugin){
        if(Static == null)
            Static = this;

        cityFolder = plugin.getDataFolder().toPath().resolve("Cities");
        cities = new HashMap<>();
        ownedChunks = new HashMap<>();
        logger = CitiesPlugin.PluginInstance.getLogger();
    }

    /**
     * Creates a new City with the given player as its creator
     * @param cityName Name of the city
     * @param creator Creator of the city
     * @return If the city was created successfully, false if one already exists with that name
     */
    public boolean createCity(String cityName, Player creator, HashSet<Chunk> cityChunks){
        if(cityExists(cityName)) return false;

        City city = new City(cityName, creator.getUniqueId(), creator.getWorld());

        for(Chunk c : cityChunks){
            city.addChunk(c);

            if(ownedChunks.containsKey(c))continue;

            ownedChunks.put(c, new ChunkData(c, city));
        }
        cities.put(cityName.toLowerCase(), city);
        SaveCities();
        return true;
    }

    /**
     * Removes the given city from the world.
     * @param cityName Name of the city
     * @return True, if city was removed, false if no such city exist
     */
    public boolean removeCity(String cityName){
        if(!cityExists(cityName))return false;
        City city = cities.get(cityName.toLowerCase());
        World world = Bukkit.getWorld(city.getCityWorld());
        if(world == null){
            logger.warning("The city " + cityName + " is no longer in a valid world. It will still be removed, its chunks wont be updated");
            cities.remove(cityName.toLowerCase());
            return true;
        }

        for(Vector2 chunk : city.getChunks()){
            Chunk c = world.getChunkAt(chunk.X, chunk.Y);
            ownedChunks.remove(c); //TODO: Change to more complex chunk system if needed, since this is way to simple
        }

        cities.remove(cityName.toLowerCase());

        return true;
    }

    /**
     * Adds a new resident to a city, if the player is not a resident in another city.
     * @param cityName City to add the player as a resident
     * @param resident Player to add as resident
     * @return True, if player was added, false if he is already a resident of another city or the city does not exist
     */
    public boolean addResidentToCity(String cityName, Player resident) {
        if(!cityExists(cityName))return false;
        if(getPlayerCity(resident) != null)return false;

        City city = cities.get(cityName.toLowerCase());
        city.addResident(resident.getUniqueId());
        return true;
    }

    /**
     * Removes the player from a cities residents.
     * @param cityName Name of the city
     * @param resident Player to remove
     * @return True, if removed, false if city does not exist or the player is no resident there.
     */
    public boolean removeResidentFromCity(String cityName, Player resident){
        if(!cityExists(cityName))return false;
        City city = cities.get(cityName);

        return city.getResidents().remove(resident.getUniqueId());
    }

    /**
     * Adds the given chunk to the given cities area, if it is not part of another city.
     * @param chunk Chunk to add
     * @param cityName Name of the city
     * @return True, if added, false if either the chunk is already part of a city, or the city does not exist.
     */
    public boolean addChunkToCity(Chunk chunk, String cityName){
        if(!cityExists(cityName))return false;
        if(ownedChunks.containsKey(chunk))return false;
        City city = cities.get(cityName.toLowerCase());
        ChunkData data = new ChunkData(chunk, city);
        ownedChunks.put(chunk, data);
        city.addChunk(chunk);

        return true;
    }

    /**
     * Removes a chunk from a city, if it is in its area.
     * @param chunk The chunk to remove from the city
     * @param cityName The city.
     * @return True, if removed, false if city does not exist or chunk has no data associated with
     */
    public boolean removeChunkFromCity(Chunk chunk, String cityName){
        if(!cityExists(cityName))return false;
        if(!ownedChunks.containsKey(chunk))return false;

        ChunkData data = ownedChunks.get(chunk);

        if(data.getCity() == null)return false;

        City city = cities.get(cityName);

        boolean ret = city.removeChunk(chunk);
        ownedChunks.remove(chunk);

        return ret;
    }

    /**
     * Tries to get the city of the player, where he resides.
     * @param resident The player to get the city for
     * @return The name of the city of the player or null, if he is no resident
     */
    public String getPlayerCity(Player resident){
        for(City city : cities.values()){
            if(city.getResidents().contains(resident.getUniqueId())){
                return city.getName();
            }
        }
        return null;
    }

    /**
     * Checks if the given city exists
     * @param cityName Name of the city
     * @return True if tit exists
     */
    public boolean cityExists(String cityName){
        return cities.containsKey(cityName.toLowerCase());
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
                logger.warning("The file " + file.getName() + " does not contain data for a city. It is skipped.");
                continue;
            }

            World w = CitiesPlugin.PluginInstance.getServer().getWorld(data.getCityWorld());
            if(w == null)continue;

            cities.put(data.getName().toLowerCase(), data);

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
        for(City city : cities.values()){
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
                logger.warning("City " + city.getName() + " could not be saved.");
            }
        }
    }

}
