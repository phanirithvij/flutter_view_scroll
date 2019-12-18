#import <UIKit/UIKit.h>

@protocol NativeViewControllerDelegate <NSObject>

- (void)didTapIncrementButton;

@end

@interface NativeViewController : UIViewController
@property(strong, nonatomic) id<NativeViewControllerDelegate> delegate;
- (void)didReceiveIncrement;
@end
