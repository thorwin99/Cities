package Serilazibles;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Serializable class for a city. A city is defined by its chunks
 * and its residents. It has a name and belongs to only one world.
 */
public class City implements Serializable {
    private static final long serialVersionUID = -179363563369893866L;

    /**
     * A set of vectors of chunk coordinates for all chunks of the city area
     */
    private HashSet<Vector2> chunks;

    /**
     * The uuids of the residents
     */
    private HashSet<UUID> residents;
    private String cityName;
    private String cityWorld;

    protected City(){
        residents = new HashSet<>();
        cityName = "";
        cityWorld = "";
        chunks = new HashSet<>();
    }

    /**
     * Creates a new city with the given creator as first resident
     * @param name Name of the city
     * @param creator Creator of the city
     */
    public City(String name, UUID creator, World world){
        residents = new HashSet<>();
        residents.add(creator);
        cityName = name;
        cityWorld = world.getName();
        chunks = new HashSet<>();
    }

    /**
     * Adds a new chunk to the city
     * @param chunk The chunk to add
     * @return True if chunk was added successfully.
     */
    public boolean addChunk(Chunk chunk){
        Vector2 coordinates = new Vector2(chunk.getX(), chunk.getZ());
        if(chunks.contains(coordinates))return false;

        chunks.add(coordinates);
        return true;
    }

    /**
     * Gets the name of the city
     * @return The city name
     */
    public String getName(){
        return cityName;
    }

    /**
     * Gets all residents of the city
     * @return Residents of the city
     */
    public HashSet<UUID> getResidents(){
        return residents;
    }

    /**
     * Returns the x y coordinates of all chunks in the city.
     * @return The chunk coords.
     */
    public HashSet<Vector2> getChunks(){
        return chunks;
    }

    /**
     * Returns the world, the city is in
     * @return The world
     */
    public String getCityWorld(){
        return cityWorld;
    }

    /**
     * Checks if player is resident
     * @param playerId Player id of player
     * @return true if he is a resident
     */
    public boolean isResident(UUID playerId){
        return residents.contains(playerId);
    }

    /**
     * Checks if the given chunk is present in this city
     * @param chunk Chunk to check
     * @return true if it is present in this city
     */
    public boolean containsChunk(Chunk chunk){
        if(!chunk.getWorld().getName().equals(cityWorld))return false;
        Vector2 coords = new Vector2(chunk.getX(), chunk.getZ());

        return chunks.contains(coords);
    }

    /**
     * Loads a city object from the file at filePath
     * @param filePath Path to the file containing this object
     * @return null if it cant be loaded, otherwise the serialized object
     */
    public static City LoadData(String filePath){
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            City data = (City) in.readObject();
            in.close();

            return data;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves this object to filepath
     * @param filePath Path to the file for this object
     * @return true if save was successful
     */
    public boolean SaveData(String filePath){
        try{
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
}
