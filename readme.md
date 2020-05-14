# XLayout - A lightweight, code-only layout engine for RoboVm in Kotlin

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.control-j.xlayout/xcore/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.control-j.xlayout/xcore/badge.png)

XLayout is a simple layout engine for code-only layouts potentially targeting multiple frameworks, but currently only  RoboVM, written in Kotlin

* Code only layouts - no more Xib files.
* Leverages Kotlin language features to define layouts in a concise, flexible and powerful way.
* Supports LinearLayout - the workhorse of automatic layout.
* Supports FrameLayout - for overlaid views.
* Deliberately uses Android terminology and concepts make it instantly familiar for cross-platform developers.
* Extremely light weight, uses UIViews directly without any re-wrapping or property delegation.
* Easily integrated into any existing RoboVM project - it's not a framework so you can use it as much or as little as you like!

To include in a Robovm project add this to build.gradle:

````
dependencies {
    implementation("com.control-j.xlayout:xcore:1.0.1")
    implementation("com.control-j.xlayout:xios:1.0.1")
}
````

Inspired by XibFree by toptensoftware

### License

XLayout

Copyright 2018 Control-J Pty Ltd

Copyright 2013 Topten Software

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this product except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 


