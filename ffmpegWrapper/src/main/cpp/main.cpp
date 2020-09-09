#include <jni.h>

#include "log.h"

/**
 * Code obtained from
 * @param vm
 * @param reserved
 * @return
 */

/**
 * This function is called when the native library is loaded.
 */

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    logi("JNI ON LOAD");

    return JNI_VERSION_1_6;
}

/**
 * This function is called when the native library is unloaded.
 */
void JNI_OnUnload(JavaVM *vm, void *reserved) {
    //utils_fields_free(vm);
    logi("JNI ON UNLOAD");
}