#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/inotify.h>
#include <malloc.h>

#define EVENT_SIZE  ( sizeof (struct inotify_event) )
#define BUF_LEN     ( 1024 * ( EVENT_SIZE + 16 ) )

/* Header for class com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor */

#ifndef _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#define _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor
 * Method:    monitor
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_monitor
  (JNIEnv * env, jobject obj, jstring javaSpecifiedPath){
     
    // - setup inotify
    // - each time we have a request, send it into the java class
    
    //   lets first work on getting inotify up and running from Java
   
      int fd = inotify_init();
  if ( fd < 0 ) {
    perror( "inotify_init" );
  }
   char * path ;

   path = (char *)(*env)->GetStringUTFChars( env, javaSpecifiedPath , NULL ) ;

//  char *path =(char*) (*env)->GetStringUTFChars(env, javaSpecifiedPath, 0);
  printf("going to monitor %s", path);
  // todo make this path refer to the variable that was passed in!
  int wd = inotify_add_watch( fd, path, /*IN_MOVE*/ IN_MOVED_TO| IN_CLOSE_WRITE/*IN_CREATE*/);
  while(1>0){
    int length = 0;
    int i = 0;
    char buffer[BUF_LEN];
    length = read( fd, buffer, BUF_LEN );  

    if ( length < 0 ) {
      perror( "read" );
    }  

    while ( i < length ) {
      struct inotify_event *event = ( struct inotify_event * ) &buffer[ i ];
      if ( event->len ) {
	if ( event->mask & IN_CLOSE_WRITE || event->mask & IN_MOVED_TO ) {
	//  if ( event->mask & IN_ISDIR ) {
	 //   printf( "The directory %s was created.\n", event->name );       
	 // } // TODO -- we don't care for now...
	  // else 
	  // { 
	    jclass cls = (*env)->GetObjectClass(env, obj);
	    
	    jmethodID mid = (*env)->GetMethodID(env, cls, "nativeFileRecieved", "(Ljava/lang/String;)V");
	    // to figure out method signature, i ran: 
	    // javap -s -p -classpath . com.joshlong.esb.springintegration.modules.nativefs.NativeFileSystemMonitor 
	    if( mid == 0 ) {
	      printf( "method callback is not valid!") ;
	      return ;
	    }
	    char *name = event->name; 
	    const int mlen = event->len;
	    char nc[mlen];
	    int indx;
	    for(indx=0; indx < event->len; indx++) {
	      char c  =(char) name[indx]; 
	      nc[indx]=c;
	    }
	    jstring jpath = (*env)->NewStringUTF( env, (const char*) nc  ); 
	    (*env)->CallVoidMethod(env, obj, mid, jpath );
	}
      }
      i += EVENT_SIZE + event->len;
    }  
  }
  ( void ) inotify_rm_watch( fd, wd );
  ( void ) close( fd );

}

#ifdef __cplusplus
}
#endif
#endif
/* Header for class com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_FileAddedListener */

#ifndef _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_FileAddedListener
#define _Included_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_FileAddedListener
#ifdef __cplusplus
extern "C" {
#endif
#ifdef __cplusplus
}
#endif
#endif
