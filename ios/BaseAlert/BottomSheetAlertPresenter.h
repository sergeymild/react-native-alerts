#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <ReactCodegen/AlertsSpec/AlertsSpec.h>

@interface AppAlertController : UIAlertController
@property (nonatomic, copy) void (^onDismiss)(void);
@end

@interface BottomSheetAlertPresenter : NSObject
- (void)bottomSheetAlertWithArgs:(JS::NativeAlerts::SpecBottomSheetAlertWithArgsParams &)params callback:(RCTResponseSenderBlock)callback;
@end
