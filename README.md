Android Crons
========== 
 
Android Crons is a util library that allows use crons on Android. Works on Android 4.1 (API level 16) and upwards. 
 
[![Release](https://img.shields.io/github/tag/raxden/android-crons.svg?label=Download)](https://jitpack.io/#raxden/android-crons/)
[![API](https://img.shields.io/badge/API-17%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=17)

## Usage

In order to use the library, there are 3 options:

**1. Gradle dependency**

 - 	Add the following to your `build.gradle`:
 ```gradle
repositories {
	    maven { url "https://jitpack.io" }
}

dependencies {
	    compile 'com.github.raxden:android-crons:4.0.7@aar'
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
	    <artifactId>AndroidNavigation</artifactId>
	    <version>4.0.7</version>
</dependency>
```

**3. clone whole repository**
 - Open your **commandline-input** and navigate to your desired destination folder (where you want to put the library)
 - Use the command `git clone https://github.com/raxden/android-crons.git` to download the full android-crons repository to your computer (this includes the folder of the library project as well as the example project)

## LICENSE

    Copyright 2015 Ángel Gómez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.