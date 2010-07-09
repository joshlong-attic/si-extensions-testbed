/*
 *  main.h
 *  FSEventsExample
 *
 *  Created by Captain on 2/14/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */
 #include <fseventstream.h>
 #include <treeutils.h>

void testDirectoryStateNotifications(char *_dir) ;
void notice(const treeNode *tnode);
void testEventStreamStatusNotify(char *_dir) ;

void callbackNotice( ConstFSEventStreamRef streamRef,
						 void *clientCallBackInfo,
						 size_t numEvents,
						 void *eventPaths,
						 const FSEventStreamEventFlags eventFlags[],
						 const FSEventStreamEventId eventIds[]);
char * buffer;