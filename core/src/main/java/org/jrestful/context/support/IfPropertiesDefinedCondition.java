package org.jrestful.context.support;

import java.util.Properties;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * Loads a bean only if specified properties are defined.
 */
public class IfPropertiesDefinedCondition implements ConfigurationCondition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(IfPropertiesDefined.class.getName());
    String[] propertiesNames = (String[]) attributes.getFirst("value");
    if (propertiesNames.length == 0) {
      return false;
    }
    String propertiesBeanName = (String) attributes.getFirst("propertiesBeanName");
    if (propertiesBeanName.isEmpty()) {
      Environment environment = context.getEnvironment();
      for (String propertyName : propertiesNames) {
        if (!environment.containsProperty(propertyName)) {
          return false;
        }
      }
    } else {
      try {
        Properties properties = (Properties) context.getBeanFactory().getBean(propertiesBeanName);
        for (String propertyName : propertiesNames) {
          if (!properties.containsKey(propertyName)) {
            return false;
          }
        }
      } catch (NoSuchBeanDefinitionException ignore) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ConfigurationPhase getConfigurationPhase() {
    return ConfigurationPhase.REGISTER_BEAN;
  }

}
