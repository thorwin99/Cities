package Serilazibles;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing a 2D Vector
 */
public class Vector2 implements ConfigurationSerializable {

    public int X;
    public int Y;

    /**
     * Creates the zero vector
     */
    public Vector2(){
        X = 0;
        Y = 0;
    }

    /**
     * Creates a new Vector2 with given coordinates
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Vector2(int x, int y){
        X = x;
        Y = y;
    }

    /**
     * Deserialization constructor
     * @param values Map of member variables. "X" is X, "Y" is Y
     */
    public Vector2(Map<String, Object> values){
        X = (int)values.get("X");
        Y = (int)values.get("Y");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return X == vector2.X &&
                Y == vector2.Y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> values = new HashMap<>();
        values.put("X", X);
        values.put("Y", Y);
        return values;
    }
}
