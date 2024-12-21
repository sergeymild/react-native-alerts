#import "BottomSheetAlertPresenter.h"
#import <React/RCTConvert.h>

static __weak UIAlertController *previousBottomSheet;

@implementation AppAlertController
- (void)dealloc {
  if (self.onDismiss) {
    self.onDismiss();
  }
  self.onDismiss = nil;
  NSLog(@"deinit AppAlertController");
}
@end

@interface BottomSheetAlertPresenter ()
@property (nonatomic, strong) BottomSheetAlertPresenter *strongSelf;
@property (nonatomic, strong) UIWindow *alertWindow;
@end

@implementation BottomSheetAlertPresenter

- (UIImage *)resizeImage:(UIImage *)image newWidth:(CGFloat)newWidth {
  if (!image || image.size.width <= newWidth) return image;
  
  CGFloat scale = newWidth / image.size.width;
  CGFloat newHeight = image.size.height * scale;
  CGSize newSize = CGSizeMake(newWidth, newHeight);
  
  UIGraphicsImageRendererFormat *format = [UIGraphicsImageRendererFormat defaultFormat];
  format.scale = 3.0;
  format.opaque = NO;
  format.preferredRange = UIGraphicsImageRendererFormatRangeStandard;
  
  UIGraphicsImageRenderer *renderer = [[UIGraphicsImageRenderer alloc] initWithSize:newSize format:format];
  UIImage *resizedImage = [renderer imageWithActions:^(UIGraphicsImageRendererContext * _Nonnull context) {
    [image drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
  }];
  
  return resizedImage;
}

- (void)createAlertWindow {
  if (self.alertWindow) return;
  CGRect frame = RCTKeyWindow().bounds;
  self.alertWindow = [[UIWindow alloc] initWithFrame:frame];
  self.alertWindow.rootViewController = [UIViewController new];
  self.alertWindow.windowLevel = UIWindowLevelAlert + 1;
  [self.alertWindow makeKeyAndVisible];
}

- (void)bottomSheetAlertWithArgs:(JS::NativeAlerts::SpecBottomSheetAlertWithArgsParams &)params callback:(RCTResponseSenderBlock)callback {
  self.strongSelf = self;
  [self createAlertWindow];
  
  BOOL isDark = NO;
  if (@available(iOS 12.0, *)) {
    isDark = [UIScreen mainScreen].traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark;
  }
  if (params.theme()) {
    isDark = [params.theme() isEqualToString:@"dark"];
  }
  
  void (^completion)(void) = ^{
    previousBottomSheet = nil;
    
    NSString *titleString = @"";
    NSString *messageString = @"";
    if (params.title().has_value()) {
      titleString = params.title().value().text();
    }
    if (params.message().has_value()) {
      messageString = params.message().value().text();
    }
    
    AppAlertController *alert = [AppAlertController alertControllerWithTitle:titleString
                                                                     message:messageString
                                                              preferredStyle:UIAlertControllerStyleActionSheet];
    
    alert.view.tintColor = [RCTConvert UIColor:params.iosTintColor()];

    if (params.buttons().size() == 0) return;
    
    
    
    for (int i = 0; i < params.buttons().size(); i++) {
      auto button = params.buttons()[i];
      UIAlertActionStyle style = UIAlertActionStyleDefault;
      if ([button.style() isEqualToString:@"cancel"]) style = UIAlertActionStyleCancel;
      if ([button.style() isEqualToString:@"destructive"]) style = UIAlertActionStyleDestructive;
      
      NSString *alignment = button.appearance().has_value() ? button.appearance().value().textAlign() : @"center";
      
      UIAlertAction *action = [UIAlertAction actionWithTitle:button.text()
                                                       style:style
                                                     handler:^(UIAlertAction * _Nonnull action) {
        callback(@[@(i)]);
      }];
      
      if (![alignment isEqualToString:@"center"]) {
        [action setValue:alignment forKey:@"titleTextAlignment"];
      }
      
      
      if (button.icon().has_value()) {
        NSString *type = button.icon().value().type();
        id iconName = button.icon().value().icon();
        UIImage *image = nil;
        if ([type isEqualToString:@"asset"]) {
          image = [self resizeImage:[UIImage imageNamed:iconName] newWidth:20];
        } else if ([type isEqualToString:@"drawable"]) {
          image = [self resizeImage:[RCTConvert UIImage:@{@"__packager_asset": @YES, @"uri": iconName}] newWidth:20];
        }
        if (image) {
          [action setValue:image forKey:@"image"];
        }
      }
      
      [alert addAction:action];
    }
    
    if (@available(iOS 13.0, *)) {
      alert.overrideUserInterfaceStyle = isDark ? UIUserInterfaceStyleDark : UIUserInterfaceStyleLight;
    }
    
    UIViewController *controller = self.alertWindow.rootViewController;
    if (alert.popoverPresentationController) {
      alert.popoverPresentationController.sourceView = controller.view;
      alert.popoverPresentationController.sourceRect = CGRectMake(controller.view.bounds.size.width / 2, controller.view.bounds.size.height / 2, 0, 0);
      alert.popoverPresentationController.permittedArrowDirections = 0;
    }
    
    [controller presentViewController:alert animated:YES completion:nil];
    alert.onDismiss = ^{
      NSLog(@"didDismiss");
      [controller removeFromParentViewController];
      previousBottomSheet = nil;
      self.alertWindow = nil;
      self.strongSelf = nil;
    };
  };
  
  if (!previousBottomSheet) {
    completion();
  } else {
    [previousBottomSheet dismissViewControllerAnimated:YES completion:completion];
  }
}

- (void)dealloc {
  NSLog(@"deinit presenter");
}

@end
