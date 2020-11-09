package qStivi;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static String get(String key) {

        File f = new File(System.getProperty("user.dir") + "\\.env");

        Dotenv DOTENV;

        if (f.exists() && !f.isDirectory()) {
            DOTENV = Dotenv.load();
            return DOTENV.get(key);
        } else {
            boolean fileCreated = false;
            try {
                fileCreated = f.createNewFile();
                if (fileCreated) {
                    FileWriter fw = new FileWriter(f);
                    fw.write("TOKEN=Your bot token goes here");
                    fw.write(System.lineSeparator());
                    fw.write("PREFIX=Your command prefix goes here");
                    fw.write(System.lineSeparator());
                    fw.write("OWNER_ID=Your own user ID goes here. Used for admin commands");
                    fw.write(System.lineSeparator());
                    fw.write("CHANNEL_ID=Bot channel ID goes here");
                    fw.write(System.lineSeparator());
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

}
