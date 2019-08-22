/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.Util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author awittig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BuilderToVerifyNotNull {
    
}
