# CEFJB
JNI Bindings for CEF that gives you the sweet sweet ByteBuffer you deserve!

This is a simple project, adding simple Java bindings to [CEF (Chromium Embedded Framework)](https://github.com/chromiumembedded).
This library is very experimental and might work might not. Only dependency is the apache commons compress library for unpacking the cef libraries.
This whole thing is more a proof of concept than it is good, but some might deem it useful..so anyways

## Usage
The Library always has to be initialized with 'CEFInstance.init(...)'

Using Swing:
  create a 'CEFSwingComponent' and add it to something lika a JFrame

Other:
  run '<code>CEFFunk.createBrowser(...)</code>' to create a browser and aquire its ID.
  run 'CEFInstance.register(browserID, CEFFunk funk)' to register the browser to the specified CEFFunk Object. 
 A CEFFunk Object contains callbacks that can be overridden
 calls going to the native library can be invoked by the static methods found in CEFFunk, exmpl:
  'CEFFunk.setFocus(browserID, false);'
