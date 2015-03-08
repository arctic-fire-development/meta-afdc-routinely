# meta-afdc-routinely
This is a third-party yocto layer for the Routine.ly GCS on Edison

These directions are for building the flash image from scratch and for setting up a repository

## Download the Edison Source
1. install build system dependencies
  - `sudo apt-get install build-essential git diffstat gawk chrpath texinfo libtool gcc-multilib`
2. create directories for bitbake to use for source download and build state storage
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

Intel Edison Board Support Package (BSP) [documentation](http://download.intel.com/support/edison/sb/edisonbsp_ug_331188005.pdf).

### Add the OpenEmbedded repo

Get the OpenEmbedded Yocto layer collection from GitHub. We use the "daisy" branch matching the
version of Yocto that is used by the Intel® Edison software.
  ```bash
  cd edison-src/device-software
  git clone https://github.com/openembedded/meta-openembedded.git
  cd meta-openembedded
  git checkout daisy
  ```

### Add the Routine.ly repo

Get the Routine.ly Yocto layer from github

  ```bash
  cd edison-src/device-software
  git clone git@github.com:arctic-fire-development/meta-afdc-routinely.git
  ```

### Edit bblayers.conf
Tell bitbake to look for recipes contained the layers we just added. Edit the file `~/edison-src/build/conf/bblayers.conf` and append the path to the new layers into the BBLAYERS variable:

  ```bash
  BBLAYERS ?=  "\
  [..]
  /home/ubuntu/edison-src/device-software/meta-openembedded/meta-oe \
  /home/ubuntu/edison-src/device-software/meta-afdc-routinely \
  "
  ```

### Build the image
Build the image as follows:

  ```bash
  cd edison-src
  source poky/oe-init-build-env
  bitbake edison-image
  ```

## Flashing

Building all the packages from scratch can take up to 5 or 6 hours, depending on your host.

After the first build
(provided you have not done any major cleanups), you can expect much faster rebuilds, depending on your host and the amount of changes.

When the bitbake process completes, images to flash are created in the `edisonsrc/build/tmp/deploy/images` directory.

To simplify the flash procedure, run the script below to copy the necessary files to the build/toFlash directory.

`../device-software/utils/flash/postBuild.sh`

The images are ready to flash on the Intel® Edison Development Board.
Refer to the [GSG] for details on the
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

## How to enable a kernel module from our layer

If we find we need another kernel module, these are the steps to take.
- `tar zxvf edison-src-ww05-15.tgz`
- `cd edison-src`
- `./device-software/setup.sh --dl_dir=/home/ubuntu/bitbake_download_dir --sstate_dir=/home/ubuntu/bitbake_sstate_dir`
- `source poky/oe-init-build-env`
- `bitbake linux-yocto -c kernel_configme -f`
- `bitbake linux-yocto -c menuconfig`
- `bitbake linux-yocto -c diffconfig`
  - it will tell you where the generated .cfg file is at
- in your layer
  - `mkdir -p recipes-kernel/linux/files`
  - copy the generated .cfg into recipes-kernel/linux/files/<module_name>.cfg
    - `cp /home/ubuntu/edison-src/build/tmp/work/edison-poky-linux/linux-yocto/3.10.17+gitAUTOINC+6ad20f049a_c03195ed6e-r0/fragment.cfg recipes-kernel/linux/files/ftdisio.cfg`
  - make recipes-kernel/linux/linux-yocto_3.10.bbappend
    ```bash
    FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
    SRC_URI += "file://ftdisio.cfg"
    ```


## TODO
- add recipe for our git repo
  - include services, etc
  - do_install()
- add kernel config modification for ftdi
  - can just be readme instructions for enabling it via the text file for now
- add instructions for enabling a repo from this build
  - [this](http://www.jumpnowtek.com/yocto/Using-your-build-workstation-as-a-remote-package-repository.html) looks like a good resource
