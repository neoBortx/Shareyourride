/**
 * Collection of macros to simplify loging operations
 * Created by Alexander Berezhnoi on 25/03/19.
 * Edited by bvillarroya in 09/09/2020
 */

#ifndef FFMPEG_WRAPPER_LOG_H
#define FFMPEG_WRAPPER_LOG_H

/**
 * Tag used in logs
 */
#define LOG_TAG  "ffmepegWrapper"

#include <android/log.h>

/**
 * In only interested in this three log levels:
 */
#define logd(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define logi(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define loge(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif //FFMPEG_WRAPPER_LOG_H
