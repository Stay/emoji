package com.stay4it.emoji;

import java.util.ArrayList;

import com.stay4it.emoji.ChatActivity.ChatAdapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Stay
 * @version create time：May 24, 2015 10:02:04 PM
 */
public class ChatActivity extends Activity implements com.stay4it.emoji.EmoticonView.OnEmoticonTapListener, OnClickListener {
	private EmoticonView mEmoView;
	private EditText mChatEditorTxt;
	private ListView mChatLsv;
	private ChatAdapter adapter;
	private ArrayList<String> messages = new ArrayList<String>();
	private Button mChatSendBtn;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		EmojiParser.getInstance(this);
		mChatLsv = (ListView) this.findViewById(R.id.mChatLsv);
		adapter = new ChatAdapter();
		mChatLsv.setAdapter(adapter);
		mEmoView = (EmoticonView) this.findViewById(R.id.message_layout_emotes);
		mEmoView.init(this, this, getResources());
		mChatEditorTxt = (EditText) this.findViewById(R.id.mChatEditorTxt);
		mChatSendBtn = (Button) this.findViewById(R.id.mChatSendBtn);
		mChatSendBtn.setOnClickListener(this);
	}

	class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = new TextView(ChatActivity.this);
			view.setText(EmojiParser.getInstance(ChatActivity.this).convertToEmoji(messages.get(position)));
			return view;
		}

	}


	@Override
	public void onEmoticonTap(String drawableId) {
		Editable editable = mChatEditorTxt.getText();
		int index = mChatEditorTxt.getSelectionEnd();
		String emo = EmojiParser.getInstance(this).convertToUnicode(drawableId);
		SpannableStringBuilder builder = new SpannableStringBuilder(emo);
		int resId = getResources().getIdentifier("emoji_" + drawableId, "drawable", getPackageName());
		Drawable d = getResources().getDrawable(resId);
		d.setBounds(0, 0, 30, 30);
		ImageSpan span = new ImageSpan(d);
		builder.setSpan(span, 0, emo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (index < mChatEditorTxt.length()) {
			editable.insert(index, builder);
		}else {
			editable.append(builder);
		}
		mChatEditorTxt.setSelection(index + emo.length());
		
		
//		drawableSrc = "emoji_" + drawableId;
//		ImageGetter imageGetter = new ImageGetter() {
//			public Drawable getDrawable(String source) {
//				int id = ChatActivity.this.getResources().getIdentifier(source, "drawable", getPackageName());
//				Drawable d = ChatActivity.this.getResources().getDrawable(id);
//				d.setBounds(0, 0, 24, 24);
//				return d;
//			}
//		};
//		CharSequence cs1 = Html.fromHtml("<img src='" + drawableSrc + "'/>", imageGetter, null);
//		int index = mChatEditorTxt.getSelectionStart();
//		Editable etb = mChatEditorTxt.getText();
//		int length = etb.length();
//		if (index < length) {
//			etb.insert(index, cs1);
//		} else {
//			mChatEditorTxt.append(cs1);
//		}
//		mChatEditorTxt.setSelection(index + 1);
	}

	@Override
	public void onEmoticonDel() {
		mChatEditorTxt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
		mChatEditorTxt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
	}

	@Override
	public void onClick(View v) {
		String message = mChatEditorTxt.getText().toString();
		if (!"".equals(message.trim())) {
			messages.add(message);
			adapter.notifyDataSetChanged();
			Trace.d(message);
		}
	}
}
