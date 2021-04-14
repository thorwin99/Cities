package Main;

import Serilazibles.City;
import org.bukkit.Chunk;

/***
 * Class representing custom data for the given Chunk
 */
public class ChunkData {

    /**
     * The city this chunk belongs to, if any
     */
    private City city;

    /**
     * The base chunk
     */
    private Chunk chunk;

    public ChunkData(Chunk chunk, City city){
        this.city = city;
        this.chunk = chunk;
    }

    /**
     * Gets the city of the chunk
     * @return The city of the chunk or null, if it doesnt have one
     */
    public City getCity() {
        return city;
    }

    /**
     * Gets the base bukkit chunk
     * @return The base chunk, this data belongs to
     */
    public Chunk getChunk() {
        return chunk;
    }

    /**
     * Sets the city of this chunk
     * @param city City to set
     */
    public void setCity(City city) {
        this.city = city;
    }
}
