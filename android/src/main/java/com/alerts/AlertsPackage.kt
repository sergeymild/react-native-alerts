package com.alerts

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider


class AlertsPackage : TurboReactPackage() {
  override fun getModule(name: String, context: ReactApplicationContext): NativeModule? {
    if (AlertsModule.NAME == name) {
      return AlertsModule(context);
    }
    return null
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val moduleInfoMap: MutableMap<String, ReactModuleInfo> = HashMap()
      moduleInfoMap[AlertsModule.NAME] = ReactModuleInfo(
        AlertsModule.NAME,
        AlertsModule.NAME,
        false,  // canOverrideExistingModule
        false,  // needsEagerInit
        false,  // isCxxModule
        true // isTurboModule
      )
      moduleInfoMap
    }
  }

}
