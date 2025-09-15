package awildgoose.gooselights.lights;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehaviorManager;

import java.util.*;
import java.util.function.Consumer;

/**
 * Light manager to add/remove/get lights in the world
 */
@SuppressWarnings("unused")
public class LightManager {
    private static volatile DynamicLightBehaviorManager dlManager;
    private static final Set<DynamicLightBehavior> lights =
            Collections.synchronizedSet(new HashSet<>());
    private static final List<Consumer<DynamicLightBehaviorManager>> dlQueue =
            Collections.synchronizedList(new ArrayList<>());

    /**
     * Internal function to set the DynamicLightBehaviorManager
     * @param dl DL Manager
     */
    public static synchronized void setDlManager(DynamicLightBehaviorManager dl) {
        dlManager = dl;
        for (Consumer<DynamicLightBehaviorManager> consumer : dlQueue)
            consumer.accept(dl);
        dlQueue.clear();
    }

    /**
     * Adds a light to the world
     * NOTE: Make sure you're calling this when the world has already initialized!
     * @param light Light to add
     */
    public static void addLight(DynamicLightBehavior light) {
        withDlManager(dl -> dl.add(light));
        lights.add(light);
    }

    /**
     * Removes a light from the world
     * @param light Light to remove
     */
    public static void removeLight(DynamicLightBehavior light) {
        withDlManager(dl -> dl.remove(light));
        lights.remove(light);
    }

    /**
     * Returns all active lights (from this API)
     * @return All active lights
     */
    public static Set<DynamicLightBehavior> getLights() {
        return lights;
    }

    private static void withDlManager(Consumer<DynamicLightBehaviorManager> consumer) {
        DynamicLightBehaviorManager current = dlManager;
        if (current != null) {
            consumer.accept(current);
        } else {
            dlQueue.add(consumer);
        }
    }
}