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

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include "eventstream.h"
#include "treeutils.h"



#ifndef _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#define _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#endif


void monitoringCallback(ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[]);

/*
 * Class:     com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
 * Method:    monitor
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_monitor
  (JNIEnv * env, jobject obj, jstring javaSpecifiedPath){

	// Startup FSEvents, and monitor
	// TODO: fix thread context so that we dont fork a child during runLoop initialization 
	//       as this may break JEE.
	init();

	}
	
	
void init() {
	createEventStreamWithCallback("/tmp/foo", &monitoringCallback);
}



void monitoringCallback(ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[])
						 {
						 
							// TODO provide implemention of interafce between our business code and the JNI method
							// use the  'nativeFileRecieved' method once we've determined a file has been 
							// fully added to the tree.
							
							// every event for write/attribe will trigger a directory state inspection function
							//  ( this is where we determine files being added/removed )

						 }
