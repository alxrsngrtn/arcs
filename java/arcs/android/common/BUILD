load("//third_party/java/arcs/build_defs:build_defs.bzl", "arcs_kt_android_library")

licenses(["notice"])

package(default_visibility = ["//java/arcs:allowed-packages"])

exports_files(["AndroidManifest.xml"])

arcs_kt_android_library(
    name = "common",
    srcs = glob(["*.kt"]),
    manifest = "AndroidManifest.xml",
    deps = ["//java/arcs/core/util:utils-platform-dependencies"],
)
