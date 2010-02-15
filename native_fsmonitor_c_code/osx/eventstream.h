/*
 *  main.h
 *  FSEventsExample
 *
 *  Created by Captain on 2/14/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <CoreServices/CoreServices.h>
void myCallbackFunction( ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[]);
void createEventStream(const char path[]);						 
void createEventStreamWithCallback(const char path[], FSEventStreamCallback callbackFunction);