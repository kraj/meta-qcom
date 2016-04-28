# Copyright (C) 2014 Linaro
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Linaro Qualcomm Landing team 4.4 Kernel"

require recipes-kernel/linux/linux-linaro-qcom.inc
require recipes-kernel/linux/linux-dtb.inc
require recipes-kernel/linux/linux-qcom-bootimg.inc

SRCBRANCH = "release/qcomlt-4.4"
SRCREV = "6ee1ac74d8115fd050cb76a5e668f11ef0c8a266"

COMPATIBLE_MACHINE = "(ifc6410|dragonboard-410c)"

KERNEL_DEFCONFIG_dragonboard-410c ?= "${S}/arch/arm64/configs/defconfig"
KERNEL_DEFCONFIG_ifc6410 ?= "${S}/arch/arm/configs/qcom_defconfig"
KERNEL_CONFIG_FRAGMENTS += "${S}/kernel/configs/distro.config"
KERNEL_IMAGETYPE_dragonboard-410c ?= "Image"
KERNEL_DEVICETREE_dragonboard-410c = "qcom/apq8016-sbc.dtb"

# fixup.bin needs to be prepended to zImage to fixup the atag mem info because of broken bootloaders.
# Without this a panic will occur upon freeing bootmem.
do_compile_append_ifc6410() {
    if [ -e "fixup.bin" ]; then
        cp ${KERNEL_OUTPUT} ${KERNEL_OUTPUT}.backup
        cat "fixup.bin" ${KERNEL_OUTPUT}.backup > ${KERNEL_OUTPUT}
        rm -f ${KERNEL_OUTPUT}.backup
    fi
}

# append DTB, since bootloader doesn't support DTB
do_compile_append_ifc6410() {
    if ! [ -e ${B}/arch/${ARCH}/boot/dts/${KERNEL_DEVICETREE} ] ; then
        oe_runmake ${KERNEL_DEVICETREE}
    fi
    cp ${KERNEL_OUTPUT} ${KERNEL_OUTPUT}.backup
    cat ${KERNEL_OUTPUT}.backup ${B}/arch/${ARCH}/boot/dts/${KERNEL_DEVICETREE} > ${KERNEL_OUTPUT}
    rm -f ${KERNEL_OUTPUT}.backup
}

# Wifi firmware has a recognizable arch :( 
ERROR_QA_remove = "arch"

QCOM_BOOTIMG_ROOTFS_dragonboard-410c = "mmcblk0p10"
QCOM_BOOTIMG_ROOTFS_ifc6410 = "mmcblk0p12"
