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

   Josh Long (josh@joshlong.com)
   Mario Gray (mario.gray@gmail.com)
   Robert Roland (rob.roland@gmail.com)
*/

#include "stdafx.h"
#include "fsmon.h"
#include <jni.h>
#include <msclr/marshal.h>

#using <System.dll>
#using <mscorlib.dll>

using namespace System;
using namespace System::IO;
using namespace System::Runtime::InteropServices;
using namespace System::Security::Permissions;
using namespace msclr::interop;

public ref class NativeFileSystemWatcher
{
private:
	static JNIEnv * env;
	static jobject obj;
	static jstring javaSpecifiedPath;
	static jmethodID mid;

	static void OnCreated(Object^ source, FileSystemEventArgs^ args)
	{
		System::String^ fullPath = args->FullPath;

		marshal_context ^ context = gcnew marshal_context();

		const char* pathCharPtr = context->marshal_as<const char*>(fullPath);

		jstring jpath = env->NewStringUTF(pathCharPtr);		 
		
		if (env->MonitorEnter(obj) != JNI_OK) 
		{
			printf( "couldn't accquire lock, dropping event!");

			return;
		}

		env->CallVoidMethod(obj, mid, jpath );

		if (env->MonitorExit(obj) != JNI_OK) 
		{
			printf("couldn't release lock!"); /* error handling */
		}
	}

	static void OnDeleted(Object^ source, FileSystemEventArgs^ args)
	{
	}

	static void OnChanged(Object^ source, FileSystemEventArgs^ args)
	{
	}

	static void OnRenamed(Object^ source, RenamedEventArgs^ args)
	{
	}

public:
	[PermissionSet(SecurityAction::Demand, Name="FullTrust")]
	int static RunMonitor(JNIEnv *env, jobject obj, jstring javaSpecifiedPath) 
	{
		NativeFileSystemWatcher::env = env;
		NativeFileSystemWatcher::obj = obj;
		NativeFileSystemWatcher::javaSpecifiedPath = javaSpecifiedPath;

		jclass cls = env->GetObjectClass(obj);
		mid = env->GetMethodID(cls, "nativeFileRecieved", "(Ljava/lang/String;)V");

		if(mid == 0) 
		{
	    	printf( "method callback is not valid!") ;
	    	return -1;
    	}

		char *path = (char *)env->GetStringUTFChars(javaSpecifiedPath, NULL);

		String ^pathString = gcnew String(path);

		FileSystemWatcher^ watcher = gcnew FileSystemWatcher;
		watcher->Path = pathString;

	    watcher->NotifyFilter = static_cast<NotifyFilters>( NotifyFilters::LastAccess | NotifyFilters::LastWrite 
    	    | NotifyFilters::FileName | NotifyFilters::DirectoryName );

	    watcher->Changed += gcnew FileSystemEventHandler(NativeFileSystemWatcher::OnChanged);
	    watcher->Created += gcnew FileSystemEventHandler(NativeFileSystemWatcher::OnCreated);
	    watcher->Deleted += gcnew FileSystemEventHandler(NativeFileSystemWatcher::OnDeleted);
	    watcher->Renamed += gcnew RenamedEventHandler(NativeFileSystemWatcher::OnRenamed);

		watcher->EnableRaisingEvents = true;

		return 1;
	}
};

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL Java_com_joshlong_esb_springintegration_modules_nativefs_NativeFileSystemMonitor_monitor
  (JNIEnv * env, jobject obj, jstring javaSpecifiedPath) {
}
#ifdef __cplusplus
}
#endif

// This is an example of an exported variable
FSMON_API int nfsmon=0;

// This is an example of an exported function.
FSMON_API int fnfsmon(void)
{
	return 42;
}

// This is the constructor of a class that has been exported.
// see fsmon.h for the class definition
Cfsmon::Cfsmon()
{
	return;
}
