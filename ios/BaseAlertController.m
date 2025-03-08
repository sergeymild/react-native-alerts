
#import <React/RCTUtils.h>

#import "BaseAlertController.h"

@interface BaseAlertController ()

@property (nonatomic, strong) UIWindow *alertWindow;

@end

@implementation BaseAlertController

- (UIWindow *)alertWindow
{
  if (_alertWindow == nil) {
    _alertWindow = [[UIWindow alloc] initWithFrame:RCTKeyWindow().bounds];
    _alertWindow.rootViewController = [UIViewController new];
    _alertWindow.windowLevel = UIWindowLevelAlert + 1;
  }
  return _alertWindow;
}

- (void)show:(BOOL)animated completion:(void (^)(void))completion
{
  [self.alertWindow makeKeyAndVisible];
  [self.alertWindow.rootViewController presentViewController:self animated:animated completion:completion];
}

- (void)hide
{
  [_alertWindow setHidden:YES];
  _alertWindow.windowScene = nil;
  _alertWindow = nil;
}

@end
