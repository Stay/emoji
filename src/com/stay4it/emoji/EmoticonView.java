package com.stay4it.emoji;

import java.util.ArrayList;

import com.stay4it.emoji.R;
import com.stay4it.emoji.R.id;
import com.stay4it.emoji.R.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EmoticonView extends LinearLayout implements OnItemClickListener, View.OnClickListener, OnCheckedChangeListener {
	private ArrayList<String> emojis = new ArrayList<String>();
	private GridView mEmojiGridView;
	private EmojiAdapter adapter;
	private Context mContext;
	private int curPage = -1;
	private RadioButton mEmojiRadio1;
	private RadioButton mEmojiRadio2;
	private RadioButton mEmojiRadio3;
	private RadioButton mEmojiRadio4;
	private RadioButton mEmojiRadio5;
	private ImageView mEmojiDelte;
	private OnEmoticonTapListener mTapListener;
	private EmojiParser parser;
	private Resources resources;
	private RadioGroup mEmojiGroup;
	private static final int nature = 1;
	private static final int objects = 2;
	private static final int people = 0;
	private static final int places = 3;
	private static final int symbols = 4;

	// public EmoView(Context context, AttributeSet attrs, int defStyle) {
	// super(context, attrs, defStyle);
	// }

	public EmoticonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}

	public EmoticonView(Context context) {
		super(context);
		initializeView();
	}

	private void initializeView() {
		LayoutInflater.from(getContext()).inflate(R.layout.chat_emo, this);
	}

	public void init(Context mContext, OnEmoticonTapListener mTapListener, Resources resources) {
		this.mContext = mContext;
		this.mTapListener = mTapListener;
		this.resources = resources;
		parser = EmojiParser.getInstance(mContext);
		mEmojiGridView = (GridView) this.findViewById(R.id.message_facebar_gv_emotes);
		adapter = new EmojiAdapter();
		mEmojiGridView.setAdapter(adapter);
		mEmojiGridView.setOnItemClickListener(this);
		mEmojiGroup = (RadioGroup) findViewById(R.id.message_facebar_radiobutton_type);
		mEmojiGroup.setOnCheckedChangeListener(this);
		mEmojiRadio1 = (RadioButton) this.findViewById(R.id.emote_radio_1);
		mEmojiRadio1.setOnClickListener(this);
		mEmojiRadio1.setSelected(true);
		mEmojiRadio1.setChecked(true);
		mEmojiRadio2 = (RadioButton) this.findViewById(R.id.emote_radio_2);
		mEmojiRadio2.setOnClickListener(this);
		mEmojiRadio3 = (RadioButton) this.findViewById(R.id.emote_radio_3);
		mEmojiRadio3.setOnClickListener(this);
		mEmojiRadio4 = (RadioButton) this.findViewById(R.id.emote_radio_4);
		mEmojiRadio4.setOnClickListener(this);
		mEmojiRadio5 = (RadioButton) this.findViewById(R.id.emote_radio_5);
		mEmojiRadio5.setOnClickListener(this);
		mEmojiDelte = (ImageView) this.findViewById(R.id.emote_radio_delete);
		mEmojiDelte.setOnClickListener(this);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == View.VISIBLE) {
			if (curPage == -1) {
				getPage(people);
			}
		} else {
			curPage = -1;
		}
	}

	void getPage(int page) {
		emojis.clear();
		this.curPage = page;
		switch (page) {
		case people:
			emojis.addAll(parser.getEmoMap().get("people"));
			break;
		case nature:
			emojis.addAll(parser.getEmoMap().get("nature"));
			break;
		case objects:
			emojis.addAll(parser.getEmoMap().get("objects"));
			break;
		case places:
			emojis.addAll(parser.getEmoMap().get("places"));
			break;
		case symbols:
			emojis.addAll(parser.getEmoMap().get("symbols"));
			break;
		default:
			break;
		}
		adapter.notifyDataSetChanged();
	}

	public interface OnEmoticonTapListener {
		void onEmoticonTap(String drawableId);

		void onEmoticonDel();
	}

	class EmojiAdapter extends BaseAdapter {
		private ViewHolder holder;
		private Drawable drawable;

		@Override
		public int getCount() {
			return emojis.size();
		}

		@Override
		public Object getItem(int position) {
			return emojis.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null || convertView.getTag() == null) {
				holder = new ViewHolder();
				holder.emoticon = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.chat_emo_item, null);
				convertView = holder.emoticon;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.emoticon.setImageResource(resources.getIdentifier("emoji_" + emojis.get(position), "drawable", getContext().getPackageName()));
			return convertView;
		}
	}

	static class ViewHolder {
		ImageView emoticon;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mTapListener.onEmoticonTap((String) parent.getItemAtPosition(position));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emote_radio_1:
			getPage(people);
			break;
		case R.id.emote_radio_2:
			getPage(nature);
			break;
		case R.id.emote_radio_3:
			getPage(objects);
			break;
		case R.id.emote_radio_4:
			getPage(places);
			break;
		case R.id.emote_radio_5:
			getPage(symbols);
			break;
		case R.id.emote_radio_delete:
			mTapListener.onEmoticonDel();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.emote_radio_1:
			getPage(people);
			break;
		case R.id.emote_radio_2:
			getPage(nature);
			break;
		case R.id.emote_radio_3:
			getPage(objects);
			break;
		case R.id.emote_radio_4:
			getPage(places);
			break;
		case R.id.emote_radio_5:
			getPage(symbols);
			break;
		default:
			break;
		}
	}
}
