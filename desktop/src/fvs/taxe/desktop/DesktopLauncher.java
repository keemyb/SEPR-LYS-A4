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
        config.addIcon("icon/lys256.png", FileType.Internal);
        config.addIcon("icon/lys128.png", FileType.Internal);
        config.addIcon("icon/lys64.png", FileType.Internal);
        config.addIcon("icon/lys32.png", FileType.Internal);
        config.addIcon("icon/lys16.png", FileType.Internal);
       // config.fullscreen = true;
        new LwjglApplication(new TaxeGame(), config);
    }
}
