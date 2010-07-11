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
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include "eventstream.h"
#include "treeutils.h"



#ifndef _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#define _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#ifdef __cplusplus
extern "C" {
#endif
void *FOCQ;			// File Operation Completion Queue (complete write)
char *path;
struct directory myDirectory;

void monitoringCallback(ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[]);
void init();

JNIEnv * genv;
jobject *gobj;
jstring *gjavaSpecifiedPath;
jclass *gcls;
jmethodID *gmid;

/*
 * Class:     com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
 * Method:    monitor
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_monitor
  (JNIEnv * env, jobject obj, jstring javaSpecifiedPath){

    printf (" HELLO WORLD !!! \n");

genv = env;
gobj = obj;
gjavaSpecifiedPath = javaSpecifiedPath;

	// Startup FSEvents, and monitor
	// TODO: fix thread context so that we dont fork a child during runLoop initialization 
	//       as this may break JEE.
	path = (char *)(*env)->GetStringUTFChars( env, javaSpecifiedPath , NULL ) ;
	if(path==NULL) {
		printf( "Invalid path Specified\n!" );
		return;
	}
	
	printf("going to monitor %s\n", path);
	
	// cache these
	jclass cls = (*env)->GetObjectClass(env, obj);
	jmethodID mid = (*env)->GetMethodID(env, cls, "nativeFileRecieved", "(Ljava/lang/String;)V");

gcls = cls;
gmid = mid;

	if( mid == 0 ) {
	      printf( "method callback is not valid!") ;
	      return ;
    	}
 		
	initMonitor();		
/*
	while(true) {
	
		bool newEntryExists = false;
		// TODO IMPLEMENT FOCQ polling code here
		
		// SOMETHING IMPORTANT HAPPENED HERE...
			if(newEntryExists == true) {
					char * jpath;		// Contains full path to our file
							
					if ((*env)->MonitorEnter(env, obj) != JNI_OK) {
						printf( "couldn't accquire lock!");
					}

					
					(*env)->CallVoidMethod(env, obj, mid, jpath );

					if ((*env)->MonitorExit(env, obj) != JNI_OK) {
						printf("couldn't release lock!"); // error handling
					};

			}
	}
	*/
	destroyDirectory(&myDirectory);

}
	
	
void initMonitor() {
	myDirectory = getDirSnapshot(path);
	buildDirectoryBinTree(&myDirectory);
	
	createEventStreamWithCallback(path, &monitoringCallback);
	
}


// TODO: this is an 'add' notice only currently
void FSnotice(const treeNode *tnode) {
	if(tnode==NULL) return;
	
					// synchronize access
					jstring jpath = (*genv)->NewStringUTF( genv, (const char*)tnode->d_name);
									
					if ((*genv)->MonitorEnter(genv, *gobj) != JNI_OK) {
						printf( "couldn't accquire lock!");
					}
	
					(*genv)->CallVoidMethod(genv, *gobj, *gmid, jpath );

					if ((*genv)->MonitorExit(genv, *gobj) != JNI_OK) {
						printf("couldn't release lock!"); /* error handling */
					};

		
	// TODO: Perform FOCQ add event
	
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
							
							int i=0;
							// every event for write/attribe will trigger a directory state inspection function
							//  ( this is where we determine files being added/removed )
							for( i =0; i < numEvents; i++ ) {
							
								checkFileAdded(path, &myDirectory,(void (*)(const void *))FSnotice);						 
							
							}
						}

#ifdef __cplusplus
}
#endif
#endif
#ifndef _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_FileAddedListener
#define _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_FileAddedListener
#ifdef __cplusplus
extern "C" {
#endif
#ifdef __cplusplus
}
#endif
#endif
					
