# TestBox3Dretry

Second attempt at Box3D test.  Now with only the desktop platform (no teavm) and no other dependencies.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.


## Steps followed

Add this to gradle.properties:

    jbox3dVersion=-SNAPSHOT

Add this to build.gradle (core):

    repositories {
        mavenCentral()
        maven {
                url = uri("https://central.sonatype.com/repository/maven-snapshots/")
                content {
                includeGroup("com.github.xpenatan.jBox3D")
            }
        }
    }
    dependencies {
        implementation("com.github.xpenatan.jBox3D:core:$jbox3dVersion")
        runtimeOnly("com.github.xpenatan.jBox3D:desktop-jni:$jbox3dVersion")            // or in module lwjgl3?
        implementation("com.github.xpenatan.jBox3D:gdx-gl:$jbox3dVersion")              // needed?
    }


Refresh Gradle.

        gravity.Set(0f, -10f, 0f);
Cannot access com.github.xpenatan.jParser.API.nativeObject


Added jParser to build.gradle (should be pulled in via box3d:core)

F:\Coding\IdeaProjects\TestBox3d\core\src\main\java\com\monstrous\testbox3d\Main.java:13: error: package com.github.xpenatan.box3d does not exist
import com.github.xpenatan.box3d.B3Vec3;

Call JBox3DLoader.init to load the native library on startup.
Only works if this dependency is defined: runtimeOnly("com.github.xpenatan.jBox3D:desktop-jni:$jbox3dVersion") 


Problem: dummy Java methods are called instead of JNI native calls. So B3Vec3.getY() always returns 0 regardless how the value was set.



