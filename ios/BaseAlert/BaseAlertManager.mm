#import "BaseAlertManager.h"
#import <React/RCTConvert.h>
#import "BottomSheetAlertPresenter.h"

@implementation BaseAlertManager
RCT_EXPORT_MODULE(Alerts)
- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup {
  return true;
}


- (void)invalidate
{
  if (self.presentedAlert) {
    [self.presentedAlert.presentingViewController dismissViewControllerAnimated:YES completion:nil];
  }
  self.presentedAlert = NULL;
}

- (void)dismissTopPresented {
  RCTExecuteOnMainQueue(^{
    [self invalidate];
  });
}

- (void)alertWithArgs:(JS::NativeAlerts::SpecAlertWithArgsParams &)params callback:(RCTResponseSenderBlock)callback {
  RCTExecuteOnMainQueue(^{
    [self invalidate];
    NSString *title = params.title();
    NSString *type = params.type();
    NSString *message = params.message();
    NSString *theme = params.theme();
    auto buttonsArray = RCTConvertVecToArray(params.buttons(), ^id(JS::NativeAlerts::SpecAlertWithArgsParamsButtonsElement element) {
      return @{
        @"text": element.text(),
        @"style": element.style(),
        @"id": element.id_()
      };
    });
    auto fieldsArray = RCTConvertVecToArray(params.fields(), ^id(JS::NativeAlerts::SpecAlertWithArgsParamsFieldsElement element) {
      return @{
        @"placeholder": [RCTConvert NSString:element.placeholder()],
        @"keyboardType": [RCTConvert NSString:element.keyboardType()],
        @"defaultValue": [RCTConvert NSString:element.defaultValue()],
        @"security": element.security().has_value() ? element.security().value() ? @"1" : @"0" : @"0",
        @"id": element.id_()
      };
    });
    
    if (!title && !message) return;
    
    BaseAlertController *alertController = [BaseAlertController alertControllerWithTitle:title
                                                                                 message:nil
                                                                          preferredStyle:UIAlertControllerStyleAlert];
    if ([theme  isEqual: @"light"]) {
      [alertController setOverrideUserInterfaceStyle:UIUserInterfaceStyleLight];
    } else if ([theme  isEqual: @"dark"]) {
      [alertController setOverrideUserInterfaceStyle:UIUserInterfaceStyleDark];
    } else {
      [alertController setOverrideUserInterfaceStyle:UIUserInterfaceStyleUnspecified];
    }
    
    if ([type isEqualToString:@"prompt"]) {
      for (NSDictionary<NSString *, id> *field in fieldsArray) {
        NSString *defaultValue = (NSString*)[field valueForKey:@"defaultValue"];
        UIKeyboardType keyboardType = [RCTConvert UIKeyboardType:[field valueForKey:@"keyboardType"]];
        BOOL isSecurity = [field[@"security"] isEqual: @"1"];
        
        [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
          textField.secureTextEntry = isSecurity;
          textField.keyboardType = keyboardType;
          textField.accessibilityIdentifier = field[@"id"];
          textField.text = defaultValue;
          textField.placeholder = (NSString*)field[@"placeholder"];
        }];
      }
    }
    
    
    
    alertController.message = message;
    
    for (NSDictionary<NSString *, id> *button in buttonsArray) {
      //RCTLogError(@"Button definitions should have exactly one key.");
      NSString *buttonId = (NSString*)button[@"id"];
      NSString *buttonTitle = (NSString*)button[@"text"];
      NSString *rawButtonStyle = (NSString*)button[@"style"];
      
      UIAlertActionStyle buttonStyle = UIAlertActionStyleDefault;
      if ([rawButtonStyle isEqualToString:@"cancel"]) {
        buttonStyle = UIAlertActionStyleCancel;
      } else if ([rawButtonStyle isEqualToString:@"destructive"]) {
        buttonStyle = UIAlertActionStyleDestructive;
      }
      
      __weak BaseAlertManager *weakSelf = self;
      [alertController
       addAction:[UIAlertAction
                  actionWithTitle:buttonTitle
                  style:buttonStyle
                  handler:^(__unused UIAlertAction *action) {
        
        if ([type isEqualToString:@"prompt"]) {
          NSMutableDictionary<NSString*, NSString*>* values = [[NSMutableDictionary alloc] initWithCapacity:[fieldsArray count]];
          for (NSDictionary<NSString *, id> *field in fieldsArray) {
            NSString* fieldId = field[@"id"];
            
            for (UITextField* textField in weakSelf.presentedAlert.textFields) {
              if ([fieldId isEqualToString:textField.accessibilityIdentifier]) {
                values[fieldId] = textField.text;
              }
            }
          }
          callback(@[ buttonId, values ]);
        } else {
          callback(@[ buttonId ]);
        }
        
        
        [weakSelf.presentedAlert hide];
        weakSelf.presentedAlert = NULL;
      }]];
    }
    
    self.presentedAlert = alertController;
    [alertController show:YES completion:nil];
  });
}

- (void)bottomSheetAlertWithArgs:(JS::NativeAlerts::SpecBottomSheetAlertWithArgsParams &)params callback:(RCTResponseSenderBlock)callback {
  RCTExecuteOnMainQueue(^{
    [[[BottomSheetAlertPresenter alloc] init] bottomSheetAlertWithArgs:params callback:callback];
  });
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativeAlertsSpecJSI>(params);
}

@end
