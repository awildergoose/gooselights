package awildgoose.gooselights.lights;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehaviorManager;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class LightManager {
    private static volatile DynamicLightBehaviorManager dlManager;
    private static final Set<DynamicLightBehavior> lights =
            Collections.synchronizedSet(new HashSet<>());
    private static final List<Consumer<DynamicLightBehaviorManager>> dlQueue =
            Collections.synchronizedList(new ArrayList<>());

    public static synchronized void setDlManager(DynamicLightBehaviorManager dl) {
        dlManager = dl;
        for (Consumer<DynamicLightBehaviorManager> consumer : dlQueue)
            consumer.accept(dl);
        dlQueue.clear();
    }

    public static void addLight(DynamicLightBehavior light) {
        withDlManager(dl -> dl.add(light));
        lights.add(light);
    }

    public static void removeLight(DynamicLightBehavior light) {
        withDlManager(dl -> dl.remove(light));
        lights.remove(light);
    }

    private static void withDlManager(Consumer<DynamicLightBehaviorManager> consumer) {
        DynamicLightBehaviorManager current = dlManager;
        if (current != null) {
            consumer.accept(current);
        } else {
            dlQueue.add(consumer);
        }
    }

    public static Set<DynamicLightBehavior> getLights() {
        return lights;
    }
}