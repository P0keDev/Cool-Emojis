package dev.p0ke.coolemojis.mixin;

import dev.p0ke.coolemojis.config.CoolEmojisConfig;
import dev.p0ke.coolemojis.manager.EmojiMapManager;
import net.minecraft.server.filter.TextStream.Message;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class NetworkHandlerMixin {

	@ModifyVariable(at = @At("STORE"), ordinal = 0, name = "string",
			method = "onChatMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V")
	private String replaceChatMessage(String message) {
		if (!CoolEmojisConfig.chatEnabled()) return message;
		// return new string with emojis replaced
		return EmojiMapManager.replaceEmojis(message);
	}

	@ModifyVariable(at = @At("HEAD"), argsOnly = true, ordinal = 0, name = "signText",
			method = "onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V")
	private List<Message> replaceSignLines(List<Message> signText) {
		if (!CoolEmojisConfig.signsEnabled()) return signText;
		// create new list, replacing emojis in each sign line
		return signText.stream().map(
				m -> Message.permitted(EmojiMapManager.replaceEmojis(m.getRaw(), true))).toList();
	}

	@ModifyVariable(at = @At("HEAD"), argsOnly = true, ordinal = 0, name = "messages",
		method = "setTextToBook(Ljava/util/List;Ljava/util/function/UnaryOperator;Lnet/minecraft/item/ItemStack;)V")
	private List<Message> replaceBookPages(List<Message> pages) {
		if (!CoolEmojisConfig.booksEnabled()) return pages;
		// create new list, replacing emojis in each book page
		return pages.stream().map(
				m -> Message.permitted(EmojiMapManager.replaceEmojis(m.getRaw(), true))).toList();
	}

	@ModifyArg(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/screen/AnvilScreenHandler;setNewItemName(Ljava/lang/String;)V"), index = 0,
			method = "onRenameItem(Lnet/minecraft/network/packet/c2s/play/RenameItemC2SPacket;)V")
	private String replaceRenameText(String name) {
		if (!CoolEmojisConfig.anvilsEnabled()) return name;
		// return new string with emojis replaced
		return EmojiMapManager.replaceEmojis(name, true);
	}
}
