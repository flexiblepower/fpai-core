package org.flexiblepower.appstore.server.model;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.AllPermission;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.flexiblepower.appstore.common.PermissionQuestion;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightsQuestionGenerator {
    public static interface QuestionGenerator {
        void generateQuestions(Permission permission, Set<String> coveredPermissions, List<PermissionQuestion> result);
    }

    private static final Logger logger = LoggerFactory.getLogger(RightsQuestionGenerator.class);

    private final Properties questionTemplates;

    private final Map<Class<? extends Permission>, QuestionGenerator> questionGenerators;

    public RightsQuestionGenerator(Locale locale) throws IOException {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }

        ClassLoader cl = getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream("OSGI-INF/l10n/bundle_" + locale.getLanguage() + ".properties");
        if (is == null) {
            is = cl.getResourceAsStream("OSGI-INF/l10n/bundle.properties");
        }

        questionTemplates = new Properties();
        questionTemplates.load(is);

        questionGenerators = new HashMap<Class<? extends Permission>, QuestionGenerator>();

        questionGenerators.put(AllPermission.class, new QuestionGenerator() {
            @Override
            public void generateQuestions(Permission permission,
                                          Set<String> coveredPermissions,
                                          List<PermissionQuestion> result) {
                result.add(new PermissionQuestion((String) questionTemplates.get("allpermission"),
                                                  false,
                                                  true,
                                                  coveredPermissions));
            }
        });

        questionGenerators.put(ServicePermission.class, new QuestionGenerator() {
            @Override
            public void generateQuestions(Permission permission,
                                          Set<String> coveredPermissions,
                                          List<PermissionQuestion> result) {
                ServicePermission servicePermission = (ServicePermission) permission;
                String question = null;

                boolean isAnyType = servicePermission.getName().isEmpty() || "*".equals(servicePermission.getName());
                boolean getAction = servicePermission.getActions().contains("get");
                boolean registerAction = servicePermission.getActions().contains("register");

                if (getAction) {
                    if (isAnyType) {
                        question = String.format(questionTemplates.getProperty("servicepermission_get_any"));
                    } else {
                        question = String.format(questionTemplates.getProperty("servicepermission_get"),
                                                 servicePermission.getName());
                    }
                    result.add(new PermissionQuestion(question, true, true, coveredPermissions));
                }

                if (registerAction) {
                    if (isAnyType) {
                        question = String.format(questionTemplates.getProperty("servicepermission_register_any"));
                    } else {
                        question = String.format(questionTemplates.getProperty("servicepermission_register"),
                                                 servicePermission.getName());
                    }
                    result.add(new PermissionQuestion(question, true, true, coveredPermissions));
                }
            }
        });

        questionGenerators.put(PackagePermission.class, new QuestionGenerator() {
            @Override
            public void generateQuestions(Permission permission,
                                          Set<String> coveredPermissions,
                                          List<PermissionQuestion> result) {
                PackagePermission packagePermission = (PackagePermission) permission;
                String question = null;

                boolean isAnyType = packagePermission.getName().isEmpty() || "*".equals(packagePermission.getName());
                boolean exportAction = packagePermission.getActions().contains("export");
                boolean importAction = packagePermission.getActions().contains("import");

                if (exportAction) {
                    if (isAnyType) {
                        question = String.format(questionTemplates.getProperty("packagepermission_export_any"));
                    } else {
                        question = String.format(questionTemplates.getProperty("packagepermission_export"),
                                                 packagePermission.getName());
                    }
                    result.add(new PermissionQuestion(question, true, true, coveredPermissions));
                }

                if (importAction) {
                    if (isAnyType) {
                        question = String.format(questionTemplates.getProperty("packagepermission_import_any"));
                    } else {
                        question = String.format(questionTemplates.getProperty("packagepermission_import"),
                                                 packagePermission.getName());
                    }
                    result.add(new PermissionQuestion(question, true, false, coveredPermissions));
                }
            }
        });
    }

    public List<PermissionQuestion> makeQuestions(Collection<String> permissions) {
        return makeQuestions(reduce(permissions));
    }

    private List<PermissionQuestion> makeQuestions(Map<Permission, Set<String>> permissionCoverage) {
        List<PermissionQuestion> questions = new ArrayList<PermissionQuestion>(permissionCoverage.size());

        for (Permission permission : permissionCoverage.keySet()) {
            Set<String> coveredPermissions = permissionCoverage.get(permission);
            QuestionGenerator questionGenerator = questionGenerators.get(permission.getClass());
            if (questionGenerator != null) {
                questionGenerator.generateQuestions(permission, coveredPermissions, questions);
            } else {
                questions.add(new PermissionQuestion(String.format((String) questionTemplates.get("unknownpermission"),
                                                                   permission.toString()),
                                                     false,
                                                     true,
                                                     coveredPermissions));
            }
        }

        return questions;
    }

    private Map<Permission, Set<String>> reduce(Collection<String> perms) {
        Map<Permission, Set<String>> result = new HashMap<Permission, Set<String>>();

        for (String perm : perms) {
            Set<String> list = new HashSet<String>();
            list.add(perm);
            Permission permission = parse(perm);
            if (result.containsKey(permission)) {
                result.get(permission).add(perm);
            } else {
                result.put(permission, list);
            }
        }

        List<Permission> toMatch = new ArrayList<Permission>(result.keySet());
        if (perms.size() > 1) {
            for (Permission p : toMatch) {
                if (result.containsKey(p)) {
                    Set<String> coverage = result.get(p);
                    Iterator<Permission> it = result.keySet().iterator();
                    while (it.hasNext()) {
                        Permission q = it.next();
                        if (!p.equals(q) && p.implies(q)) {
                            coverage.addAll(result.get(q));
                            it.remove();
                        }
                    }
                }
            }
        }
        logger.debug("Permissions reduced from " + perms + " to " + result);
        return result;
    }

    private Permission parse(String perm) {
        ClassLoader cl = getClass().getClassLoader();
        PermissionInfo pi = new PermissionInfo(perm);
        try {
            Class<?> clazz = cl.loadClass(pi.getType());
            Constructor<?> constructor = clazz.getConstructor(String.class, String.class);
            return (Permission) constructor.newInstance(pi.getName(), pi.getActions());
        } catch (Exception e) {
            throw new IllegalArgumentException("The permission description could not be parsed: " + e.getMessage(), e);
        }
    }
}
