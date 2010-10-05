//
//  test.m
//  OBJC-MainSample
//
//  Created by Captain on 8/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "test.h"


@implementation test


- (id) init 
{
	self = [super init ];
	if( self != nil) {
		count++;
	}
	return self;
}


-(int) getCount
{
	return count;
}

//TODO : move me to a global include !
-(void) logme: (char *)msg 
{
     FILE *newfile;

if(msg == NULL) return;

     if (  (newfile = fopen("/tmp/foobar","a+")) == NULL ) {

        return;
     }

    fprintf(newfile, "LogLine: %s\n", msg);

     fclose(newfile);

}


@end
