/*
 * Copyright 2010 the original author or authors
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

 

 /*
     Contributed by:

     Mario Gray (mario.gray@gmail.com)
 */


 
 #include <fseventstream.h>
 #include <treeutils.h>								 

void createEventStream(const char path[]) 
{
	createEventStreamWithCallback(path, &myCallbackFunction);
}


/**
	Create an FSEventStream subscription for the directory we specified. 
	We will in tern be updated with any new activity.
**/
void createEventStreamWithCallback(const char path[], FSEventStreamCallback callbackFunction)
{
    /* 
	
	   Define variables and create a CFArray object containing

       CFString objects containing paths to watch.

     */
	
	const UInt8 *pptr = &path;
    CFStringRef mypath = CFStringCreateWithBytes(kCFAllocatorDefault,
												 pptr,
												 sizeof(path),
												 kCFStringEncodingMacRoman,
												 false);

    CFArrayRef pathsToWatch = CFArrayCreate(NULL, (const void **)&mypath, 1, NULL);

    void *callbackInfo = NULL; // could put stream-specific data here.

    FSEventStreamRef stream;

    CFAbsoluteTime latency = 1.0; /* Latency in seconds */

 

    /* Create the stream, passing in a callback */

    stream = FSEventStreamCreate(NULL,

        callbackFunction,

        callbackInfo,

        pathsToWatch,

        kFSEventStreamEventIdSinceNow, /* Or a previous event ID */

        latency,

        kFSEventStreamCreateFlagNone /* Flags explained in reference */

    );
	
	FSEventStreamScheduleWithRunLoop(stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
	FSEventStreamStart(stream);
	CFRunLoopRun();
	
	
}

char thedir[50]="/tmp/foo";

/**
	Example call back function.  Use only to DISPLAY what events are being triggered.
*/
void myCallbackFunction( ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[])
					{
						int i;
						char **paths = eventPaths;
						
						// printf("Callback called\n");
						for(i = 0; i < numEvents; i++ ) {
							//int count;
							/* flags are unsigned long, ids ARE ALL UINT64_T */
							printf("Change %llu in %s, flags %llu\n", eventIds[i], paths[i],
																	 eventFlags[i]);
						}
					}
					
					