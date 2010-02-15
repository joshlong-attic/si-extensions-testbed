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

#include "treeutils.h"
/**
	
	TreeUtils will work by giving differences in Directory structure (state change inference).
	Objective: to determine that a file has been added or removed.
**/
int array[] = { 1, 17, 2432, 645, 2456, 1234, 6543, 214, 3, 45, 34 };
void *dirtree;
static int cmp(const void *a, const void *b) {
	if (*(int *)a < *(int *)b) return -1;
	if (*(int *)a > *(int *)b) return 1;
	return 0;
}



/* Pass in a directory as an argument. */
int tree_test(char testPath)
{


	int i;
	for (i=0; i< sizeof(array) / sizeof(array[0]); i++) {
		void *x = tsearch(&array[i], &dirtree, &cmp);
		printf("Inserted %p\n", x);
	}
	printtree();
	void *deleted_node = tdelete(&array[2], &dirtree, &cmp);
	printf("Deleted node %p with value %d (parent node contains %d)\n",
		   deleted_node, array[2], **(int**)deleted_node);
	for (i=0; i< sizeof(array) / sizeof(array[0]); i++) {
		void *node = tfind(&array[i], &dirtree, &cmp);
		
		if (node) {
			int **x = node;
			printf("Found %d (%d) at %p\n", array[i], **x, node);
		} else {
			printf("Not found: %d\n", array[i]);
		}
	}
	exit(0);
}

// Obtain a list of the files as d_ino ( tree size should not have a limit) *
ino_t * getDirectoryInodes(char *path) {
ino_t dirEnts[1000];
struct dirent *file;
int i = 0;

	DIR *mydir = opendir(path);
	while( (file = readdir(mydir)) != NULL) {
			dirEnts[i++] = file->d_ino;
		
		// Hokey allocation
		if(i>sizeof(dirEnts)) { 
			ino_t _dirEnts[ sizeof(dirEnts)+1000];
			// TODO: implement array copy
		//	dirEnts = _dirEnts;
		}
	};


return dirEnts;
}

static void printme(const void *node, VISIT v, int k)
{
	const void *myvoid = *(void **)node;
	const int *myint = (const int *)myvoid;
	// printf("x\n");
	if (v != postorder && v != leaf) return;
	printf("%d\n", *myint);
}

void printtree(void)
{
	twalk(dirtree, &printme);
}
