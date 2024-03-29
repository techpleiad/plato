package org.techpleiad.plato.api.constant;

public class Constants {

    public static final String VERSION = "/v1";
    public static final String SERVICES = "/services";
    public static final String RULES = "/rules";
    public static final String VALIDATE = "-validate";
    public static final String BRANCHES = "/branches";
    private static final String RESOLVE_INCONSISTENCY = "/resolve-inconsistency";
    private static final String ACROSS_PROFILES = "/across-profiles";
    private static final String ACROSS_BRANCHES = "/across-branches";
    private static final String CONSISTENCY_LEVEL = "-consistency-level";
    private static final String CUSTOM = "/custom";

    public static final String GET_FILES = "/getFiles";
    public static final String VERSION_SERVICES = VERSION + SERVICES;
    public static final String CUSTOM_VALIDATE = CUSTOM + VALIDATE;
    public static final String RESOLVE_INCONSISTENCY_ACROSS_PROFILES = RESOLVE_INCONSISTENCY + ACROSS_PROFILES;
    public static final String VERSION_RULES = VERSION + RULES;
    public static final String VERSION_SERVICES_BRANCHES = VERSION_SERVICES + BRANCHES;
    public static final String ACROSS_PROFILES_VALIDATE = ACROSS_PROFILES + VALIDATE;
    public static final String ACROSS_BRANCHES_VALIDATE = ACROSS_BRANCHES + VALIDATE;
    public static final String CONSISTENCY_LEVEL_VALIDATE = ACROSS_BRANCHES + CONSISTENCY_LEVEL + VALIDATE;
}
