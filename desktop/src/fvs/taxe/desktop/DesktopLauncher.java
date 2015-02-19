package fvs.taxe.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fvs.taxe.TaxeGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //Set window size
        config.width = 800;
        config.height = 600;
        config.title = "TaxE";
        config.resizable = true;
        config.addIcon("icon/xyg256.png", FileType.Internal);
        config.addIcon("icon/xyg128.png", FileType.Internal);
        config.addIcon("icon/xyg64.png", FileType.Internal);
        config.addIcon("icon/xyg32.png", FileType.Internal);
        config.addIcon("icon/xyg16.png", FileType.Internal);
       // config.fullscreen = true;
        new LwjglApplication(new TaxeGame(), config);
    }
}
