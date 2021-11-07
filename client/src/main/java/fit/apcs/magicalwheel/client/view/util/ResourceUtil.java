package fit.apcs.magicalwheel.client.view.util;

import java.net.URL;
import java.util.Optional;

public final class ResourceUtil {

    public static final String GAME_NAME = "Magical Wheel";

    private ResourceUtil() {

    }

    public static URL getImageURL(String imageName) {
        return Optional.ofNullable(ResourceUtil.class.getClassLoader().getResource(imageName))
                       .orElseThrow(() -> new RuntimeException("Cannot find image " + imageName));
    }

}
