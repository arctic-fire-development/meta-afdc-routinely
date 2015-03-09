DESCRIPTION = "nodeJS UAV Ground Control Station"
HOMEPAGE = "http://routine.ly"
LICENSE = "MIT & BSD & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "nodejs"

SRC_URI = "git://github.com/arctic-fire-development/dapper-gcs.git;branch=paths"

S = "${WORKDIR}/git"
