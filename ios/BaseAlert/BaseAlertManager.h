#import <UIKit/UIKit.h>

#import <React/RCTBridgeModule.h>
#import <React/RCTInvalidating.h>
#import "BaseAlertController.h"

@interface BaseAlertManager : NSObject <RCTBridgeModule, RCTInvalidating>
@property (weak, nonatomic) BaseAlertController *presentedAlert;
@end
