package Serilazibles;

import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class CityManager {

    public static CityManager Static;

    private List<City> cities;
    private Path cityFolder;

    public CityManager(JavaPlugin plugin){
        if(Static == null)
            Static = this;

        cityFolder = plugin.getDataFolder().toPath().resolve("Cities");
    }

    /**
     * Tries to get the city of a chunk
     * @param chunk Chunk to check
     * @return The City of the chunk or null
     */
    public City getCity(Chunk chunk){
        for(City city : cities){
            if(city.containsChunk(chunk)){
                return city;
            }
        }
        return null;
    }

    /**
     * Loads all cities from the city data folder
     */
    public void LoadCities(){
        File folder = cityFolder.toFile();
        for(File file : folder.listFiles()){
            City data = City.LoadData(file.getAbsolutePath());
            if(data == null){
                //Error handling goes here
                continue;
            }
            cities.add(data);
        }
    }

    /**
     * Saves all known cities.
     */
    public void SaveCities(){
        for(City city : cities){
            if(!city.SaveData(cityFolder.resolve(city.getName()).toAbsolutePath().toString())){
                //Handle error
            }
        }
    }

}
