
#import "generated/RNAlertsSpec/RNAlertsSpec.h"
#import "BaseAlertController.h"

@interface Alerts : NSObject <NativeAlertsSpec>
@property (weak, nonatomic) BaseAlertController *presentedAlert;
@end
