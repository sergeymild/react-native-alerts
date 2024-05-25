package com.alerts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.facebook.drawee.drawable.DrawableUtils
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ColorPropConverter
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.net.URI

fun ReadableMap?.color(context: Context, key: String, default: Int): Int {
  if (this == null) return default
  if (!hasKey(key)) return default
  return ColorPropConverter.getColor(getDouble(key), context)
}

fun ReadableMap.double(key: String): Double? {
  if (!hasKey(key)) return null
  return getDouble(key)
}

fun ReadableMap.string(key: String): String? {
  if (!hasKey(key)) return null
  return getString(key)
}

fun ReadableMap.map(key: String): ReadableMap? {
  if (!hasKey(key)) return null
  return getMap(key)
}


fun TextView.applyTextStyle(appearance: ReadableMap?) {
  appearance?.double("fontSize")?.let {
    this.textSize = it.toFloat()
  }
  appearance?.string("textAlign")?.let {
    if (it == "center") this.gravity = Gravity.CENTER
  }
  appearance?.string("fontFamily")?.let {
    this.typeface = Typeface.createFromAsset(context.assets, "fonts/$it")
  }
}

class BottomSheetAlert(private val context: Activity, private val options: ReadableMap) {
  private val density = context.resources.displayMetrics.density
  @SuppressLint("RestrictedApi")
  fun create(isDark: Boolean, actionCallback: Callback): BottomSheetDialog? {
    val backgroundColor = if (isDark) Color.parseColor("#121212") else Color.WHITE

    val dialog = BottomSheetDialog(context)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED


    val baseLayout = LinearLayout(context)

    baseLayout.orientation = LinearLayout.VERTICAL
    baseLayout.clipChildren = false
    baseLayout.clipToPadding = false
    baseLayout.setPadding(0, 0, 0, (density * 16).toInt())
    baseLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
      it.marginEnd = (density * 16).toInt()
      it.marginStart = (density * 16).toInt()
    }

    val buttonsContainer = LayoutInflater.from(context).inflate(R.layout.base_layout, baseLayout, false) as CardView
    buttonsContainer.setCardBackgroundColor(backgroundColor)

    if (options.hasKey("buttonsBorderRadius")) {
      buttonsContainer.radius = options.getDouble("buttonsBorderRadius").toFloat()
    }

    val verticalContainer = buttonsContainer.findViewById<LinearLayout>(R.id.vertical_container)

    val buttons = options.getArray("buttons") ?: return null
    val hasHeader = options.hasKey("title") || options.hasKey("message")
    val layoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    )
    if (hasHeader) {
      val header = LayoutInflater.from(context).inflate(R.layout.sheet_header, baseLayout, false) as BottomSheetHeader
      header.configure(
        options.getMap("title"),
        options.getMap("message"),
        isDark
      )
      header.setBackgroundColor(backgroundColor)
      verticalContainer.addView(header)
    }

    var resolved = false
    val clickListener = View.OnClickListener { v: View ->
      if (resolved) return@OnClickListener
      resolved = true
      val tag = v.tag as Int
      Handler(Looper.getMainLooper()).post { dialog.dismiss() }
      actionCallback.invoke(Arguments.fromList(listOf(tag)))
    }

    val tintColor = options.color(context, "tintColor", if (isDark) Color.WHITE else Color.BLACK)

    var cancelButtonIndex = -1
    for (i in 0 until buttons.size()) {
      val readableMap = buttons.getMap(i)
      val appearance = readableMap.getMap("appearance")
      val style = readableMap.getString("style")
      val text = readableMap.getString("text")
      val icon = getIcon(context, readableMap.map("icon"))
      val isCancel = style != null && style == "cancel"
      if (isCancel) {
        cancelButtonIndex = i
        continue
      }
      val isDestructive = style != null && style == "destructive"
      var color = appearance.color(context, "color", tintColor)
      if (isDestructive) {
        color = if (isDark) Color.RED else Color.RED
      }
      val listItemView = LayoutInflater.from(context).inflate(R.layout.sheet_button, verticalContainer, false) as LinearLayout
      val titleView = listItemView.findViewById<TextView>(R.id.title)
      titleView.applyTextStyle(appearance)

      val iconView = listItemView.findViewById<AppCompatImageView>(R.id.icon)
      if (icon != null) {
        iconView.visibility = View.VISIBLE
        iconView.setImageBitmap(icon)
        iconView.supportImageTintList = ColorStateList.valueOf(color)
      }
      listItemView.tag = i
      titleView.setTextColor(color)
      titleView.text = text
      listItemView.setOnClickListener(clickListener)
      verticalContainer.addView(listItemView, layoutParams)
    }

    baseLayout.addView(buttonsContainer)

    if (cancelButtonIndex != -1) {
      val readableMap = buttons.getMap(cancelButtonIndex)
      val appearance = readableMap.getMap("appearance")
      val text = readableMap.getString("text")
      var color = if (isDark) Color.WHITE else Color.BLACK
      color = appearance.color(context, "color", color)
      val listItemView = LayoutInflater.from(context).inflate(R.layout.sheet_cancel_button, baseLayout, false) as CardView
      val titleView = listItemView.findViewById<TextView>(R.id.title)
      if (options.hasKey("cancelButtonBorderRadius")) {
        listItemView.radius = options.getDouble("cancelButtonBorderRadius").toFloat()
      }
      titleView.setTextColor(color)
      titleView.applyTextStyle(appearance)
      titleView.text = text
      listItemView.tag = cancelButtonIndex
      listItemView.setOnClickListener(clickListener)
      listItemView.setCardBackgroundColor(backgroundColor)
      baseLayout.addView(listItemView)
    }

    dialog.setOnDismissListener {
      if (resolved) return@setOnDismissListener
      resolved = true
      actionCallback.invoke(Arguments.fromList(listOf(cancelButtonIndex)))
    }

    dialog.setContentView(baseLayout)
    val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
    bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
    return dialog
  }

}


fun getIcon(activity: Activity, source: ReadableMap?): Bitmap? {
  try {
    source ?: return null
    if (source.string("type") == "drawable") {
      val resourceId = activity.resources.getIdentifier(source.string("icon"), "drawable", activity.packageName)

      return if (resourceId == 0) {
        null
      } else {
        BitmapFactory.decodeResource(activity.resources, resourceId)
      }
    }

    if (source.string("type") == "asset") {
      val asset = activity.application.assets.open(
        source.string("icon")!!
          .replace("asset:/", "")
          .replace("asset://", "")
      )
      return asset.use { BitmapFactory.decodeStream(it) }
    }
    return null
  } catch (e: Throwable) {
    return null
  }
}
