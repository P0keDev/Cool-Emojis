package dev.p0ke.coolemojis.manager;

import dev.p0ke.coolemojis.CoolEmojis;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.util.Formatting.*;

public class EmojiMapManager {

    private static final String FILE_NAME = "cool-emojis-map.txt";

    private static Map<String, String> emojiMap = new HashMap<>();
    private static Map<String, String> emojiList = new HashMap<>();
    private static File emojiFile = null;

    public static void setupEmojiMapFile() {
        try {
            if (emojiFile != null) return;
            Path filepath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
            emojiFile = filepath.toFile();

            // file doesn't exist, create it
            if (!emojiFile.exists()) {
                emojiFile.createNewFile();
                return;
            }

            // file exists, read in emojis
            loadEmojis();
        } catch (IOException e) {
            CoolEmojis.LOGGER.error("Failed to load emoji file!");
        }
    }

    public static String replaceEmojis(String message) {
        return replaceEmojis(message, false);
    }

    public static String replaceEmojis(String message, boolean forceColor) {
        // iterate through emoji map, replacing any instances of names with corresponding chars
        for (Entry<String, String> emoji : emojiMap.entrySet()) {
            if (!message.contains(emoji.getKey())) continue;
            String toReplace = emoji.getValue();
            if (forceColor) toReplace = WHITE + toReplace + RESET;
            message = message.replace(emoji.getKey(), toReplace);
        }
        return message;
    }

    public static String getEmojiList() {
        return String.join("" + DARK_PURPLE + BOLD + " | ", emojiList.entrySet().stream().map(
                e -> WHITE + e.getKey() + BLUE + " - " + DARK_AQUA + e.getValue()).toList());
    }

    public static void loadEmojis() {
        emojiMap.clear();
        emojiList.clear();

        try {
            BufferedReader emReader = new BufferedReader(new FileReader(emojiFile, StandardCharsets.UTF_8));
            String line;
            while ((line = emReader.readLine()) != null) {
                if (line.startsWith("##")) continue; // comment

                // format is name=character, or name,name,name=character
                String[] split = line.split("=");
                if (split.length != 2) continue; // invalid line

                // split into char and name(s), add to map
                String emojiChar = split[1];
                String[] names = split[0].split(",");
                for (String name : names) {
                    if (name.isEmpty()) continue;
                    emojiMap.put(name, emojiChar);
                }

                // add emoji to list, with all its names
                emojiList.put(emojiChar, String.join(" ", names));
            }

            CoolEmojis.LOGGER.info("Loaded " + emojiMap.size() + " emojis!");
        } catch (IOException e) {
            CoolEmojis.LOGGER.error("Failed to read from emoji file!");
        }
    }
}
