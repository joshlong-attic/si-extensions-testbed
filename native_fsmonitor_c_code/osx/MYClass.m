//
//  MYClass.m
//  OBJC-MainSample
//
//  Created by Captain on 8/25/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "MYClass.h"


@implementation MYClass

-(void)myClassWillDoSomething:(MYClass *)myclass
{
	NSLog(@"Hello there, MYClass here!");
}

- (void)myDelegateCall
{
	NSLog(@"Hello\n");
}


-(id) init
{
	self = [ super init ];
	if( self!=nil) {
		NSLog(@"New MYCLass object\n");
	}
	return self;
}
@end
