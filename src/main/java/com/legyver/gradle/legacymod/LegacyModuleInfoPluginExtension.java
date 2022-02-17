package com.legyver.gradle.legacymod;

import org.gradle.api.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A data class to collect all the module information we want to add.
 * Here the class is used as extension that can be configured in the build script
 * and as input to the LegacyModuleInfoTransform that add the information to Jars.
 */
public class LegacyModuleInfoPluginExtension {
    private static final Logger logger =  LoggerFactory.getLogger(LegacyModuleInfoPluginExtension.class);

    private final Map<String, ModuleInfo> moduleInfo = new HashMap<>();
    private final Map<String, String> automaticModules = new HashMap<>();

    /**
     * Add full module information for a given Jar file.
     */
    public void module(String jarName, String moduleName, String moduleVersion) {
        module(jarName, moduleName, moduleVersion, null);
    }

    /**
     * Add full module information, including exported packages and dependencies, for a given Jar file.
     * Will guess the module from the jar name
     * moduleName: start to last dash, replacing dashes with dots
     * moduleVersion: last dash to last dot
     * example:
     *   jarName = 'google-cloud-storage-2.4.1.jar'
     *   moduleName = 'google.cloud.storage'
     *   moduleVersion = '2.4.1'
     * @param jarName the name of the jar
     * @param conf the closure that applies any additional info like requires, exports, etc.
     */
    public void module(String jarName, @Nullable Action<? super ModuleInfo> conf) {
        int lastDash = jarName.lastIndexOf('-');
        int lastDot = jarName.lastIndexOf(".jar");
        String moduleName = null;
        String moduleVersion = null;
        if (lastDash > 0 && lastDot > 0 && lastDot > lastDash) {
            moduleName = jarName.substring(0, lastDash).replaceAll("-", ".");
            moduleVersion = jarName.substring(lastDash + 1, lastDot);
        } else if (lastDash < 1){
            logger.warn("Unable to parse module or version: No dash found in jarName [{}]", jarName);
        } else if (lastDot < lastDash){
            logger.warn("Unable to parse module or version: Last dash comes after the last dot [{}]", jarName);
        } else {
            logger.warn("Unable to parse module or version: No dot found in jarName [{}]", jarName);
        }
        module(jarName, moduleName, moduleVersion, conf);
    }

    /**
     * Add full module information, including exported packages and dependencies, for a given Jar file.
     * @param jarName the name of the jar
     * @param moduleName the name of the module
     * @param moduleVersion the version of the module
     * @param conf the closure that applies any additional info like requires, exports, etc.
     */
    public void module(String jarName, String moduleName, String moduleVersion, @Nullable Action<? super ModuleInfo> conf) {
        logger.info("Registering module: [jarName: {}, moduleName {}, moduleVersion: {}]", jarName, moduleName, moduleVersion);
        ModuleInfo moduleInfo = new ModuleInfo(moduleName, moduleVersion);
        if (conf != null) {
            conf.execute(moduleInfo);
        }
        this.moduleInfo.put(jarName, moduleInfo);
    }

    protected Map<String, ModuleInfo> getModuleInfo() {
        return moduleInfo;
    }

    protected Map<String, String> getAutomaticModules() {
        return automaticModules;
    }
}
