package com.alerts


import android.content.res.Configuration
import com.facebook.react.bridge.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.ref.WeakReference


class BottomSheetAlertModule(reactContext: ReactApplicationContext?) : ReactContextBaseJavaModule(reactContext) {
  private var previousDialog: WeakReference<BottomSheetDialog>? = null
  override fun getName(): String {
    return "BottomSheetAlert"
  }

  @ReactMethod
  fun show(options: ReadableMap, actionCallback: Callback) {
    currentActivity?.runOnUiThread {
      if (previousDialog != null) {
        val bottomSheetDialog = previousDialog!!.get()
        bottomSheetDialog?.dismiss()
        previousDialog?.clear()
      }

      val currentNightMode = currentActivity!!.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
      var isDarkMode = false
      if (!options.hasKey("theme")) {
        when (currentNightMode) {
          Configuration.UI_MODE_NIGHT_NO -> isDarkMode = false
          Configuration.UI_MODE_NIGHT_YES -> isDarkMode = true
        }
      } else {
        isDarkMode = options.getString("theme") == "dark"
      }

      val bottomSheetDialog: BottomSheetDialog = BottomSheetAlert(currentActivity!!, options).create(isDarkMode, actionCallback)
        ?: return@runOnUiThread
      previousDialog = WeakReference(bottomSheetDialog)
      bottomSheetDialog.show()
    }
  }
}
