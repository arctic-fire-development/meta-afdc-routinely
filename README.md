# meta-afdc-routinely
This is a third-party yocto layer for the Routine.ly GCS on Edison

These directions are for building the flash image from scratch and for setting up a repository

## Download the Edison Source
1. install build system dependencies
  - `sudo apt-get install build-essential git diffstat gawk chrpath texinfo libtool gcc-multilib`
2. make directories for bitbake to use for source download and build state storage
  - `mkdir bitbake_download_dir`
  - `mkdir bitbake_sstate_dir`
3. `wget http://downloadmirror.intel.com/24698/eng/edison-src-ww05-15.tgz`
  - or browse to the [software download](http://www.intel.com/support/edison/sb/CS-035180.htm) page for the latest
  - edison-src-ww05-15.tgz is latest at the time of writing
4. unpack
  - `tar zxvf edison-src-ww05-15.tgz`
  - `cd edison-src`

5. setup the build environment
  - `./device-software/setup.sh --dl_dir=/home/ubuntu/bitbake_download_dir --sstate_dir=/home/ubuntu/bitbake_sstate_dir`
6. Configure the shell environment with the source command below. After the command executes, the directory changes to the edison-src/build folder
  - `source poky/oe-init-build-env`
7. Now do the initial Edison build.
  - `bitbake edison-image`

## Installation

### Add the OpenEmbedded repo
directions from the Intel Edison Board Support Package (BSP) [documentation](http://download.intel.com/support/edison/sb/edisonbsp_ug_331188005.pdf) with a few modifications, primarily using .bbappend recipes in our Routinely repo
1. Get the OpenEmbedded Yocto layer collection from GitHub. We use the "daisy" branch matching the
version of Yocto that is used by the Intel® Edison software.
  ```bash
  cd edison-src/device-software
  git clone https://github.com/openembedded/meta-openembedded.git
  cd meta-openembedded
  git checkout daisy
  ```
2. Tell bitbake to look for recipes contained in the new meta-oe/ layer. Edit the build/conf/bblayers.conf
file and append the path to the new layer into the BBLAYERS variable:
  ```bash
  BBLAYERS ?= " \
  [..]
  /home/ubuntu/edison-src/device-software/meta-openembedded/meta-oe \ "
  ```

### Add the Routine.ly repo
assumes a standard image has been created by running the setup.sh script and bitbake edison-image as described in the previous sections
1. Get the Routine.ly Yocto layer from github

  ```bash
  cd edison-src/device-software
  git clone git@github.com:arctic-fire-development/meta-afdc-routinely.git
  cd meta-afdc-routinely
  ```

2. Tell bitbake to look for recipes contained in the new meta-afdc-routinely/ layer. Edit the build/conf/bblayers.conf
file and append the path to the new layer into the BBLAYERS variable:

  ```bash
  BBLAYERS ?=  "\
  [..]
  /home/ubuntu/edison-src/device-software/meta-afdc-routinely \
  "
  ```

4. Save the file and rebuild the image as follows:
  cd edison-src
  source poky/oe-init-build-env
  bitbake edison-image

## Flashing
Building all the packages from scratch can take up to 5 or 6 hours, depending on your host. After the first build
(provided you have not done any major cleanups), you can expect much faster rebuilds, depending on your host
and the amount of changes. When the bitbake process completes, images to flash are created in the edisonsrc/build/tmp/deploy/images
directory. To simplify the flash procedure, run the script below to copy the necessary
files to the build/toFlash directory.
`../device-software/utils/flash/postBuild.sh`
The images are ready to flash on the Intel® Edison Development Board. Refer to the [GSG] for details on the
flashing procedure.

## Notes
- sometime bitbake will fail with an error like this:

  ```bash
  Summary: 1 task failed:
  /export/edison/edison-src/poky/meta/recipes-kernel/linux/linux-yocto_3.10.bb, do_fetch
  Summary: There were 4 WARNING messages shown.
  Summary: There were 2 ERROR messages shown, returning a non-zero exit code.
  ```

    - sometimes you can "fix" this by just re-running bitbake
    - other times you need to edit the recipe to add a mirror
      - use google to find one
      - i've encountered this mostly with sourceforge addresses
- use .bbappend files to configure existing recipes
  - eg) gpsd
