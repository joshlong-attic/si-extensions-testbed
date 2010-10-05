//
//  test.h
//  OBJC-MainSample
//
//  Created by Captain on 8/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>


@interface test : NSObject {
	int count;
}

- (int) getCount;
- (void) logme: (char *)msg;
@end
