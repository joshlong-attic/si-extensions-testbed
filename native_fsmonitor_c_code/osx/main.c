/*
 *  main.c
 *  FSEventsExample
 *
 *  Created by Captain on 2/14/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "main.h"
	

// Define structure for our Directory
struct directory myDirectory;

// treeUtils callback function 
// Do something about the new file we just noticed. u
void notice(const treeNode *tnode) {
	if(tnode==NULL) return;
		printf("Filename we noticed is: %s \n", tnode->d_name);//myFile.d_ino);	
}

void callbackNotice( ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[])
					{
						int i;
						char **paths = eventPaths;
						
					
						// TODO make sure we dont decend into sub-directories (req's path[i]==watchPath )
						// printf("Callback called\n");
						for(i = 0; i < numEvents; i++ ) {
							
							/* flags are unsigned long, ids ARE ALL UINT64_T */
							printf("Change %llu in %s, flags %llu\n", eventIds[i], paths[i],
																	 eventFlags[i]);
							if( i==0 || (i>0 && strcmp(paths[i], paths[i-1])!=0) )
								checkFileAdded(paths[i], &myDirectory,(void (*)(const void *))notice);
						}
					//checkFileAdded(_dir, &myDirectory,(void (*)(const void *))notice); 
						
					}
					

void myUglyCallbackFunction( ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[])
					{
    if(buffer==NULL) { printf("initialize buffer.\n"); buffer = malloc(1024); buffer[0]=0;};
						int i;
						char **paths = eventPaths;
						printf("%i events:\n", numEvents);
						// printf("Callback called\n");
						for(i = 0; i < numEvents; i++ ) {
							//int count;
							/* flags are unsigned long, ids ARE ALL UINT64_T */
							printf("A Change %llu in %s, flags %llu\n", eventIds[i], paths[i],
																	 eventFlags[i]);

                                                        buffer[0]++;
                                                        printf("buffer: %i\n", buffer[0]);
						}
					}


int main (int argc, const char * argv[]) {
	
	char defaultdir[50] = "/tmp/foo";
	testEventStreamStatusNotify(defaultdir);
	//testDirectoryStateNotifications(defaultdir);
	
	return 0;
}

/**
	Tests the fseventstream API to make sure we intercepte all events
	and detect properly the files that get added/removed
**/
void testEventStreamStatusNotify(char *_dir) {

	system("mkdir -p /tmp/foo");
	myDirectory = getDirSnapshot(_dir);
	buildDirectoryBinTree( &myDirectory );
        buffer = malloc(120);
	createEventStreamWithCallback(_dir, &callbackNotice);// , &myUglyCallbackFunction);//,
	// use this to only test stream
        //createEventStream(_dir);
	

};

/**
	Test that our functions which keep state of a directory
	accurately report that files have been added/removed.
**/
 void testDirectoryStateNotifications(char *_dir) {

	// First create a directory with an entry,
	// and take it's snapshot
	system("mkdir -p /tmp/foo");
	system("/usr/bin/touch /tmp/foo/testdelete");
	myDirectory = getDirSnapshot(_dir);
	buildDirectoryBinTree( &myDirectory );
	
	// Change something in this directory:
	// Determine that we detected the addition/removal a file!
	system("/usr/bin/touch /tmp/foo/bar2");
	
	// wait for some time (give this thread time to catch up with FS operation)
	sleep(1);

	// Verify
	// Should notice a file was added/removed here
	checkFileAdded(_dir, &myDirectory,(void (*)(const void *))notice); 
	
	printf("\n");  // Extra space for readability

	// create a file  and allow thread to catch up
	system("/usr/bin/touch /tmp/foo/bar3");
	sleep(1);
	
	// Should notice a file was added here
	checkFileAdded(_dir, &myDirectory,(void (*)(const void *))notice); 
	

	// make many deletions that should be seen , allow to catch up
	printf("Deleting 3 files: bar2, bar3, and testdelete");
	system("/bin/rm /tmp/foo/bar2");
	system("/bin/rm /tmp/foo/testdelete");
	system("/bin/rm /tmp/foo/bar3");
	printf("\n");
	sleep(1);
	
	
	// Should notice 2 files were removed here
	checkFileAdded(_dir, &myDirectory, (void (*)(const void *))notice);

	// Free all memory
	destroyDirectory(&myDirectory);


}

