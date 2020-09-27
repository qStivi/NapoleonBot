package qStivi;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv DOTENV = Dotenv.load();

    public static String get(String key) {
        return DOTENV.get(key);
    }

}
