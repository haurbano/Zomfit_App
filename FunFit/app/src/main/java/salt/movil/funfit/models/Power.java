package salt.movil.funfit.models;

/**
 * Created by Hamilton Urbano on 2/4/2017.
 */

public class Power {
    public final static String REDUCE_TIME_ACCTION = "reduce_time_acction";
    public final static String REMOVE_ENEMY_KEY = "remove_enemy_key";

    String accion;
    int value;
    int image;

    public Power(String accion, int value, int image) {
        this.accion = accion;
        this.value = value;
        this.image = image;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
