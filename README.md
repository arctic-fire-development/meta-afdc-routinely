# meta-afdc-routinely
This is a third-party yocto layer for the Routine.ly GCS on Edison

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
  Full/path/to/edison-src/device-software/meta-openembedded/meta-oe \ "
  ```
3. You now can add any recipe provided by the new meta-oe layer to your image. To add opencv to the image, add it to the IMAGE_INSTALL variable.
You can do this in the edison-src/devicesoftware/meta-edison-distro/recipes-core/images/edison-image.bb file, for example.
In the particular case of opencv, to avoid bringing too many dependencies, you should also redefine a specific variable so that
the library is built without gtk support:
  ```bash
  IMAGE_INSTALL += "opencv"
  PACKAGECONFIG_pn-opencv="eigen jpeg libav png tiff v4l"
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
  BBLAYERS ?=  \
  [..]
  Full/path/to/edison-src/device-software/meta-afdc-routinely \
  ```

3. You now can add any recipe provided by the new meta-oe layer to your image. As in section 4.1, to add
opencv to the image, add it to the IMAGE_INSTALL variable. You can do this in the edison-src/devicesoftware/meta-edison-distro/recipes-core/images/edison-image.bb
file, for example. In the particular case of opencv, to avoid bringing too many dependencies, you should also redefine a specific variable so that the library is built without gtk support:

  ```bash
  IMAGE_INSTALL += "opencv"
  PACKAGECONFIG_pn-opencv="eigen jpeg libav png tiff v4l"
  ```

4. Save the file and rebuild the image as follows:
  cd edison-src
  source poky/oe-init-build-env
  bitbake edison-image

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
