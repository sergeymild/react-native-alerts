
import Foundation
import React


@objc(BottomSheetAlert)
class BottomSheetAlert: NSObject, RCTBridgeModule {
  static func moduleName() -> String! {
    return "BottomSheetAlert"
  }

  static func requiresMainQueueSetup() -> Bool {
    return true
  }

  @objc
  func show(_ options: NSDictionary, callback: @escaping RCTResponseSenderBlock) {
    DispatchQueue.main.async {
      BottomSheetAlertPresenter().present(options: options, callback: callback)
    }
  }
}
