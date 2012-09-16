package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgProv extends AppWidgetProvider
{
	private Bitmap				m_bm;					// bitmap for ImageView
	private Bitmap				m_bmtex;				// texture 
	private static final int	CN_COLS = 4;			//
	private static final int	CN_ROWS = 1;			//
	private static final float	CF_CYPAHRS = 0.6f;		// height room for prev/following hours

	public WidgProv() {
		m_bmtex = BitmapFactory.decodeResource(
				GloneApp.getContext().getResources(), R.drawable.timestr_tex);
	}

	/** 
	 * @param context
	 * @param awmarg
	 */
	private void initBitmap(Context context, AppWidgetManager awmarg){
//		// Create buffer bitmap align to maximum widget size.
//		ComponentName	comp0 = new ComponentName(context, WidgProv.class);
//		int[]	anAllWidgetIds = awmarg.getAppWidgetIds(comp0);
//		int		ncx = 0, ncy = 0;
//		for(int nWidgetId : anAllWidgetIds) {
//			AppWidgetProviderInfo	awpi0 = awmarg.getAppWidgetInfo(nWidgetId);
//			ncx = (awpi0.minWidth > ncx ? awpi0.minWidth : ncx);
//			ncy = (awpi0.minHeight > ncy ? awpi0.minHeight : ncy);
//		}
//		float	fdens = GloneApp.getContext().getResources().getDisplayMetrics().density;
//		m_bm = Bitmap.createBitmap(
//				(int)((float)ncx * fdens), (int)((float)ncy * fdens), Bitmap.Config.ARGB_8888);
		// Create buffer bitmap in a fixed size.
		float	fcy = GlStripe.CFTEX_HURCY * (1f + CF_CYPAHRS * 2);
		float	fcx = fcy / (float)CN_ROWS * (float)CN_COLS;
		m_bm = Bitmap.createBitmap((int)fcx, (int)fcy, Bitmap.Config.ARGB_8888);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager awmarg, int[] appWidgetIds) {
		boolean	bFirst = false;
		if(m_bm == null) {
			initBitmap(context, awmarg);
			bFirst = true;
		}

		ComponentName	comp0 = new ComponentName(context, WidgProv.class);
		int[]	anAllWidgetIds = awmarg.getAppWidgetIds(comp0);
		for(int nWidgetId : anAllWidgetIds) {
//			AppWidgetProviderInfo	awpi0 = awmarg.getAppWidgetInfo(nWidgetId);
			int		nidx = 0;
			long	lnow = System.currentTimeMillis();
			ArrayList<GloneTz>	altz = GloneApp.getDoc().getTzList();
			float	fcx0 = ((float)m_bm.getWidth() - GlStripe.CFTEX_HURCX) / ((float)(altz.size() - 1));
			Paint	paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
			Canvas	cv0 = new Canvas(m_bm);
//			paint0.setColor(Color.RED);
//			cv0.drawRect(0f, 0f, m_bm.getWidth(), m_bm.getHeight(), paint0);
			// draw tz stripes
			for(GloneTz gtz0: altz) {
				int		nx0 = (int)(fcx0 * (float)(altz.size() - 1 - nidx));
				drawHours(cv0, nx0, gtz0);
				//
				nidx++;
			}
			// draw under half shadow
			paint0.setColor(Color.BLACK);
			paint0.setAlpha(80);	//0xFF *this makes gradient being affected by some bug 
//			paint0.setShader(new LinearGradient(0, 0, m_bm.getWidth(), 0,
//					0x77000000, 0x22000000, Shader.TileMode.REPEAT));
			cv0.drawRect(0, GlStripe.CFTEX_HURCY * (1f + CF_CYPAHRS),
					m_bm.getWidth(), m_bm.getHeight(), paint0);
			//
			final float	NC_TEXTSIZE = 11f;
			paint0.setColor(Color.WHITE);
			paint0.setAlpha(0xFF);
			paint0.setTextSize(NC_TEXTSIZE);
			nidx = 0;
			// draw tz names
			for(GloneTz gtz0: altz) {
				int		nx0 = (int)(fcx0 * (float)(altz.size() - 1 - nidx));
				String	stzid = gtz0.getTimeZone().getDisplayName(
						gtz0.getTimeZone().inDaylightTime(new Date(lnow)), TimeZone.SHORT);
//				int		ncwstr = (int)((GlStripe.CFTEX_HURCX - paint0.measureText(stzid)) / 2f);	// centering
				int		ncwstr = (int)(GlStripe.CFTEX_HURCX - paint0.measureText(stzid) - (NC_TEXTSIZE / 2f));	// right aligned
				cv0.drawText(stzid, nx0 + ncwstr,
						(float)m_bm.getHeight() - paint0.getFontMetrics().bottom, paint0);
				//
				nidx++;
			}

			RemoteViews	rv0 = new RemoteViews(context.getPackageName(), R.layout.widg01);
			rv0.setImageViewBitmap(R.id.iv_widg01_test1, m_bm);
//			rv0.setTextViewText(R.id.tv_widg01_test2, "dens: " + 0);

			// Register an onClickListener
			if(bFirst) {
				{
					Intent		i0 = new Intent(context, WidgProv.class);
					i0.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
					i0.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
					PendingIntent	pi0 = PendingIntent.getBroadcast(context,
							0, i0, PendingIntent.FLAG_UPDATE_CURRENT);
					rv0.setOnClickPendingIntent(R.id.iv_widg01_tb01, pi0);
				}
				{
					Intent		i0 = new Intent(Intent.ACTION_MAIN);
					i0.setClass(GloneApp.getContext(), AyMain.class);
					i0.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					PendingIntent	pi0 = PendingIntent.getActivity(
							GloneApp.getContext(), 0, i0, 0);
					rv0.setOnClickPendingIntent(R.id.iv_widg01_test1, pi0);
				}
			}

			awmarg.updateAppWidget(nWidgetId, rv0);
		}
	}

	private void drawHours(Canvas cv0, int nx0, GloneTz gtz0) {
		final int	NC_HOURS = 3;
		Calendar	cal0 = Calendar.getInstance(gtz0.getTimeZone());
//		float		foffs = gtz0.getDSTOffsetInTheDay(cal0.getTimeInMillis(), 0);
		float		foffs = ((float)(gtz0.getTimeZone().getOffset(cal0.getTimeInMillis()) / 60 / 1000) / 60f) % (60f);
//Log.d("(X)", "[" + gtz0.getTimeZoneId() + "](" + foffs + ", " + gtz0.getTimeZone().getOffset(cal0.getTimeInMillis()));
		int			nap = (foffs % 1f != 0f ? +0 : -0);
		cal0.add(Calendar.HOUR_OF_DAY, (+2 + nap));// - (int)(fdstoffs / 1f));
		int			nh0 = cal0.get(Calendar.HOUR_OF_DAY);
		float		fmin = (float)(cal0.get(Calendar.MINUTE) / 60f);	// TODO an hour is not necessarily be 60min.
//		float		fdens = GloneApp.getContext().getResources().getDisplayMetrics().density;	// no need of density conversion
		float		fdsty = GlStripe.CFTEX_HURCY * (-.4f + (float)(nap * -1));
		for(int i = 0; i < NC_HOURS; i++) {
			float	ftop0 = fdsty + GlStripe.CFTEX_HURCY * (-1f + fmin + (foffs % 1f));
			cv0.drawBitmap(m_bmtex, new Rect(0, (int)GlStripe.CFTEX_HURCY * (23 - nh0),
					(int)GlStripe.CFTEX_HURCX, (int)GlStripe.CFTEX_HURCY * (24 - nh0)),
					new RectF(nx0, ftop0,
							nx0 + GlStripe.CFTEX_HURCX, ftop0 + GlStripe.CFTEX_HURCY), null);
			fdsty += GlStripe.CFTEX_HURCY;
			cal0.add(Calendar.HOUR_OF_DAY, -1);
			nh0 = cal0.get(Calendar.HOUR_OF_DAY);
		}
	}
}
