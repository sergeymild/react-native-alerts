#import <React/RCTAssert.h>
#import <React/RCTConvert.h>
#import "BaseAlertManager.h"
#import <React/RCTLog.h>
#import <React/RCTUtils.h>

#import "CoreModulesPlugins.h"

@implementation BaseAlertManager
RCT_EXPORT_MODULE(BaseAlert)

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

RCT_EXPORT_METHOD(dismissTopPresented) {
    dispatch_block_t block = ^{
        [self invalidate];
    };

    if ([NSThread isMainThread]) {
        block();
    } else {
        dispatch_sync(dispatch_get_main_queue(), block);
    }
}

/**
 * @param {NSDictionary} args Dictionary of the form
 *
 *   @{
 *     @"message": @"<Alert message>",
 *     @"buttons": @[
 *       @{@"<key1>": @"<title1>"},
 *       @{@"<key2>": @"<title2>"},
 *     ],
 *     @"cancelButtonKey": @"<key2>",
 *   }
 * The key from the `buttons` dictionary is passed back in the callback on click.
 * Buttons are displayed in the order they are specified.
 */
RCT_EXPORT_METHOD(alertWithArgs:(NSDictionary*)args
                  callback:(RCTResponseSenderBlock)callback)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self invalidate];
        NSString *title = [RCTConvert NSString:[args valueForKey:@"title"]];
        NSString *message = [RCTConvert NSString:[args valueForKey:@"message"]];
        NSString *theme = [RCTConvert NSString:[args valueForKey:@"theme"]];
        NSString *type = [RCTConvert NSString:[args valueForKey:@"type"]];
        NSObject* btns = [args valueForKey:@"buttons"];
        NSArray<NSDictionary *> *buttonsArray = [[NSArray<NSDictionary *> alloc] init];
        if (btns != NULL) {
            buttonsArray = [RCTConvert NSDictionaryArray:btns];
        }

        NSString *defaultValue = [RCTConvert NSString:[args valueForKey:@"defaultValue"]];
        UIKeyboardType keyboardType = [RCTConvert UIKeyboardType:[args valueForKey:@"keyboardType"]];

        if (!title && !message) {
            RCTLogError(@"Must specify either an alert title, or message, or both");
            return;
        }
        
        BaseAlertController *alertController = [BaseAlertController alertControllerWithTitle:title
                                                                                     message:nil
                                                                              preferredStyle:UIAlertControllerStyleAlert];
        if ([theme  isEqual: @"light"]) {
            if (@available(iOS 13.0, *)) {
                [alertController setOverrideUserInterfaceStyle:UIUserInterfaceStyleLight];
            }
        } else if ([theme  isEqual: @"dark"]) {
            if (@available(iOS 13.0, *)) {
                [alertController setOverrideUserInterfaceStyle:UIUserInterfaceStyleDark];
            }
        } else {
            if (@available(iOS 13.0, *)) {
                [alertController setOverrideUserInterfaceStyle:UIUserInterfaceStyleUnspecified];
            }
        }

        if ([type isEqualToString:@"plain-text"]) {
            [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
                textField.secureTextEntry = NO;
                textField.text = defaultValue;
                textField.keyboardType = keyboardType;
            }];
        }

        if ([type isEqualToString:@"secure-text"]) {
            [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
                textField.secureTextEntry = YES;
            }];
        }

        alertController.message = message;

        for (NSDictionary<NSString *, id> *button in buttonsArray) {
            //RCTLogError(@"Button definitions should have exactly one key.");
            NSString *buttonId = [RCTConvert NSString:button[@"id"]];
            NSString *buttonTitle = [RCTConvert NSString:button[@"text"]];
            NSString *rawButtonStyle = [RCTConvert NSString:button[@"style"]];
            
            UIAlertActionStyle buttonStyle = UIAlertActionStyleDefault;
            if ([rawButtonStyle isEqualToString:@"cancel"]) {
                buttonStyle = UIAlertActionStyleCancel;
            } else if ([rawButtonStyle isEqualToString:@"destructive"]) {
                buttonStyle = UIAlertActionStyleDestructive;
            }

            __weak BaseAlertController *weakAlertController = alertController;
            [alertController
             addAction:[UIAlertAction
                        actionWithTitle:buttonTitle
                        style:buttonStyle
                        handler:^(__unused UIAlertAction *action) {
                if ([type isEqualToString:@"plain-text"]) {
                    callback(@[ buttonId, [weakAlertController.textFields.firstObject text] ]);
                    [weakAlertController hide];
                } else if ([type isEqualToString:@"secure-text"]) {
                    callback(@[ buttonId, [weakAlertController.textFields.firstObject text] ]);
                    [weakAlertController hide];
                } else if ([type isEqualToString:@"default"]) {
                    callback(@[ buttonId ]);
                    [weakAlertController hide];
                } else {
                    callback(@[ buttonId, [weakAlertController.textFields.firstObject text] ]);
                    [weakAlertController hide];
                }
            }]];
        }

        self.presentedAlert = alertController;
        [alertController show:YES completion:nil];
    });
}

@end
