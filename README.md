Android Crons
==========

Android Crons is a util library that allows use crons on Android. Works on Android 4.1 (API level 16) and upwards.

[![Release](https://img.shields.io/github/tag/raxden/AndroidCrons.svg?label=Download)](https://jitpack.io/#raxden/AndroidCrons/) 
[![API](https://img.shields.io/badge/API-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=16)

## Usage

In order to use the library, there are 3 options:

**1. Gradle dependency**

 - 	Add the following to your `build.gradle`:
 ```gradle
repositories {
	    maven { url "https://jitpack.io" }
}

dependencies {
	    compile 'com.github.raxden:AndroidCrons:2.5.8@aar'
	    
        compile 'com.github.raxden:AndroidCommons::2.2.7@aar'
        compile 'org.parceler:parceler:1.1.4'
        compile 'org.parceler:parceler-api:1.1.4' 
        compile 'io.reactivex:rxandroid:1.2.1'
        compile 'io.reactivex:rxjava:1.1.6'
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
	    <version>2.5.8</version>
</dependency>
```

**3. clone whole repository**
 - Open your **commandline-input** and navigate to your desired destination folder (where you want to put the library)
 - Use the command `git clone https://github.com/raxden/AndroidCrons.git` to download the full AndroidCrons repository to your computer (this includes the folder of the library project as well as the example project)

### Documentation 

For a **detailed documentation**, please have a look at the [**Wiki**](https://github.com/raxden/AndroidCrons/wiki) or the [**Javadocs**](https://jitpack.io/com/github/raxden/AndroidCrons/2.5.8/javadoc/).

Furthermore, you can also rely on the [**Sample**](https://github.com/raxden/AndroidCrons/tree/master/sample). folder and check out the example code in that project.
