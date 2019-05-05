package me.winded.passu;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static String getFileNameFromPath(String path) throws UnsupportedEncodingException {
        File f = new File(path);
        String decodedPath = URLDecoder.decode(f.getName(), StandardCharsets.UTF_8.name());
        f = new File(decodedPath);
        return f.getName();
    }
}
