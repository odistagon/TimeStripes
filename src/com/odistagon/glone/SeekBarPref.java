package com.odistagon.glone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 */
public class SeekBarPref extends Preference implements OnSeekBarChangeListener
{
	private int			m_nMinVal = 10;
	private int			m_nMaxVal = 100;
	private int			m_nInterval = 5;
	private int			m_nVal = 50;
	private int			m_nDefault = 0;
	private TextView	m_tvText;

	public static final String	XMLNS_ANDROID = "http://schemas.android.com/apk/res/android";

	public SeekBarPref(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.pref_seekbar);
		getAttributes(attrs);
	}

	private void getAttributes(AttributeSet attrs) {
		String	stemp = attrs.getAttributeValue(XMLNS_ANDROID, "text");
		if(stemp != null) {
			String[]	astemp = stemp.split(",");
			m_nMinVal = Integer.parseInt(astemp[0]);
			m_nMaxVal = Integer.parseInt(astemp[1]);
			m_nInterval = Integer.parseInt(astemp[2]);
		}
		m_nDefault = attrs.getAttributeIntValue(XMLNS_ANDROID, "defaultValue", 0);
	}

	@Override
	protected void onBindView(View view) {
		LinearLayout	l0 = (LinearLayout)view.findViewById(R.id.pref_seekbar_lo0);

		SeekBar			sb0 = (SeekBar)l0.findViewById(R.id.pref_seekbar_sb1);
		sb0.setMax(m_nMaxVal - m_nMinVal);
		sb0.setProgress(m_nVal - m_nMinVal);
		sb0.setOnSeekBarChangeListener(this);

		m_tvText = (TextView)l0.findViewById(R.id.pref_seekbar_tv2);
		m_tvText.setText(Integer.toString(m_nVal));

		super.onBindView(view);
	}

	//@Override
	public void onProgressChanged(SeekBar sbarg, int nProgress, boolean bFromUser) {
		nProgress = nProgress / m_nInterval * m_nInterval;

		if(!callChangeListener(nProgress)){
			sbarg.setProgress(nProgress); 
			return; 
		}

		sbarg.setProgress(nProgress);
		m_nVal = nProgress + m_nMinVal;
		this.m_tvText.setText(Integer.toString(m_nVal));
//		commitPreferenceValueChange(nProgress);
//		notifyChanged();
	}

	//@Override
	public void onStartTrackingTouch(SeekBar abarg) {
	}

	//@Override
	public void onStopTrackingTouch(SeekBar sbarg) {
		m_nVal = sbarg.getProgress() + m_nMinVal;
		commitPreferenceValueChange(m_nVal);
//		notifyChanged();
	}

	//@Override 
	protected Object onGetDefaultValue(TypedArray ta, int index){
		int	dValue = (int)ta.getInt(index, (m_nMaxVal - m_nMinVal) / 2);
		return validateValue(dValue);
	}

	@Override
	protected void onSetInitialValue(boolean bRestoreValue, Object oDefault) {
		super.onSetInitialValue(bRestoreValue, oDefault);
		if(bRestoreValue)
			m_nVal = shouldPersist() ? getPersistedInt(m_nDefault) : 0;
		else
			m_nVal = m_nDefault;//(Integer)oDefault;	// oDefault not working. missing something?
	}

	private int validateValue(int value) {
		if(value > (m_nMaxVal - m_nMinVal))
			value = (m_nMaxVal - m_nMinVal);
		else if(value <= 0)
			value = 0;
		else if(value % m_nInterval != 0)
			value = Math.round(((float)value) / m_nInterval) * m_nInterval;  

		return value;  
	}

	private void commitPreferenceValueChange(int newValue) {
		SharedPreferences.Editor	editor =  getEditor();
		editor.putInt(getKey(), newValue);
		editor.commit();
	}

//	public void setRange(int nMin, int nMax, int nInterval) {
//		m_nMinVal = nMin;
//		m_nMaxVal = nMax;
//		m_nInterval = nInterval;
//	}

}
