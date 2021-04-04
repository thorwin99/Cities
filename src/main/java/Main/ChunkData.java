package Main;

import Serilazibles.City;
import org.bukkit.Chunk;

/***
 * Class representing custom data for the given Chunk
 */
public class ChunkData {

    private City city;
    private Chunk chunk;

    public ChunkData(Chunk chunk, City city){
        this.city = city;
        this.chunk = chunk;
    }

    public City getCity() {
        return city;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
