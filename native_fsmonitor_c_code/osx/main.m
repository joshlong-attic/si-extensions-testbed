//
//  main.m
//  OBJC-MainSample
//
//  Created by Captain on 8/23/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import "FSMon.h"
#import "test.h"

void logme (char *msg )
{
     FILE *newfile;

if(msg == NULL) return;

     if (  (newfile = fopen("/tmp/foobar","a+")) == NULL ) {

        return;
     }

    fprintf(newfile, "LogLine: %s\n", msg);

     fclose(newfile);

}


int main(int argc, char *argv[])
{
	MYClass *mc = [ [MYClass alloc] init];
	[mc myClassWillDoSomething:mc ];

	[mc retain];
	FSMon *myFSmon = [[FSMon alloc] init];
	[myFSmon retain]; 
	//[ myFSmon setDelegate: mc ];
	//[ myFSmon doDelegate ];

	[myFSmon start ];
	
	
	CFRunLoopRun();
	
	return 0;
	
	  // return NSApplicationMain(argc,  (const char **) argv);
}

