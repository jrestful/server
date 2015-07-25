package org.jrestful.context.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * Loads a bean only if specified beans are defined.
 */
public class IfBeansDefinedCondition implements ConfigurationCondition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(IfBeansDefined.class.getName());
    Set<Class<?>> requiredBeansClasses = new HashSet<>(Arrays.asList((Class<?>[]) attributes.getFirst("value")));
    if (requiredBeansClasses.size() == 0) {
      return false;
    }
    Set<Class<?>> autowirables = new HashSet<>();
    for (String candidateName : context.getRegistry().getBeanDefinitionNames()) {
      BeanDefinition candidateDefinition = context.getRegistry().getBeanDefinition(candidateName);
      if (candidateDefinition.isAutowireCandidate()) {
        String candidateClassName = candidateDefinition.getBeanClassName();
        if (candidateClassName != null) {
          try {
            Class<?> candidateClass = Class.forName(candidateClassName);
            for (Class<?> requiredBeanClass : requiredBeansClasses) {
              if (requiredBeanClass.isAssignableFrom(candidateClass)) {
                autowirables.add(requiredBeanClass);
                if (autowirables.size() == requiredBeansClasses.size()) {
                  return true;
                }
              }
            }
          } catch (ClassNotFoundException ignore) {
            continue;
          }
        }
      }
    }
    return false;
  }

  @Override
  public ConfigurationPhase getConfigurationPhase() {
    return ConfigurationPhase.REGISTER_BEAN;
  }

}
