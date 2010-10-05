//
//  FSMon.h
//  FileSystemMonitor
//
//  Created by Captain on 8/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <Foundation/NSObject.h>
#import "MYCLass.h"

@interface FSMon : NSObject {
	NSFileManager* fm;
    NSMutableArray* filesAdded;
    NSMutableDictionary* pathModificationDates;
    NSDate* appStartedTimestamp;
    NSNumber* lastEventId;
    FSEventStreamRef stream;
	id callbackDelegate;
}

- (void) messmeUp: (int) idpointer;
- (void) start;
- (void) setDelegate: (id)aDelegate;
- (void) doDelegate;
- (void) logme: (char *) msg;
- (NSString*) getLastFileAdded;
- (void) registerDefaults;
- (void) initializeEventStream;
- (void) addModifiedImagesAtPath: (NSString *)path;
- (void) updateLastEventId: (uint64_t) eventId;
- (BOOL) fileIsImage: (NSString *)path;

@end


@interface NSObject(MyDelegateMethods)
- (void)myDelegateCall;
@end

