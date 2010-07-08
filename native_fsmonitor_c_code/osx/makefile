#  Copyright 2010 the original author or authors
# 
#      Licensed under the Apache License, Version 2.0 (the "License");
#      you may not use this file except in compliance with the License.
#      You may obtain a copy of the License at
# 
#          http://www.apache.org/licenses/LICENSE-2.0
#
#      Unless required by applicable law or agreed to in writing, software
#      distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
# 
#     Contributed by:
#     Mario Gray (mario.gray@gmail.com)
#     
CC=gcc
INCFLAGS=-I.
JAVAHOME=/System/Library/Frameworks/JavaVM.framework/Home
CFLAGS=-I. -I${JAVAHOME}/include
LIBFLAGS=-framework CoreServices -framework CoreFoundation -framework Carbon
LDFLAGS=-dynamiclib ${LIBFLAGS}
LIBRARY=libsifsmon.dylib
INSTALLNAME=-install_name /usr/local/lib/libsifsmon.dylib
ARCH=-arch i386 -arch x86_64

all:
	${CC} ${ARCH} ${CFLAGS} ${LIBFLAGS} ${LDFLAGS} -o ${LIBRARY}  fseventstream.c treeutils.c sifsmon.c ${INSTALLNAME}

test: 
	${CC} ${CFLAGS} ${LIBFLAGS} -o fseTest fseventstream.c treeutils.c main.c

