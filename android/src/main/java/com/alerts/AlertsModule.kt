package com.alerts


import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.fbreact.specs.NativeAlertManagerSpec
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.UiThreadUtil
import com.google.android.material.textfield.TextInputLayout


fun View.setCornerRadius(r: Float) {
  outlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
      val left = 0
      val top = 0
      val right = view.width
      val bottom = view.height
      val cornerRadius =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r, view.resources.displayMetrics)
          .toInt()

      outline.setRoundRect(left, top, right, bottom, cornerRadius.toFloat())
    }
  }
  clipToOutline = true
}

fun colorStateList(color: Int): ColorStateList {
  val states = arrayOf(intArrayOf(android.R.attr.state_enabled))
  val colors = intArrayOf(color)

  return ColorStateList(states, colors)
}

class AlertsModule(reactContext: ReactApplicationContext) :
  NativeAlertsSpec(reactContext) {
  companion object {
    const val NAME: String = "Alerts"
  }

  override fun getName(): String {
    return NAME
  }

  override fun dismissTopPresented() {

  }

  override fun alertWithArgs(options: ReadableMap, actionCallback: Callback) {
    UiThreadUtil.runOnUiThread {
      var alertDialog: AlertDialog? = null
      val dialogBuilder = AlertDialog.Builder(currentActivity!!)
      val dialogView = LayoutInflater.from(currentActivity!!).inflate(R.layout.layout_alert_prompt, null)
      dialogBuilder.setView(dialogView)
      dialogView.setCornerRadius(20f)

      var isDark = false

      if (options.hasKey("theme")) {
        if (options.getString("theme") == "dark") {
          isDark = true
          dialogView.setBackgroundColor(Color.parseColor("#ff212121"))
        }
      }

      if (options.hasKey("title")) {
        dialogView.findViewById<TextView>(R.id.alert_title).also {
          if (isDark) it.setTextColor(Color.WHITE)
          it.text = options.getString("title")
          it.text = options.getString("title")
          it.visibility = View.VISIBLE
        }
      }

      if (options.hasKey("message")) {
        dialogView.findViewById<TextView>(R.id.alert_message).also {
          if (isDark) it.setTextColor(Color.WHITE)
          it.text = options.getString("message")
          it.visibility = View.VISIBLE
        }
      }

      val editTexts = mutableMapOf<String, EditText>()
      if (options.hasKey("buttons")) {
        options.getArray("buttons")?.let { buttons ->
          for (i in 0 until buttons.size()) {
            val button = buttons.getMap(i)
            val hashMap = button.toHashMap()
            val style = hashMap["style"] as String
            val id = hashMap["id"] as String
            val text = hashMap["text"] as String

            val clickListener = View.OnClickListener {
              val args = Arguments.createMap()
              editTexts.forEach {
                args.putString(it.key, it.value.text?.toString())
              }

              editTexts.clear()
              alertDialog?.dismiss()
              actionCallback.invoke(id, args)
            }

            if (style == "cancel") {
              val btn = dialogView.findViewById<Button>(R.id.cancel)
              if (isDark) btn.setTextColor(Color.WHITE)
              btn.visibility = View.VISIBLE
              btn.text = text
              btn.setOnClickListener(clickListener)
            }

            if (style == "destructive") {
              val btn = dialogView.findViewById<Button>(R.id.destructive)
              btn.visibility = View.VISIBLE
              btn.text = text
              btn.setOnClickListener(clickListener)
            }

            if (style == "default") {
              val btn = dialogView.findViewById<Button>(R.id.positive)
              if (isDark) btn.setTextColor(Color.WHITE)
              btn.visibility = View.VISIBLE
              btn.text = text
              btn.setOnClickListener(clickListener)
            }
          }

        }
      }

      if (options.hasKey("fields")) {
        options.getArray("fields")?.let { fields ->
          val container = dialogView.findViewById<LinearLayout>(R.id.content)
          container.visibility = View.VISIBLE

          for (i in 0 until fields.size()) {
            val field = fields.getMap(i)
            val hashMap = field.toHashMap()
            val placeholder = hashMap["placeholder"] as? String
            val defaultValue = hashMap["defaultValue"] as? String
            val keyboardType = hashMap["keyboardType"] as? String
            val security = hashMap["security"] as? Boolean
            val id = hashMap["id"] as String

            val editText = LayoutInflater.from(dialogView.context).inflate(R.layout.input, null)
            val layout = editText.findViewById<TextInputLayout>(R.id.md_input_layout)
            layout.hint = placeholder
            layout.placeholderTextColor = colorStateList(if (isDark) Color.WHITE else Color.parseColor("#ff212121"))
            layout.hintTextColor = colorStateList(if (isDark) Color.WHITE else Color.parseColor("#ff212121"))
            layout.editText?.setTextColor(if (isDark) Color.WHITE else Color.parseColor("#ff212121"))
            layout.editText?.setText(defaultValue ?: "")

            if (keyboardType != null && (keyboardType == "number-pad" || keyboardType == "decimal-pad")) {
              layout.editText?.inputType = InputType.TYPE_CLASS_NUMBER
            }
            if (security == true) {
              layout.editText!!.inputType = layout.editText!!.inputType or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            editTexts.put(id, layout.editText!!)
            container.addView(editText)
          }

        }
      }

      alertDialog = dialogBuilder.create()
      alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      alertDialog.show()
    }
  }

  override fun bottomSheetAlertWithArgs(params: ReadableMap, callback: Callback) {
    BottomSheetAlertModule(currentActivity as AppCompatActivity?).show(params, callback)
  }
}
