//
//  JNIFSMon.m
//  JNITablet
//
//  Created by Captain on 8/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "JNIFSMon.h"

 <Cocoa/Cocoa.h>

/* Our global variables */
static JavaVM *g_jvm;
static jobject g_object;
static jclass g_class;
static jmethodID g_methodID;
static jstring *g_specifiedPath;

/*
** A subclass of NSApplication which overrides sendEvent and calls back into Java with the event data for mouse events.
** We don't handle tablet proximity events yet.
*/
#define EXPORT __attribute__((visibility("default")))
#ifndef _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#define _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#ifdef __cplusplus
extern "C" {
#endif
EXPORT
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
  g_jvm = vm;
  return JNI_VERSION_1_4;
}
EXPORT
static jint GetJNIEnv(JNIEnv **env, bool *mustDetach)
{
    jint getEnvErr = JNI_OK;
    *mustDetach = false;
    if (g_jvm) {
        getEnvErr = (*g_jvm)->GetEnv(g_jvm, (void **)env, JNI_VERSION_1_4);
        if (getEnvErr == JNI_EDETACHED) {
            getEnvErr = (*g_jvm)->AttachCurrentThread(g_jvm, (void **)env, NULL);
            if (getEnvErr == JNI_OK)
                *mustDetach = true;
        }
    }
    return getEnvErr;
}


@implementation JNIFSMon

-(void) mycallbackMethod 
{
		// This is my callback code.
		
}	


@end

// Setup FS Events business, and start the event stream.
void initFSMonitoring() {
	// First instantiate my JNIFSmon delegate - to do bidding of the fseent callback code:
	JNIFSMon *jniDelegate = [[JNIFSmon alloc] init];
	[jniDelegate retain];
	
	// OK, now create the actual FSEvent Monitor object, and cal it's
	// start method which will kick off the CFEventStream thread.  
	// TODO: We have to find a way to return from here.
  FSMon *myFSmon = [[FSMon alloc] init];
  [myFSmon retain];
  [myFSmon setDelegate: jniDelegate];
  [myFSmon start];
  CFRunLoopRun();
  
}

EXPORT
JNIEXPORT void JNICALL Java_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_monitor
  (JNIEnv * env, jobject obj, jstring javaSpecifiedPath){
  
	g_specifiedPath = javaSpecifiedPath;
	
	char *path;
	// Startup FSEvents, and monitor
	path = (char *)(*env)->GetStringUTFChars( env, javaSpecifiedPath , NULL ) ;
	if(path==NULL) {
		printf( "Invalid path Specified\n!" );
		return;
	}
	
	printf("going to monitor %s\n", path);
	
    g_object = (*env)->NewGlobalRef( env, this );
    g_class = (*env)->GetObjectClass( env, this );
    g_class = (*env)->NewGlobalRef( env, g_class );
	
	
    if ( g_class != (jclass)0 )
        g_methodID = (*env)->GetMethodID( env, g_class, "nativeFileReceived", "(Ljava/lang/String;)V" );

	if ( g_methodID == 0 ) {
		printf("method call is not valid!");
		return;
	}
		
	// Initialize the fSMON here
	initFSMonitoring();
	

	// Shutdown here

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
