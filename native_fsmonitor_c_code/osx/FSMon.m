//
//  FSMon.m
//  FileSystemMonitor
//
//  Created by Captain on 8/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "FSMon.h"
#include <CoreServices/CoreServices.h>

#define EXPORT __attribute__((visibility("default")))

@implementation FSMon

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


- (void) setDelegate:(id)aDelegate
{
		callbackDelegate = aDelegate;
}

- (void) doDelegate 
{
	if ( [ callbackDelegate respondsToSelector:@selector(myDelegateCall)]	) {
		[ callbackDelegate myDelegateCall ];
	}
}

- (void) myDelegateCall
{
	NSLog(@"Hi, this delegate as been reached\n");
}


void fsevents_callback(ConstFSEventStreamRef streamRef,
                       void *userData,
                       size_t numEvents,
                       void *eventPaths,
                       const FSEventStreamEventFlags eventFlags[],
                       const FSEventStreamEventId eventIds[])
{
    FSMon *ac = (FSMon *)userData;
	if ( ac !=nil )
		[ac messmeUp: 10];
		
	   FILE *newfile;
	char *msg = "hof\n";
if(msg == NULL) return;

     if (  (newfile = fopen("/tmp/foobar","a+")) == NULL ) {

        return;
     }

    fprintf(newfile, "LogLine: %s \n %x", msg, ac);

     fclose(newfile);


/*    size_t i;
    for(i=0; i < numEvents; i++){
	       [ac addModifiedImagesAtPath:[(NSArray *)eventPaths objectAtIndex:i]];
        [ac updateLastEventId:eventIds[i]];
		if( ac->callbackDelegate !=nil ) 
			{
				[ac doDelegate ];
			}
    }
*/
	
}
int k = 0;

 - (void) messmeUp: (int) idpointer
 {
	k += idpointer;
	k++;
 }

- (id) init
{
	self = [super init];
    if (self != nil) {
        fm = [NSFileManager defaultManager];
        filesAdded = [NSMutableArray new];
	}    
	return self;
}


// Returns last file added
- (NSString *) getLastFileAdded 
{
	
	NSString *_ret = (NSString *)[ filesAdded objectAtIndex:0 ];
	[ filesAdded removeObjectAtIndex:0 ];
	return _ret;
}




/**
Convert this to a main method
**/

- (void)start
{
	
	[self registerDefaults];
	appStartedTimestamp = [NSDate date];
    pathModificationDates = [[[NSUserDefaults standardUserDefaults] dictionaryForKey:@"pathModificationDates"] mutableCopy];
	lastEventId = [[NSUserDefaults standardUserDefaults] objectForKey:@"lastEventId"];
	[self initializeEventStream];
}

- (void) registerDefaults
{
	NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
	NSDictionary *appDefaults = [NSDictionary
	                             dictionaryWithObjects:[NSArray arrayWithObjects:[NSNumber numberWithUnsignedLongLong:kFSEventStreamEventIdSinceNow], [NSMutableDictionary new], nil]
	                             forKeys:[NSArray arrayWithObjects:@"lastEventId", @"pathModificationDates", nil]];
	[defaults registerDefaults:appDefaults];
}


- (void)updateLastModificationDateForPath: (NSString *)path
{
	[pathModificationDates setObject:[NSDate date] forKey:path];
}

- (NSDate *)lastModificationDateForPath: (NSString *)path
{
	if(nil != [pathModificationDates valueForKey:path]) {
		return [pathModificationDates valueForKey:path];
	}
	else{
		return appStartedTimestamp;
	}
}


- (void)updateLastEventId: (uint64_t) eventId
{
	lastEventId = [NSNumber numberWithUnsignedLongLong:eventId];
}

- (void) initializeEventStream
{
// TODO:  Modify this to get our variable directory path.
    NSString *myPath = NSHomeDirectory();
	NSArray *pathsToWatch = [NSArray arrayWithObject:myPath];
    void *appPointer = (void *)self;
    FSEventStreamContext context = {0, appPointer, NULL, NULL, NULL};
    NSTimeInterval latency = 1.0;
    stream = FSEventStreamCreate(NULL,
                                 &fsevents_callback,
                                 &context,
                                 (CFArrayRef) pathsToWatch,
                                 [lastEventId unsignedLongLongValue],
                                 (CFAbsoluteTime) latency,
                                 kFSEventStreamCreateFlagUseCFTypes
    );

    FSEventStreamScheduleWithRunLoop(stream,
                                     CFRunLoopGetCurrent(),
                                     kCFRunLoopDefaultMode);
    FSEventStreamStart(stream);
}
/*
 NOTE:
 One technique we can use to determine the exact file that has been modified is to examine the timestamp of each file in the 
 directory, checking it against the last time a file in the directory was modified. If we find a file with a timestamp later
than the previous modification, we know that it must be the file to which an event has occurred.
*/
- (void) addModifiedImagesAtPath: (NSString *)path
{
	NSArray *contents = [fm directoryContentsAtPath:path];
	NSString* fullPath = nil;
	BOOL addedImage = false;

    for(NSString* node in contents) {
        fullPath = [NSString stringWithFormat:@"%@/%@",path,node];
       
            NSDictionary *fileAttributes =
              [fm attributesOfItemAtPath:fullPath error:NULL];
            NSDate *fileModDate =
              [fileAttributes objectForKey:NSFileModificationDate];
            if([fileModDate compare:[self lastModificationDateForPath:path]] ==
              NSOrderedDescending) {
				NSLog(@"File found was: %s\n", [fullPath cString]);
                [filesAdded addObject:fullPath];
            addedImage = true;
			
            }
			       
    }

    if(addedImage){
			[ self logme: "There was an file inserted, now call something!" ];
	}

    [self updateLastModificationDateForPath:path];
}

- (BOOL)fileIsImage: (NSString *)path
{
    NSWorkspace *sharedWorkspace =
      [NSWorkspace sharedWorkspace];
	     return [sharedWorkspace type:
      [sharedWorkspace typeOfFile:path error:NULL] conformsToType:@"public.image"];
}

- (NSApplicationTerminateReply)applicationShouldTerminate: (NSApplication *)app
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:lastEventId forKey:@"lastEventId"];
    [defaults setObject:pathModificationDates forKey:@"pathModificationDates"];
    [defaults synchronize];
    FSEventStreamStop(stream);
    FSEventStreamInvalidate(stream);
    return NSTerminateNow;
}

@end
