package com.odistagon.glone;

import java.util.ArrayList;
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

	private static final int	NC_BMCX = 300;		// bitmap width
	private static final int	NC_BMCY = 150;		// bitmap height

	public WidgProv() {
		m_bm = Bitmap.createBitmap(NC_BMCX, NC_BMCY, Bitmap.Config.ARGB_8888);
		m_bmtex = BitmapFactory.decodeResource(
				GloneApp.getContext().getResources(), R.drawable.timestr_tex);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		float	fdens = 1f;//GloneApp.getContext().getResources().getDisplayMetrics().density;

		// Get all ids
		ComponentName	comp0 = new ComponentName(context, WidgProv.class);
		int[]	anAllWidgetIds = appWidgetManager.getAppWidgetIds(comp0);
		for(int nWidgetId : anAllWidgetIds) {
			AppWidgetProviderInfo	awpi0 = appWidgetManager.getAppWidgetInfo(nWidgetId);
			Log.d("(X)", "cx, cy = (" + awpi0.minWidth + ", " + awpi0.minHeight + ")");

			int		nidx = 0;
			long	lnow = System.currentTimeMillis();
			ArrayList<GloneTz>	altz = GloneApp.getDoc().getTzList();
			float	fcx0 = ((float)NC_BMCX) / ((float)altz.size());
			for(GloneTz gtz0: altz) {
				int[]	andt0 = gtz0.getTimeNumbers(lnow);

				Paint	paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint0.setColor(Color.GREEN);
				Canvas	cv0 = new Canvas(m_bm);
//				cv0.drawRect(0, 0, awpi0.minWidth, awpi0.minHeight, paint0);
				cv0.drawBitmap(m_bmtex, new Rect(0, (int)GlStripe.CFTEX_HURCY * (23 - andt0[4]),
						(int)GlStripe.CFTEX_HURCX, (int)GlStripe.CFTEX_HURCY * (24 - andt0[4])),
						new RectF(fcx0 * (float)nidx, 0, fcx0 * (float)(nidx + 1), NC_BMCY), null);
				paint0.setTextSize(30f);
				cv0.drawText(gtz0.getTimeZone().getDisplayName(
						gtz0.getTimeZone().inDaylightTime(new Date(lnow)), TimeZone.SHORT),
						fcx0 * (float)nidx, (float)NC_BMCY - paint0.getFontMetrics().bottom, paint0);
				//
				nidx++;
			}

			RemoteViews	rv0 = new RemoteViews(context.getPackageName(), R.layout.widg01);
			rv0.setImageViewBitmap(R.id.iv_widg01_test1, m_bm);
			rv0.setTextViewText(R.id.tv_widg01_test2, "dens: " + fdens);

			// Register an onClickListener
			Intent		i0 = new Intent(context, WidgProv.class);
			i0.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i0.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			PendingIntent	pi0 = PendingIntent.getBroadcast(context,
					0, i0, PendingIntent.FLAG_UPDATE_CURRENT);
			rv0.setOnClickPendingIntent(R.id.iv_widg01_test1, pi0);
			appWidgetManager.updateAppWidget(nWidgetId, rv0);
		}
	}

}
