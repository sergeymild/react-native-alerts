package com.alerts


import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.ref.WeakReference


class BottomSheetAlertModule(private val activity: AppCompatActivity?) {
  private var previousDialog: WeakReference<BottomSheetDialog>? = null
  @ReactMethod
  fun show(options: ReadableMap, actionCallback: Callback) {
    UiThreadUtil.runOnUiThread {
      if (previousDialog != null) {
        val bottomSheetDialog = previousDialog!!.get()
        bottomSheetDialog?.dismiss()
        previousDialog?.clear()
      }

      val currentNightMode = activity!!.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
      var isDarkMode = false
      if (!options.hasKey("theme")) {
        when (currentNightMode) {
          Configuration.UI_MODE_NIGHT_NO -> isDarkMode = false
          Configuration.UI_MODE_NIGHT_YES -> isDarkMode = true
        }
      } else {
        isDarkMode = options.getString("theme") == "dark"
      }

      val bottomSheetDialog: BottomSheetDialog = BottomSheetAlert(activity!!, options).create(isDarkMode, actionCallback)
        ?: return@runOnUiThread
      previousDialog = WeakReference(bottomSheetDialog)
      bottomSheetDialog.show()
    }
  }
}
