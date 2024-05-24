
import Foundation
import UIKit

weak var previousBottomSheet: UIAlertController?

class AppAlertController: UIAlertController {
    var onDismiss: (() -> Void)?
    deinit {
        onDismiss?()
        onDismiss = nil
        debugPrint("deinit AppAlertController")
    }
}

class BottomSheetAlertPresenter {
    var strongSelf: BottomSheetAlertPresenter?

    var alertWindow: UIWindow?
    var currentAlert: UIAlertController?

    private func createAlertWindow() {
        if alertWindow != nil { return }
        let frame = RCTKeyWindow()?.bounds ?? UIScreen.main.bounds
        alertWindow = UIWindow(frame: frame)
        alertWindow?.rootViewController = UIViewController()
        alertWindow?.windowLevel = UIWindow.Level.alert + 1
        alertWindow?.makeKeyAndVisible()
    }

    func present(options: NSDictionary, callback: @escaping RCTResponseSenderBlock) {
        strongSelf = self
        createAlertWindow()

        var isDark = false
        if #available(iOS 12.0, *) {
            isDark = UIScreen.main.traitCollection.userInterfaceStyle == .dark
        }
        if let theme = options["theme"] as? String {
            isDark = theme == "dark"
        }

        let completion = { [weak self] in
            guard let self = self else { return }
            previousBottomSheet = nil

            let titleOptions = options["title"] as? [String: Any]
            let messageOptions = options["message"] as? [String: Any]
            let alert = AppAlertController(
                title: titleOptions != nil ? titleOptions!["text"] as? String : nil,
                message: messageOptions != nil ? messageOptions!["text"] as? String : nil,
                preferredStyle: .actionSheet
            )

            alert.view.tintColor = RCTConvert.uiColor(options["iosTintColor"])

            let buttons = options["buttons"] as? Array<NSDictionary> ?? []
            if buttons.isEmpty { return }

            for (i, button) in buttons.enumerated() {

                var style: UIAlertAction.Style = .default
                if button["style"] as? String == "cancel" { style = .cancel }
                if button["style"] as? String == "destructive" { style = .destructive }
                
                var alignment: CATextLayerAlignmentMode = .center
                
                if let appearance = button["appearance"] as? [String: Any] {
                    alignment = appearance["textAlign"] as? String == "center" ? .center : .left
                }

                let action = UIAlertAction(title: button["text"] as? String, style: style) { _ in
                    callback([i])
                }
                if alignment != .center {
                    action.setValue(alignment, forKey: "titleTextAlignment")
                }
                //galleryActionButton.setValue(#imageLiteral(resourceName: "gallery"), forKey: "image")
                alert.addAction(action)
                previousBottomSheet = alert
            }
            guard let controller = self.alertWindow?.rootViewController else { return }
            if #available(iOS 13.0, *) {
                alert.overrideUserInterfaceStyle = isDark ? .dark : .light
            }
            controller.present(alert, animated: true)
            alert.onDismiss = { [weak self] in
                debugPrint("didDismiss")
                controller.removeFromParent()
                previousBottomSheet = nil
                self?.alertWindow = nil
                self?.strongSelf = nil
            }
        }

        if previousBottomSheet == nil { completion() }
        else { previousBottomSheet?.dismiss(animated: true, completion: completion) }
    }

    deinit {
        debugPrint("deinit presenter")
    }
}
