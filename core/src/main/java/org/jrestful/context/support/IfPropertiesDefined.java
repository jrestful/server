package org.jrestful.context.support;

import org.springframework.core.env.Environment;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * Can be added over Spring component to create an instance only if some properties are defined.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(IfPropertiesDefinedCondition.class)
public @interface IfPropertiesDefined {

  /** The properties names to check. */
  String[] value();

  /** The bean holding the properties. If not provided, properties will be searched in the {@link Environment}. */
  String propertiesBeanName() default "";

}
