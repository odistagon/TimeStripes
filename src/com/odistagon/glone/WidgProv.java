package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
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
	private Bitmap	m_bm;							// bitmap for ImageView
	private Bitmap	m_bmtex;						// texture 

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
		m_bm = Bitmap.createBitmap(
				(int)(GlStripe.CFTEX_HURCX * 4), (int)(GlStripe.CFTEX_HURCY * 2.2),
				Bitmap.Config.ARGB_8888);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager awmarg, int[] appWidgetIds) {
		if(m_bm == null)
			initBitmap(context, awmarg);

		ComponentName	comp0 = new ComponentName(context, WidgProv.class);
		int[]	anAllWidgetIds = awmarg.getAppWidgetIds(comp0);
		for(int nWidgetId : anAllWidgetIds) {
			AppWidgetProviderInfo	awpi0 = awmarg.getAppWidgetInfo(nWidgetId);

			int		nidx = 0;
			long	lnow = System.currentTimeMillis();
			ArrayList<GloneTz>	altz = GloneApp.getDoc().getTzList();
			float	fcx0 = ((float)m_bm.getWidth() - GlStripe.CFTEX_HURCX) / ((float)(altz.size() - 1));
			for(GloneTz gtz0: altz) {
				Paint	paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint0.setColor(Color.GRAY);
				Canvas	cv0 = new Canvas(m_bm);
//				cv0.drawRect(0, 0, awpi0.minWidth, awpi0.minHeight, paint0);
				//
				int		nx0 = (int)(fcx0 * (float)(altz.size() - 1 - nidx));
				drawHours(cv0, nx0, gtz0);
				//
				paint0.setColor(Color.BLACK);
				final float	NC_TEXTSIZE = 12f;
				paint0.setTextSize(NC_TEXTSIZE);
				String	stzid = gtz0.getTimeZone().getDisplayName(
						gtz0.getTimeZone().inDaylightTime(new Date(lnow)), TimeZone.SHORT);
				int		ncwstr = (int)paint0.measureText(stzid);
				cv0.drawText(stzid, nx0 + GlStripe.CFTEX_HURCX - ncwstr - (int)(NC_TEXTSIZE / 2f),
						(float)m_bm.getHeight() - paint0.getFontMetrics().bottom, paint0);
				//
				nidx++;
			}

			RemoteViews	rv0 = new RemoteViews(context.getPackageName(), R.layout.widg01);
			rv0.setImageViewBitmap(R.id.iv_widg01_test1, m_bm);
//			rv0.setTextViewText(R.id.tv_widg01_test2, "dens: " + 0);

			// Register an onClickListener
			Intent		i0 = new Intent(context, WidgProv.class);
			i0.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i0.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			PendingIntent	pi0 = PendingIntent.getBroadcast(context,
					0, i0, PendingIntent.FLAG_UPDATE_CURRENT);
			rv0.setOnClickPendingIntent(R.id.iv_widg01_test1, pi0);
			awmarg.updateAppWidget(nWidgetId, rv0);
		}
	}

	private void drawHours(Canvas cv0, int nx0, GloneTz gtz0){
		final int	NC_DAYS = 3;
		Calendar	cal0 = Calendar.getInstance(gtz0.getTimeZone());
		cal0.add(Calendar.HOUR_OF_DAY, -1);
		int			nh0 = cal0.get(Calendar.HOUR_OF_DAY);
		float		fdsty = GlStripe.CFTEX_HURCY * -.4f;
		for(int i = 0; i < NC_DAYS; i++) {
			cv0.drawBitmap(m_bmtex, new Rect(0, (int)GlStripe.CFTEX_HURCY * (23 - nh0),
					(int)GlStripe.CFTEX_HURCX, (int)GlStripe.CFTEX_HURCY * (24 - nh0)),
					new RectF(nx0, fdsty, nx0 + GlStripe.CFTEX_HURCX, fdsty + GlStripe.CFTEX_HURCY), null);
			fdsty += GlStripe.CFTEX_HURCY;
			cal0.add(Calendar.HOUR_OF_DAY, +1);
			nh0 = cal0.get(Calendar.HOUR_OF_DAY);
		}
	}
}
