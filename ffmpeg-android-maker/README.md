# ffmpeg-android-maker

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b9a9fe4c6874e65a5e2a3f9beb15605)](https://app.codacy.com/manual/Javernaut/ffmpeg-android-maker)
[![Build Status](https://travis-ci.org/Javernaut/ffmpeg-android-maker.svg?branch=master)](https://travis-ci.org/Javernaut/ffmpeg-android-maker)
[![Android Weekly #378](https://androidweekly.net/issues/issue-378/badge)](https://androidweekly.net/issues/issue-378)
[![Android Weekly #396](https://androidweekly.net/issues/issue-396/badge)](https://androidweekly.net/issues/issue-396)

<img src="https://github.com/Javernaut/ffmpeg-android-maker/blob/master/images/output_structure.png" width="280" align="right">

Here is a script that downloads the source code of [FFmpeg](https://www.ffmpeg.org) library and assembles it for Android. The script produces shared libraries (\*.so files) as well as header files (\*.h files). The output structure is represented in the image.

The script also produces `ffmpeg` and `ffprobe` executables that can be used in Android's terminal directly or can even be embedded into an Android app. They can be found in `build` directory after the successful build.

The main focus of ffmpeg-android-maker is to prepare shared libraries for seamless integration into an Android project. The script prepares the `output` directory that is meant to be used. And it's not the only thing this project does.

By default this script downloads and builds the FFmpeg **4.2.3**, but the version can be overridden.

The details of how this script is implemented are described in this series of posts:
* [Part 1](https://proandroiddev.com/a-story-about-ffmpeg-in-android-part-i-compilation-898e4a249422)
* [Part 2](https://proandroiddev.com/a-story-about-ffmpeg-in-android-part-ii-integration-55fb217251f0)
* [Part 3](https://proandroiddev.com/a-story-about-ffmpeg-on-android-part-iii-extension-71025444896e)

The [WIKI](https://github.com/Javernaut/ffmpeg-android-maker/wiki) contains a lot of useful information.

## Customization

The actual content of `output` directory depends on how the FFmpeg was configured before assembling. The [master](https://github.com/Javernaut/ffmpeg-android-maker) branch of ffmpeg-android-maker builds 'vanilla' version of FFmpeg. This means all default components and shared libraries are built (according to the image).

The [what-the-codec](https://github.com/Javernaut/ffmpeg-android-maker/tree/what-the-codec) branch contains certain customizations in build scripts of FFmpeg and certain external libraries. These customizations are meant to be an example of how this project can be tuned to obtain the only functionality that is actually needed. What is actually customized can be seen [here](https://github.com/Javernaut/ffmpeg-android-maker/commit/734a4e98c41576b8b9fcf032e0754315b5b77a82).

The [WhatTheCodec](https://github.com/Javernaut/WhatTheCodec) Android app uses only a subset of FFmpeg's functionality, so the redundant parts are not even compiled. This gives much smaller output binaries.

Also there are a lot of arguments that you can pass to the `ffmpeg-android-maker.sh` script for tuning certain features. Check this [WIKI page](https://github.com/Javernaut/ffmpeg-android-maker/wiki/Available-script-arguments) out for more info.

## Supported Android ABIs

* armeabi-v7a (with NEON)
* arm64-v8a
* x86
* x86_64

You can build only some of these ABIs by specifying a [flag](https://github.com/Javernaut/ffmpeg-android-maker/wiki/Available-script-arguments#desired-abis-to-build).

## Supported host OS

On **macOS** or **Linux** just execute the ffmpeg-android-maker.sh script in terminal. Please follow the instructions in [Requirements](#Requirements) section.

~~It is also possible to execute this script on a **Windows** machine with [MSYS2](https://www.msys2.org). You also need to install specific packages to it: *make*, *git*, *diffutils* and *tar*. The script supports both 32-bit and 64-bit versions of Windows. Also see Prerequisites section for necessary software.~~

Since v2.0.0 the MSYS2 support is temporary absent.

Since v2.1.1 the **Windows** support is done with [Docker](https://www.docker.com) tool.
Check [this WIKI page](https://github.com/Javernaut/ffmpeg-android-maker/wiki/Docker-support) for more info.

## Requirements

The script expects to use **at least** Android NDK **r19** (both **r20** and **r21** also work ok).

Before the script is executed you have to define two environment variables:
* `ANDROID_SDK_HOME` - path to your Android SDK
* `ANDROID_NDK_HOME` - path to your Android NDK

Certain external libraries require additional software to be installed. Check this [WIKI page](https://github.com/Javernaut/ffmpeg-android-maker/wiki/Supported-external-libraries) out for more info. Note that if you don't need these external libraries then you also don't need to install the additional software. These external libraries are not built by default.

## Features

**Add an arbitrary external library that FFMpeg supports to the building process**. Just specify how the source code needs to be downloaded and how to perform the build operation. More about this is [here](https://github.com/Javernaut/ffmpeg-android-maker/wiki/External-libraries-integration).

**Setting your own FFmpeg version and origin**. You can actually override the version of FFmpeg used by the script. See details [here](https://github.com/Javernaut/ffmpeg-android-maker/wiki/Setting-the-FFmpeg-version).

**Test your script in a cloud**. This repository has CI integration and you can use it too for your own configurations. See details [here](https://github.com/Javernaut/ffmpeg-android-maker/wiki/Build-automation).

**Text relocations monitoring**. After an assembling is finished you can look into stats/text-relocations.txt file. This file lists all \*.so files that were built and reports if any of them has text relocations. If you don't see any mentioning of 'TEXTREL' in the file, you are good. Otherwise, you will see exact binaries that have this problem. The Travis CI build will automatically fail if text relocations occur.

## License

The ffmpeg-android-maker's source code is available under the MIT license. See the `LICENSE.txt` file for more details.