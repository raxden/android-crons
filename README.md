Android Crons
==========

Android Crons is a util library that allows use crons on Android. Works on Android 4.1 (API level 16) and upwards.

[![Release](https://img.shields.io/github/release/raxden/AndroidCrons.svg?label=maven central)](https://jitpack.io/#raxden/AndroidCrons/) [![API](https://img.shields.io/badge/API-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=16)

## Usage

In order to use the library, there are 3 options:

**1. Gradle dependency**

 - 	Add the following to your `build.gradle`:
 ```gradle
repositories {
	    maven { url "https://jitpack.io" }
}

dependencies {
	    compile 'com.github.raxden:AndroidCrons:v2.5.0@aar'
}
```

**2. Maven**
- Add the following to your `pom.xml`:
 ```xml
<repository>
       	<id>jitpack.io</id>
	    <url>https://jitpack.io</url>
</repository>

<dependency>
	    <groupId>com.github.raxden</groupId>
	    <artifactId>AndroidCrons</artifactId>
	    <version>v2.5.0</version>
</dependency>
```

**3. clone whole repository**
 - Open your **commandline-input** and navigate to your desired destination folder (where you want to put the library)
 - Use the command `git clone https://github.com/raxden/AndroidCrons.git` to download the full AndroidCrons repository to your computer (this includes the folder of the library project as well as the example project)

### Documentation 

For a **detailed documentation**, please have a look at the [**Wiki**](https://github.com/raxden/AndroidCrons/wiki) or the [**Javadocs**](https://jitpack.io/com/github/raxden/AndroidCrons/v2.5.0/javadoc/).

Furthermore, you can also rely on the [**Sample**](https://github.com/raxden/AndroidCrons/tree/master/sample). folder and check out the example code in that project.
