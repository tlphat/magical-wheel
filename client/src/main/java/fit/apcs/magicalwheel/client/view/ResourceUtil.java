package fit.apcs.magicalwheel.client.view;

import java.awt.GridBagConstraints;
import java.net.URL;
import java.util.Optional;

public final class ResourceUtil {

    public static final String GAME_NAME = "Magical Wheel";
    public static GridBagConstraints gbc;

    private ResourceUtil() {
        gbc = new GridBagConstraints();
    }

    public static URL getImageURL(String imageName) {
        return Optional.ofNullable(ResourceUtil.class.getClassLoader().getResource(imageName))
                       .orElseThrow(() -> new RuntimeException("Cannot find image " + imageName));
    }
}
