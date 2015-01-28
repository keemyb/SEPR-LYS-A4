package fvs.taxe.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fvs.taxe.TaxeGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //Set window size
        config.width = 900;
        config.height = 600;
        config.title = "TaxE";
        config.resizable = true;
        config.addIcon("icon/fvs256.png", FileType.Internal);
        config.addIcon("icon/fvs128.png", FileType.Internal);
        config.addIcon("icon/fvs64.png", FileType.Internal);
        config.addIcon("icon/fvs32.png", FileType.Internal);
        config.addIcon("icon/fvs16.png", FileType.Internal);
        config.fullscreen = false;
        new LwjglApplication(new TaxeGame(), config);
    }
}
