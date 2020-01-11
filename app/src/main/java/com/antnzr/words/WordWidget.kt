package com.antnzr.words

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.antnzr.words.WordWidget.Companion.WORD


class WordWidget : AppWidgetProvider() {
    private val TAG = WordWidget::class.java.simpleName

    companion object {
        const val WORD = "word"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate: started...")

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: started")
        super.onReceive(context, intent)

        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
            && intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
        ) {
            context?.let {
                updateAppWidget(it, AppWidgetManager.getInstance(it), getIntExtra(intent))
            }
        }

        if (intent?.action == DO_NOT_DISPLAY_WORD_ACTION
            && intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
        ) {

            context?.let {
                updateAppWidget(it, AppWidgetManager.getInstance(it), getIntExtra(intent))
            }
        }

        if (intent?.action == WORD_DETAILS_ACTION
            && intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
        ) {
            val detailsIntent = Intent(context, WordDetailsActivity::class.java)
            detailsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getIntExtra(intent))
            detailsIntent.putExtra(WORD, intent.getStringExtra(WORD))
            detailsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context?.startActivity(detailsIntent)
        }
    }
}

private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val service = LocalTsvWords()

    val randomWordsPair = service.getWord(context)

    val views: RemoteViews = RemoteViews(context.packageName, R.layout.word_widget)
        .apply {
            setOnClickPendingIntent(
                R.id.word,
                pendingIntent(appWidgetId, context, AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            )
            setOnClickPendingIntent(
                R.id.do_not_display_btn,
                pendingIntent(appWidgetId, context, DO_NOT_DISPLAY_WORD_ACTION)
            )
            setOnClickPendingIntent(
                R.id.details_btn,
                pendingIntent(appWidgetId, context, WORD_DETAILS_ACTION, randomWordsPair.from)
            )
            setOnClickPendingIntent(
                R.id.left_btn,
                pendingIntent(appWidgetId, context, PREVIOUS_WORD_ACTION)
            )
            setTextViewText(R.id.word, randomWordsPair.toString())
        }

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun pendingIntent(
    appWidgetId: Int,
    context: Context,
    action: String,
    word: String? = null
): PendingIntent {
    val intent = Intent(context, WordWidget::class.java)
    intent.action = action
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

    word.let { intent.putExtra(WORD, it) }

    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

private fun getIntExtra(intent: Intent): Int {
    return intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )
}
