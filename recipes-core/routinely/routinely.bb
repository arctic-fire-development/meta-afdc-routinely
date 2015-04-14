DESCRIPTION = "nodeJS UAV Ground Control Station"
HOMEPAGE = "http://routine.ly"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=4b54a1fd55a448865a0b32d41598759d"

#DEPENDS = "nodejs-native"
RDEPENDS_${PN} += "nodejs"

SRC_URI = "git://github.com/arctic-fire-development/dapper-gcs.git;branch=paths"

S = "${WORKDIR}/git"

do_compile () {
    # changing the home directory to the working directory, the .npmrc will be created in this directory
    export HOME=${WORKDIR}

    # does not build dev packages
    npm config set dev false

    # access npm registry using http
    npm set strict-ssl false
    npm config set registry http://registry.npmjs.org/

    # configure http proxy if neccessary
    if [ -n "${http_proxy}" ]; then
        npm config set proxy ${http_proxy}
    fi
    if [ -n "${HTTP_PROXY}" ]; then
        npm config set proxy ${HTTP_PROXY}
    fi

    # configure cache to be in working directory
    npm set cache ${WORKDIR}/npm_cache

    # clear local cache prior to each compile
    npm cache clear

    # compile and install  node modules in source directory
    npm --arch=${TARGET_ARCH} --production --verbose install
}
