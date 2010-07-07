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


#include <Carbon/Carbon.h>

#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <dirent.h>
#include <sys/stat.h>
#include <string.h>
#include <search.h>


/* Contains intelligence for building a binaryTree of files */
typedef struct twalknode {
	ino_t d_ino;			/* file number of entry */
	__uint16_t d_reclen;		/* length of this record */
	__uint8_t  d_type; 		/* file type, see below */
	__uint8_t  d_namlen;		/* length of string in d_name */
	char d_name[__DARWIN_MAXNAMLEN + 1];	/* name must be no longer than this */
	bool walked;
	int  nodeID;
} treeNode;


/* Contains all VALID the nodes of a directory, plus the binary Tree of those nodes. */
struct directory {

		treeNode *treeNodes;
		void	 *binTree;
		int		 elements;
		int		 myID;
} ;




static bool qualifyFile(char *fp, char *name);	
void destroyDirectory(struct directory *_inDirPtr);
void buildDirectoryBinTree(struct directory *_inDir) ;
struct directory getDirSnapshot(void *path);
void printtree(void);
ino_t * getDirectoryInodes(char *path);
static void printme(const void *node, VISIT v, int k);
void printtree(void);
int checkFileAdded(char *testPath, struct directory *_fDir,  void (*notice)(const void *));
static int cmpTnode(const treeNode *a, const treeNode *b) ;