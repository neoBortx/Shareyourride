#!/usr/bin/env bash

# This script parses arguments that were passed to ffmpeg-android-maker.sh
# and exports a bunch of varables that are used elsewhere.

# Local variables with default values (except ABIS_TO_BUILD).
# Can be overridden with specific arguments.
# See the end of this file for more description.
ABIS_TO_BUILD=()
API_LEVEL=16
SOURCE_TYPE=TAR
SOURCE_VALUE=4.2.3
BINUTILS=gnu
EXTERNAL_LIBRARIES=()

for argument in "$@"; do
  case $argument in
    # Build for only specified ABIs (separated by comma)
    --target-abis=*|-abis=*)
      IFS=',' read -ra ABIS <<< "${argument#*=}"
      for abi in "${ABIS[@]}"; do
        case $abi in
          x86|x86_64|armeabi-v7a|arm64-v8a)
            ABIS_TO_BUILD+=( "$abi" )
          ;;
          arm)
            ABIS_TO_BUILD+=( "armeabi-v7a" )
          ;;
          arm64)
            ABIS_TO_BUILD+=( "arm64-v8a" )
          ;;
          *)
            echo "Unknown ABI: $abi"
          ;;
        esac
      done
    ;;
    # Use this value as Android platform version during compilation.
    --android-api-level=*|-android=*)
      API_LEVEL="${argument#*=}"
    ;;
    # Checkout the particular tag in the FFmpeg's git repository
    --source-git-tag=*)
      SOURCE_TYPE=GIT_TAG
      SOURCE_VALUE="${argument#*=}"
    ;;
    # Checkout the particular branch in the FFmpeg's git repository
    --source-git-branch=*)
      SOURCE_TYPE=GIT_BRANCH
      SOURCE_VALUE="${argument#*=}"
    ;;
    # Download the particular tar archive by its version
    --source-tar=*)
      SOURCE_TYPE=TAR
      SOURCE_VALUE="${argument#*=}"
    ;;
    # Which binutils to use (gnu or llvm)
    --binutils=*|-binutils=*)
      binutils_value="${argument#*=}"
      case $binutils_value in
        gnu|llvm)
          BINUTILS=$binutils_value
        ;;
        *)
          echo "Unknown binutils: $binutils_value"
        ;;
      esac
    ;;
    # Arguments below enable certain external libraries to build into FFmpeg
    --enable-libaom|-aom)
      EXTERNAL_LIBRARIES+=( "libaom" )
    ;;
    --enable-libdav1d|-dav1d)
      EXTERNAL_LIBRARIES+=( "libdav1d" )
    ;;
    --enable-libmp3lame|-mp3lame|-lame)
      EXTERNAL_LIBRARIES+=( "libmp3lame" )
    ;;
    --enable-libopus|-opus)
      EXTERNAL_LIBRARIES+=( "libopus" )
    ;;
    --enable-libwavpack|-wavpack)
      EXTERNAL_LIBRARIES+=( "libwavpack" )
    ;;
    --enable-libtwolame|-twolame)
      EXTERNAL_LIBRARIES+=( "libtwolame" )
    ;;
    --enable-libspeex|-speex)
      EXTERNAL_LIBRARIES+=( "libspeex" )
    ;;
    *)
      echo "Unknown argument $argument"
    ;;
  esac
  shift
done

# if ABIS_TO_BUILD list is empty, then fill it with all supported ABIs
# The x86 is the first, because it is more likely to have Text Relocations.
# In this case the rest ABIs will not be assembled at all.
if [ ${#ABIS_TO_BUILD[@]} -eq 0 ]; then
  ABIS_TO_BUILD=( "x86" "x86_64" "armeabi-v7a" "arm64-v8a" )
fi
# The FFmpeg will be build for ABIs in this list
export FFMPEG_ABIS_TO_BUILD=${ABIS_TO_BUILD[@]}

# Saving the information FFmpeg's source code downloading
export FFMPEG_SOURCE_TYPE=$SOURCE_TYPE
export FFMPEG_SOURCE_VALUE=$SOURCE_VALUE

# A list of external libraries to build into the FFmpeg
# Elements from this list are used for strings substitution
export FFMPEG_EXTERNAL_LIBRARIES=${EXTERNAL_LIBRARIES[@]}

# Desired Android API level to use during compilation
# Will be replaced with 21 for 64bit ABIs if the value is less than 21
export DESIRED_ANDROID_API_LEVEL=${API_LEVEL}
export DESIRED_BINUTILS=${BINUTILS}
