package com.alerts


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.ContextThemeWrapper
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.alerts.AlertsModule.AlertFragment.ARG_DEFAULT_VALUE
import com.alerts.AlertsModule.AlertFragment.ARG_KEYBOARD_TYPE
import com.alerts.AlertsModule.AlertFragment.ARG_MESSAGE
import com.alerts.AlertsModule.AlertFragment.ARG_THEME
import com.alerts.AlertsModule.AlertFragment.ARG_TITLE
import com.alerts.AlertsModule.AlertFragment.ARG_TYPE
import com.alerts.AlertsModule.AlertFragment.KEY_ITEMS
import com.alerts.AlertsModule.AlertFragment.KEY_MESSAGE
import com.alerts.AlertsModule.AlertFragment.KEY_TITLE
import com.facebook.react.bridge.*
import java.lang.ref.WeakReference
import java.util.*

class AlertsModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private var presentedDialog: WeakReference<MaterialDialog>? = null

  override fun getName(): String {
    return "BaseAlert"
  }

  private fun onClick(which: Buttons, arguments: Bundle?, callback: Callback, inputText: CharSequence? = null) {
    if (arguments == null) return
    callback.invoke(arguments.getString("button__id:${which.key}"), inputText?.toString())

    presentedDialog?.clear()
    presentedDialog = null
  }

  @ReactMethod
  fun dismissTopPresented() {
    presentedDialog?.get()?.dismiss()
    presentedDialog?.clear()
    presentedDialog = null
  }

  @SuppressLint("CheckResult", "RestrictedApi")
  fun showNewAlert(arguments: Bundle, actionCallback: Callback) {
    val activity = currentActivity ?: return
    UiThreadUtil.assertOnUiThread()
    dismissTopPresented()
    val type = arguments.getString("type")
    val positive = arguments.getString(Buttons.ARG_BUTTON_POSITIVE.key)
    val neutral = arguments.getString(Buttons.ARG_BUTTON_NEUTRAL.key)
    val negative = arguments.getString(Buttons.ARG_BUTTON_NEGATIVE.key)
    val title = arguments.getString(ARG_TITLE)
    val message = arguments.getString(ARG_MESSAGE)
    val keyBoardType = arguments.getString(ARG_KEYBOARD_TYPE)
    val theme = arguments.getString(ARG_THEME)
    val contextThemeWrapper = ContextThemeWrapper(
      activity,
      if (theme == "light") R.style.MyDialogStyleLight else R.style.MyDialogStyleDark
    )
    val materialDialog = MaterialDialog(contextThemeWrapper)
    materialDialog.cancelable(false)
    if (!title.isNullOrEmpty()) {
      materialDialog.title(null, title)
    }
    if (!message.isNullOrEmpty()) {
      materialDialog.message(null, message, null)
    } else {
      materialDialog.view.contentLayout.visibility = View.GONE
    }
    if (neutral != null) {
      materialDialog.neutralButton(text = neutral) {
        onClick(Buttons.ARG_BUTTON_NEUTRAL, arguments, actionCallback, it.getInputField().text)
      }
      materialDialog.view.buttonsLayout!!.actionButtons[WhichButton.NEUTRAL.index].typeface = Typeface.DEFAULT_BOLD
    }
    if (positive != null) {
      materialDialog.positiveButton(text = positive) {
        if (type != "default") return@positiveButton
        onClick(Buttons.ARG_BUTTON_POSITIVE, arguments, actionCallback, it.getInputField().text)
      }
    }
    if (negative != null) {
      materialDialog.negativeButton(null, negative) {
        onClick(Buttons.ARG_BUTTON_NEGATIVE, arguments, actionCallback, it.getInputField().text)
      }
      materialDialog.view.buttonsLayout!!.actionButtons[WhichButton.NEGATIVE.index].updateTextColor(Color.RED)
    }
    var inputType = InputType.TYPE_CLASS_TEXT
    if (keyBoardType != null && (keyBoardType == "number-pad" || keyBoardType == "decimal-pad")) {
      inputType = InputType.TYPE_CLASS_NUMBER
    }
    if (type == "secure-text") inputType = inputType or InputType.TYPE_TEXT_VARIATION_PASSWORD
    if (type != "default") {
      materialDialog.input(
        prefill = arguments.getString("defaultValue"),
        inputType = inputType,
        waitForPositiveButton = true,
        allowEmpty = true
      ) { _, charSequence ->
        onClick(Buttons.ARG_BUTTON_NEGATIVE, arguments, actionCallback, charSequence)
      }
    }
    materialDialog.show()
    presentedDialog = WeakReference(materialDialog)
  }


  @ReactMethod
  fun alertWithArgs(options: ReadableMap, actionCallback: Callback) {
    val args = Bundle()
    if (options.hasKey("theme")) {
      args.putString(ARG_THEME, options.getString("theme"))
    }
    if (options.hasKey(KEY_TITLE)) {
      args.putString(ARG_TITLE, options.getString(KEY_TITLE))
    }
    if (options.hasKey("type")) {
      args.putString(ARG_TYPE, options.getString("type"))
    }
    if (options.hasKey(KEY_MESSAGE)) {
      args.putString(ARG_MESSAGE, options.getString(KEY_MESSAGE))
    }
    if (options.hasKey("defaultValue")) {
      args.putString(ARG_DEFAULT_VALUE, options.getString("defaultValue"))
    }
    if (options.hasKey("keyboardType")) {
      args.putString(ARG_KEYBOARD_TYPE, options.getString("keyboardType"))
    }
    if (options.hasKey(KEY_ITEMS)) {
      val items = Objects.requireNonNull(options.getArray(KEY_ITEMS))
      val ints = IntArray(items!!.size())
      Arrays.fill(ints, Int.MIN_VALUE)

      for (i in 0 until items.size()) {
        val button = items.getMap(i)
        val hashMap = button.toHashMap()
        val style = hashMap["style"] as String
        val id = hashMap["id"] as String
        val text = hashMap["text"] as String
        args.putString(style, text)
        args.putString("button__id:${style}", id)
      }
    }
    UiThreadUtil.runOnUiThread { showNewAlert(args, actionCallback) }
  }


  object AlertFragment {
    const val ARG_TITLE = "title"
    const val ARG_THEME = "theme"
    const val ARG_TYPE = "type"
    const val ARG_MESSAGE = "message"
    const val ARG_DEFAULT_VALUE = "defaultValue"
    const val ARG_KEYBOARD_TYPE = "keyboardType"
    const val KEY_ITEMS = "buttons"
    const val KEY_MESSAGE = "message"
    const val KEY_TITLE = "title"
  }

  enum class Buttons(val key: String) {
    ARG_BUTTON_POSITIVE("default"),
    ARG_BUTTON_NEGATIVE("destructive"),
    ARG_BUTTON_NEUTRAL("cancel")
  }

}
