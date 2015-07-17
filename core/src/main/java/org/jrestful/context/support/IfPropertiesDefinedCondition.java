package org.jrestful.context.support;

import java.util.Properties;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

class IfPropertiesDefinedCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(IfPropertiesDefined.class.getName());
    String[] propertiesNames = (String[]) attributes.getFirst("value");
    if (propertiesNames.length == 0) {
      return false;
    }
    boolean propertiesDefined = true;
    String propertiesBeanName = (String) attributes.getFirst("propertiesBeanName");
    if (propertiesBeanName.isEmpty()) {
      Environment environment = context.getEnvironment();
      for (String propertyName : propertiesNames) {
        propertiesDefined &= environment.containsProperty(propertyName);
      }
    } else {
      Properties properties = (Properties) context.getBeanFactory().getBean(propertiesBeanName);
      for (String propertyName : propertiesNames) {
        propertiesDefined &= properties.containsKey(propertyName);
      }
    }
    return propertiesDefined;
  }

}
