#import <UIKit/UIKit.h>

#import <ReactCodegen/AlertsSpec/AlertsSpec.h>

#import "BaseAlertController.h"

@interface BaseAlertManager : NSObject <NativeAlertsSpec>
@property (weak, nonatomic) BaseAlertController *presentedAlert;
@end
