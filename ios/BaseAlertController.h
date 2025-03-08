
#import <UIKit/UIKit.h>

@interface BaseAlertController : UIAlertController

- (void)show:(BOOL)animated completion:(void (^)(void))completion;
- (void)hide;

@end
