package com.stay4it.emoji;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class EmojiParser {
	private Context context;

	private EmojiParser(Context mContext) {
		this.context = mContext;
		readMap(mContext);
	}

	private HashMap<List<Integer>, String> convertMap = new HashMap<List<Integer>, String>();
	private HashMap<String, ArrayList<String>> emoMap = new HashMap<String, ArrayList<String>>();
	private static EmojiParser mParser;

	public static EmojiParser getInstance(Context mContext) {
		if (mParser == null) {
			mParser = new EmojiParser(mContext);
		}
		return mParser;
	}

	public HashMap<String, ArrayList<String>> getEmoMap() {
		return emoMap;
	}

	public void readMap(Context mContext) {
		if (convertMap == null || convertMap.size() == 0) {
			convertMap = new HashMap<List<Integer>, String>();
			XmlPullParser xmlpull = null;
			String fromAttr = null;
			String key = null;
			ArrayList<String> emos = null;
			try {
				XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
				xmlpull = xppf.newPullParser();
				InputStream stream = mContext.getAssets().open("emoji.xml");
				xmlpull.setInput(stream, "UTF-8");
				int eventCode = xmlpull.getEventType();
				while (eventCode != XmlPullParser.END_DOCUMENT) {
					switch (eventCode) {
					case XmlPullParser.START_DOCUMENT: {
						break;
					}
					case XmlPullParser.START_TAG: {
						if (xmlpull.getName().equals("key")) {
							emos = new ArrayList<String>();
							key = xmlpull.nextText();
						}
						if (xmlpull.getName().equals("e")) {
							fromAttr = xmlpull.nextText();
							emos.add(fromAttr);
							List<Integer> fromCodePoints = new ArrayList<Integer>();
							if (fromAttr.length() > 6) {
								String[] froms = fromAttr.split("\\_");
								for (String part : froms) {
									fromCodePoints.add(Integer.parseInt(part, 16));
								}
							} else {
								fromCodePoints.add(Integer.parseInt(fromAttr, 16));
							}
							convertMap.put(fromCodePoints, fromAttr);
						}
						break;
					}
					case XmlPullParser.END_TAG: {
						if (xmlpull.getName().equals("dict")) {
							emoMap.put(key, emos);
						}
						break;
					}
					case XmlPullParser.END_DOCUMENT: {
						Trace.d("parse emoji complete");
						break;
					}
					}
					eventCode = xmlpull.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String parseEmoji(String input) {
		if (input == null || input.length() <= 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		List<Integer> key = null;
		for (int i = 0; i < codePoints.length; i++) {
			key = new ArrayList<Integer>();
			if (i + 1 < codePoints.length) {
				key.add(codePoints[i]);
				key.add(codePoints[i + 1]);
				if (convertMap.containsKey(key)) {
					String value = convertMap.get(key);
					if (value != null) {
						result.append("[e]" + value + "[/e]");
					}
					i++;
					continue;
				}
			}
			key.clear();
			key.add(codePoints[i]);
			if (convertMap.containsKey(key)) {
				String value = convertMap.get(key);
				if (value != null) {
					result.append("[e]" + value + "[/e]");
				}
				continue;
			}
			result.append(Character.toChars(codePoints[i]));
		}
		return result.toString();
	}

	private int[] toCodePointArray(String str) {
		char[] ach = str.toCharArray();
		int len = ach.length;
		int[] acp = new int[Character.codePointCount(ach, 0, len)];
		int j = 0;
		for (int i = 0, cp; i < len; i += Character.charCount(cp)) {
			cp = Character.codePointAt(ach, i);
			acp[j++] = cp;
		}
		return acp;
	}

	public String convertToUnicode(String emo) {
		emo = emo.substring(emo.indexOf("_") + 1);
		if (emo.length() < 6) {
			return new String(Character.toChars(Integer.parseInt(emo, 16)));
		}
		String[] emos = emo.split("_");
		char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16));
		char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
		char[] emoji = new char[char0.length + char1.length];
		for (int i = 0; i < char0.length; i++) {
			emoji[i] = char0[i];
		}
		for (int i = char0.length; i < emoji.length; i++) {
			emoji[i] = char1[i - char0.length];
		}
		return new String(emoji);
	}

	public SpannableStringBuilder convertToEmoji(String content) {
		String regex = "\\[e\\](.*?)\\[/e\\]";
		Pattern pattern = Pattern.compile(regex);
		String emo = "";
		Resources resources = context.getResources();
		String unicode = parseEmoji(content);
		Matcher matcher = pattern.matcher(unicode);
		SpannableStringBuilder sBuilder = new SpannableStringBuilder(unicode);
		Drawable drawable = null;
		ImageSpan span = null;
		while (matcher.find()) {
			emo = matcher.group();
			try {
				int id = resources.getIdentifier("emoji_" + emo.substring(emo.indexOf("]") + 1, emo.lastIndexOf("[")), "drawable",
						"com.stay4it.emoji");
				if (id != 0) {
					drawable = resources.getDrawable(id);
					drawable.setBounds(0, 0, 24, 24);
					span = new ImageSpan(drawable);
					sBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (Exception e) {
				break;
			}
		}
		return sBuilder;
	}

}
