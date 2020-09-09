//
// Created by Borj on 9/9/2020.
//

#include "rtsp_client.h"
#include "log.h"
#include<iostream>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavutil/frame.h>
#include <libavdevice/avdevice.h>
#include <libavutil/bprint.h>
#include <libswscale/swscale.h>

}

std::string ConvertJString(JNIEnv* env, jstring str)
{
    if ( !str ) std::string();

    const jsize len = env->GetStringUTFLength(str);
    const char* strChars = env->GetStringUTFChars(str, (jboolean *)0);

    std::string Result(strChars, len);

    env->ReleaseStringUTFChars(str, strChars);

    return Result;
}

/**
 *
 * @param metadata
 * @param ip The IP address the RTSP server
 * @return
 */
extern "C"{
JNIEXPORT void JNICALL
Java_com_bvillarroya_1creations_ffmpegWrapper_RtspClient_getStream(JNIEnv* env, jobject thiz/*,
                                                                   jstring ip*/)
{
    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

    av_register_all();
    //avdevice_register_all();
    avcodec_register_all();
    avformat_network_init();

    /*if (ip != nullptr) {
        loge("Received a null pointer as IP address");
        return;
    }*/

    //std::string sip = ConvertJString( env, ip );
    std::string sip = "192.168.42.1";

    std::string filenameSrc = "rtsp://";
    filenameSrc.append(sip);
    filenameSrc.append("/live");

    AVCodecContext *pCodecCtx;
    AVFormatContext *pFormatCtx = avformat_alloc_context();

    AVCodec *pCodec;
    AVFrame *pFrame, *pFrameRGB;
    int result = avformat_open_input(&pFormatCtx, filenameSrc.c_str(), nullptr, nullptr);

    if (result != 0) {
        loge("Unable to open the the input %s", av_err2str(result));
        return;
    }

    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 2");

    if (avformat_find_stream_info(pFormatCtx, nullptr) < 0) {
        loge("Unable to find stream");
        return;
    }

    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 3");

    av_dump_format(pFormatCtx, 0, filenameSrc.c_str(), 0);
    int videoStream = 1;

    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->coder_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = i;
            break;
        }
    }

    if (videoStream == -1) {
        return;
    }
    pCodecCtx = pFormatCtx->streams[videoStream]->codec;

    pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if (pCodec == nullptr) {
        loge("Codec not found");
        return; //codec not found
    }

    if (avcodec_open2(pCodecCtx, pCodec, nullptr) < 0) {
        loge("Unable to open the codec");
        return;
    }

    pFrame = av_frame_alloc();
    pFrameRGB = av_frame_alloc();

    uint8_t *buffer;
    int numBytes;

    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 4");

    AVPixelFormat pFormat = AV_PIX_FMT_BGR24;
    numBytes = avpicture_get_size(pFormat, pCodecCtx->width, pCodecCtx->height); //AV_PIX_FMT_RGB24
    buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
    avpicture_fill((AVPicture *) pFrameRGB, buffer, pFormat, pCodecCtx->width, pCodecCtx->height);

    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 5");

    int res;
    int frameFinished;
    AVPacket packet;
    while (res = av_read_frame(pFormatCtx, &packet) >= 0) {

        if (packet.stream_index == videoStream) {

            avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);

            if (frameFinished) {

                loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 6");
                struct SwsContext *img_convert_ctx;
                img_convert_ctx = sws_getCachedContext(NULL, pCodecCtx->width, pCodecCtx->height,
                                                       pCodecCtx->pix_fmt, pCodecCtx->width,
                                                       pCodecCtx->height, AV_PIX_FMT_BGR24,
                                                       SWS_BICUBIC, NULL, NULL, NULL);
                sws_scale(img_convert_ctx, ((AVPicture *) pFrame)->data,
                          ((AVPicture *) pFrame)->linesize, 0, pCodecCtx->height,
                          ((AVPicture *) pFrameRGB)->data, ((AVPicture *) pFrameRGB)->linesize);

                /*cv::Mat img(pFrame->height,pFrame->width,CV_8UC3,pFrameRGB->data[0]); //dst->data[0]);
                cv::imshow("display",img);
                cvWaitKey(1);*/

                av_packet_unref(&packet);
                sws_freeContext(img_convert_ctx);

            }

        }

    }

    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 7");

    av_packet_unref(&packet);

    avcodec_close(pCodecCtx);
    av_free(pFrame);
    av_free(pFrameRGB);
    avformat_close_input(&pFormatCtx);

    loge("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA 8");
}}