## Java glue layer for Shell JS and Android

Code here implements a thin layer that is portable between
Android and J2CL->JS so allow developing rapidly without
rebuilding Android apps.

Code in arcs/api is platform independent and should have no dependencies on Android API runtime.

Code in arcs/webimpl is platform dependent and expected to be reimplemented in arcs/androidimpl

Native particles will reside and be registered in arcs/builtinparticles

Note there are symbolic links between pipes-shell and
the outer shells/pipes-shell directory, that means the pipes-shell
must be build first.

## Running

1. Prerequisites
   - OSX: Ensure Xcode + command lines tools are installed.
2. Install Bazel and iBazel: `tools/sigh install @bazel/bazel @bazel/ibazel` (required once per checkout)
   - OSX / Linux: Run `install_dependencies.sh` (global install)
3. Run `tools/sigh build`
4. Run `shells/pipes-shell/web/deploy/deploy.sh`
5. Launch `hotreload.sh` and everything should build and run a server on port 6006. (Must be in javaharness directory.)
6. Visit [http://localhost:6006/javaharness_dev.html?user=harness&solo=particles/PipeApps/PipeApps.recipes&log=2]()
to try it out.
