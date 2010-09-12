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


 
 #include <eventstream.h>
 #include <treeutils.h>								 


//TODO : move me to a global include !                                                                                                       
void logme(char *msg) {
     FILE *newfile;

if(msg == NULL) return;

     if (  (newfile = fopen("/tmp/foobar","a+")) == NULL ) {

        return;
     }

    fprintf(newfile, "LogLine: %s\n", msg);

     fclose(newfile);

}


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
	
	const UInt8 *pptr = path;
	//printf("path is: %s\n", pptr);
	char pathtxt[100];
	sprintf(pathtxt,"Path is: %s", pptr);
	logme(&pathtxt);

	CFRunLoopSourceRef myCFSource;
	CFRunLoopRef    thisRunLoop;

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


    // Get current thread's runLoop
     thisRunLoop = GetCFRunLoopFromEventLoop(GetCurrentEventLoop());//CFRunLoopGetCurrent();

    // Obtain current Thread's runloop mode
       CFStringRef thisRLMode = CFRunLoopCopyCurrentMode(thisRunLoop);
    if(thisRLMode== NULL)
        thisRLMode = kCFRunLoopDefaultMode;

    // We want it to be true!
    // This function is useful only to test the state of another thread’s run loop. When called with the current thread’s run loop, this function always returns false.
        bool isCFWaiting = CFRunLoopIsWaiting(thisRunLoop);
        char myresult[50];
        sprintf(myresult,"iswaiting?%s", isCFWaiting?"true":"false" )  ;
        logme(myresult);

    // Create our own runloopSource to add to this thread's
	   //CFRunLoopAddSource(thisRunLoop, myCFSource, thisRLMode);
	       
	FSEventStreamScheduleWithRunLoop(stream, thisRunLoop, thisRLMode);

	FSEventStreamStart(stream);
	logme("Entering CFRunLoopRun");
	CFRunLoopRun();
	

    //TODO: When complete, make this happen
	CFRunLoopStop(thisRunLoop);

	    
	EventRef theEvent;
    EventTargetRef theTarget;
        theTarget = GetEventDispatcherTarget();
        
        while (ReceiveNextEvent(0, NULL,kEventDurationForever,true, &theEvent)== noErr)
        {
                logme("An Event has happened!");
                SendEventToEventTarget (theEvent, theTarget);
                ReleaseEvent(theEvent);

        }

    logme(" Outside of Event loop run");

}



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

						char eventtxt[5];
						sprintf(&eventtxt,"%i", numEvents);
						logme("Number of events are: ");
                        logme(eventtxt);

						//printf("%i events:\n", numEvents);
						// printf("Callback called\n");
						for(i = 0; i < numEvents; i++ ) {
							//int count;
							/* flags are unsigned long, ids ARE ALL UINT64_T */
							char eventlog[1024];
							sprintf(&eventlog,"A Change %llu in %s, flags %llu\n", eventIds[i], paths[i],
																	 eventFlags[i]);
                            logme(&eventlog);																	
							//printf("A Change %llu in %s, flags %llu\n", eventIds[i], paths[i],
							//										 eventFlags[i]);

   						}
					}
					
