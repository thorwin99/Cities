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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
    private final HashMap<String, City> cities;

    private final ChunkManager chunkManager;

    /**
     * The path to the folder, where the cities are saved.
     */
    private final Path cityFolder;

    /**
     * Reference to the plugins logger.
     */
    private final Logger logger;

    public CityManager(JavaPlugin plugin){
        if(Static == null)
            Static = this;

        cityFolder = plugin.getDataFolder().toPath().resolve("Cities");
        cities = new HashMap<>();
        chunkManager = new ChunkManager();
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

        City city = new City(cityName, creator.getWorld());

        for(Chunk c : cityChunks){
            city.addChunk(c);

            if(!chunkManager.isClaimable(cityName, c)) continue;
            if(chunkManager.hasChunkData(c)){
                ChunkData data = chunkManager.getChunkData(c);
                data.setCity(city);
            }
            else{
                chunkManager.createChunkData(c).setCity(city);
            }
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
        City city = getCity(cityName);
        World world = Bukkit.getWorld(city.getCityWorld());
        if(world == null){
            logger.warning("The city " + cityName + " is no longer in a valid world. It will still be removed, its chunks wont be updated");
            cities.remove(cityName.toLowerCase());
            SaveCities();
            return true;
        }

        for(Vector2 chunk : city.getChunks()){
            Chunk c = world.getChunkAt(chunk.X, chunk.Y);
            ChunkData data = chunkManager.getChunkData(c);
            if(data != null && data.getCity() == city) data.setCity(null);
        }

        cities.remove(cityName.toLowerCase());
        deleteCityFile(cityName);
        SaveCities();
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

        City city = getCity(cityName);
        city.addResident(resident.getUniqueId());
        SaveCities();
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
        City city = getCity(cityName);

        SaveCities();
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
        if(!chunkManager.isClaimable(cityName, chunk)) return false;
        City city = getCity(cityName);

        chunkManager.createChunkData(chunk).setCity(city);

        city.addChunk(chunk);

        SaveCities();
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

        if(!chunkManager.hasChunkData(chunk))return false;

        ChunkData data = chunkManager.getChunkData(chunk);

        if(data.getCity() == null)return false;

        City city = getCity(cityName);

        boolean ret = city.removeChunk(chunk);
        data.setCity(null);

        SaveCities();
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
     * Checks if the player is resident of the given city
     * @param cityName City name
     * @param player Player to check
     * @return True if he is a resident, false if either the city does not exist, or the player is no resident.
     */
    public boolean playerIsResident(String cityName, Player player){
        if(!cityExists(cityName))return false;
        City city = getCity(cityName);
        return city.isResident(player.getUniqueId());
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
     * Tries to get the city of a chunk
     * @param chunk Chunk to check
     * @return The name of the City of the chunk or null
     */
    public String getCity(Chunk chunk){
        if(!chunkManager.hasChunkData(chunk))return null;

        return chunkManager.getChunkData(chunk).getCity().getName();
    }

    /**
     * Gets all cities
     * @return Returns a list of all names for the cities.
     */
    public List<String> getCities(){
        return new ArrayList<>(cities.keySet());
    }

    /**
     * Tries to get all residents of the given city
     * @param cityName Name of the city
     * @return A list of uuids of all the cities residents or null if it does not exist.
     */
    public List<UUID> getCityResidents(String cityName){
        if(!cityExists(cityName))return null;
        return new LinkedList<>(getCity(cityName).getResidents());
    }

    /**
     * Tries to get all chunks of the given city
     * @param cityName Name of the city
     * @return A list of vector2s for all chunk coordinates contained in the city.
     */
    public List<Vector2> getCityChunks(String cityName){
        if(!cityExists(cityName))return null;
        return new ArrayList<>(getCity(cityName).getChunks());
    }

    /**
     * Tries to the world of the city
     * @param cityName Name of the city
     * @return The name of the city world
     */
    public String getCityWorld(String cityName){
        if(!cityExists(cityName))return null;
        return getCity(cityName).getCityWorld();
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

                if(!chunkManager.hasChunkData(c)){
                    chunkManager.createChunkData(c).setCity(data);
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

    /**
     * Deletes the save file of a city
     * @param city City to delete
     * @return True if deleted.
     */
    private boolean deleteCityFile(String city){
        File folder = cityFolder.toFile();
        if(!folder.exists()){
            return false;
        }

        String path = cityFolder.resolve(city).toAbsolutePath().toString();
        File f = new File(path);
        if(f.exists()){
            return f.delete();
        }
        return false;
    }

    /**
     * Gets a city if possible
     * @param cityName Name of the city
     * @return The city or null if not existend
     */
    private City getCity(String cityName){
        return cities.get(cityName.toLowerCase());
    }
}
