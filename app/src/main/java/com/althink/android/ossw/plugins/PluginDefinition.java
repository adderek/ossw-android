package com.althink.android.ossw.plugins;

import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by krzysiek on 12/06/15.
 */
public class PluginDefinition implements Comparable<PluginDefinition> {

    private String pluginId;
    private String label;
    private String packageName;

    private List<PluginFunctionDefinition> functions;
    private List<PluginPropertyDefinition> properties;

    public PluginDefinition(String pluginId, String label, String packageName) {
        this.pluginId = pluginId;
        this.label = label;
        this.packageName = packageName;
        this.functions = functions;
        this.properties = properties;
    }

    public PluginDefinition(String label, String packageName) {
        this.label = label;
        this.packageName = packageName;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<PluginFunctionDefinition> getFunctions() {
        return functions;
    }

    public List<PluginPropertyDefinition> getProperties() {
        return properties;
    }

    public void setFunctions(List<PluginFunctionDefinition> functions) {
        this.functions = functions;
    }

    public void setProperties(List<PluginPropertyDefinition> properties) {
        this.properties = properties;
    }

    @Override
    public int compareTo(PluginDefinition another) {
        return label.compareTo(another.getLabel());
    }
}