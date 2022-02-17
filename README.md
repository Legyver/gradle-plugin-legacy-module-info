# gradle-plugin-legacy-module-info
Plugin to generate module-info for non-modular dependencies

# Usage
1. Clone the project and install it into mavenLocal
```shell
gradlew publishToMavenLocal
```
2. Add mavenLocal to your project's **settings.gradle** plugin management section
```groovy
pluginManagement {
    repositories {
        mavenLocal()
    }
}
```
3. Configure the plugin in your projects **build.gradle**
```groovy
plugins {
    id 'com.legyver.legacy-java-module-info' version '1.0-SNAPSHOT'
}
```
4. Add the jars you want to generate module-info's for
```groovy
legacyJavaModuleInfo {
    //add module-info for these non-modular jars
    module('google-cloud-core-2.4.0.jar') {
        //this will go into module-info in new google 'google-cloud-core-2.4.0.jar'
        requires('com.google.auth')
        exports('com.google.cloud')
    }
    module('google-cloud-core-http-2.4.0.jar') {
        //this will go into module-info
        exports('com.google.cloud.http')
    }
    module('google-cloud-storage-2.4.1.jar') {
        //this will go into module-info
        requires('com.google.auth')
        requires('com.google.auth.oauth2')
        requires('google.cloud.core')
        requires('google.cloud.core.http')

        exports('com.google.cloud.storage')
        exports('com.google.cloud.storage.StorageOptions')
    }
    //not a complete listing
}
```

For reference, the dependency bringing in the modules we're rewriting above is included below
```groovy
dependencies {
    //google cloud storage
    implementation('com.google.cloud:google-cloud-storage:2.4.1')
}

configurations.implementation {
    //exclude dependencies that you don't want to deal with
    //this will save us having to write modules for them in the legacyJavaModuleInfo closure
    exclude group: 'com.google.http-client', module: 'google-http-client-apache-v2'
    exclude group: 'com.google.http-client', module: 'google-http-client-appengine'
    exclude group: 'com.google.oauth-client', module: 'google-oauth-client'
    exclude group: 'com.google.code.findbugs', module: 'jsr305'
    exclude group: 'com.google.guava', module: 'listenablefuture'
    //not a complete listing
}
```
