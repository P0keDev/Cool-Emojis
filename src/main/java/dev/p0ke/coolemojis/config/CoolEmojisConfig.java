package dev.p0ke.coolemojis.config;

import dev.p0ke.coolemojis.CoolEmojis;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;

public final class CoolEmojisConfig extends Config {

    public CoolEmojisConfig() {
        super(CoolEmojis.MOD_ID, new Emojis(), new Commands(), new PackGeneration());
    }

    public static boolean chatEnabled() { return Emojis.chatEmojis; }

    public static boolean signsEnabled() { return Emojis.signEmojis; }

    public static boolean anvilsEnabled() { return Emojis.anvilEmojis; }

    public static boolean booksEnabled() { return Emojis.bookEmojis; }

    public static boolean listCommandEnabled() { return Commands.listCommand; }

    public static String getEmojiWrapChar() { return PackGeneration.emojiSignifier; }

    public static int getInitialEmojiCodepoint() { return PackGeneration.initialCodePoint; }

    public static class Emojis implements ConfigGroup {
        @ConfigEntry(comment = "Whether or not emojis are supported in chat.")
        private static boolean chatEmojis = true;

        @ConfigEntry(comment = "Whether or not emojis are supported on signs.")
        private static boolean signEmojis = true;

        @ConfigEntry(comment = "Whether or not emojis are supported in anvils.")
        private static boolean anvilEmojis = true;

        @ConfigEntry(comment = "Whether or not emojis are supported in written books.")
        private static boolean bookEmojis = true;
    }

    public static class Commands implements ConfigGroup {
        @ConfigEntry(comment = "Whether or not the /emojilist command is enabled.")
        private static boolean listCommand = true;
    }

    public static class PackGeneration implements ConfigGroup {
        @ConfigEntry(comment = "The character used to wrap emoji names in generated configs.\n" +
                "For example, : will result in names like :thumbsup:")
        private static String emojiSignifier = ":";

        @ConfigEntry(comment = "The initial character used for assigning custom emoji characters.\n" +
                "You most likely don't need to touch this!")
        private static String initialCharacter = new String(Character.toChars(0xF4000));
        private static int initialCodePoint = 0xF4000;

        @Override
        public void onUpdate() {
            if (!initialCharacter.isEmpty())
                initialCodePoint = initialCharacter.codePointAt(0);
        }

    }


}
