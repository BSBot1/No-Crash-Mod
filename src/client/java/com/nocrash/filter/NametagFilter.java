package com.nocrash.filter;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Optional;

public final class NametagFilter {
	private static final int MAX_NAME_LENGTH = 16;
	private static final int MAX_LEGACY_FORMAT_CODES = 3;
	private static final int MAX_OBFUSCATED_CHARS = 2;
	private static final int MAX_UNDERLINED_CHARS = 16;

	private NametagFilter() {
	}

	public static boolean shouldHide(Text name) {
		String plain = name.getString();
		if (plain.length() > MAX_NAME_LENGTH) {
			return true;
		}

		if (countLegacyFormattingCodes(plain) > MAX_LEGACY_FORMAT_CODES) {
			return true;
		}

		int[] obfuscatedChars = {0};
		int[] underlinedChars = {0};
		name.visit((style, textPart) -> {
			if (style.isObfuscated()) {
				obfuscatedChars[0] += textPart.length();
			}
			if (style.isUnderlined()) {
				underlinedChars[0] += textPart.length();
			}
			return Optional.empty();
		}, Style.EMPTY);

		return obfuscatedChars[0] > MAX_OBFUSCATED_CHARS || underlinedChars[0] > MAX_UNDERLINED_CHARS;
	}

	private static int countLegacyFormattingCodes(String text) {
		int count = 0;
		for (int i = 0; i < text.length() - 1; i++) {
			if (text.charAt(i) == '\u00A7' && isLegacyFormattingCode(text.charAt(i + 1))) {
				count++;
			}
		}
		return count;
	}

	private static boolean isLegacyFormattingCode(char c) {
		char lower = Character.toLowerCase(c);
		return (lower >= '0' && lower <= '9')
			|| (lower >= 'a' && lower <= 'f')
			|| (lower >= 'k' && lower <= 'o')
			|| lower == 'r';
	}
}
