// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the FSMON_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// FSMON_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef FSMON_EXPORTS
#define FSMON_API __declspec(dllexport)
#else
#define FSMON_API __declspec(dllimport)
#endif

// This class is exported from the fsmon.dll
class FSMON_API Cfsmon {
public:
	Cfsmon(void);
	// TODO: add your methods here.
};

extern FSMON_API int nfsmon;

FSMON_API int fnfsmon(void);
