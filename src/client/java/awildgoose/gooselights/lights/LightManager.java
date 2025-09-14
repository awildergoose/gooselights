package awildgoose.gooselights.lights;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private static final List<Light> lights = new ArrayList<>();

    public static void addLight(Light light) {
        synchronized (lights) {
            lights.add(light);
        }
    }

    public static void removeLight(Light light) {
        synchronized (lights) {
            lights.remove(light);
        }
    }

    public static List<Light> getLights() {
        synchronized (lights) {
            return new ArrayList<>(lights);
        }
    }

    public static void clear() {
        synchronized (lights) {
            lights.clear();
        }
    }
}
