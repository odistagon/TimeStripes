package com.odistagon.glone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/** CheckBoxPreference with extra icon view.
 * CheckBoxPreference can have a one IconView on its head, so this is for you 
 * only when extra one icon is needed next to check box.
 * (But that is not on 1.6 nor 2.3.1. They still need these fix ups)
 */
public class IconCheckBoxPreference extends CheckBoxPreference implements OnCheckedChangeListener
{
	private boolean		m_bDefault;
	private Drawable	m_dIcon;
	private CheckBox	m_cb;

	public IconCheckBoxPreference(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);

		setWidgetLayoutResource(R.layout.pref_iconcb);
//		int		nres = attrs.getAttributeResourceValue(GloneUtils.XMLNS_ANDROID, "icon", R.drawable.ic_launcher_timestr);
//		m_dIcon = context.getResources().getDrawable(R.drawable.icon);
//		m_dIcon = context.getResources().getDrawable(nres);
//		mIcon = context.obtainStyledAttributes(attrs, R.styleable.IconPreference, defStyle, 0).getDrawable(R.styleable.IconPreference_icon);
		m_bDefault = attrs.getAttributeBooleanValue(GloneUtils.XMLNS_ANDROID, "defaultValue", true);
	}

	public IconCheckBoxPreference(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	protected void onBindView(final View varg) {
		super.onBindView(varg);

		final ImageView	iv0 = (ImageView)varg.findViewById(R.id.pref_iconcb_icon);
		if(iv0 != null && m_dIcon != null) {
			iv0.setImageDrawable(m_dIcon);
		}

		m_cb = (CheckBox)varg.findViewById(R.id.pref_iconcb_cb);
//		m_cb.setChecked(m_bDefault);
	}

	/**
	 * @param darg The icon for this Preference
	 */
	@SuppressLint("Override")	// will be officially overrided from API level 11
	public void setIcon(final Drawable darg) {
		if ((darg == null && m_dIcon != null) || (darg != null && !darg.equals(m_dIcon))) {
			m_dIcon = darg;
			notifyChanged();
		}
	}

	/**
	 * @return The icon.
	 */
	@SuppressLint("Override")	// will be officially overrided from API level 11
	public Drawable getIcon() {
		return	m_dIcon;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		m_cb.setChecked(!m_cb.isChecked());
	}
}