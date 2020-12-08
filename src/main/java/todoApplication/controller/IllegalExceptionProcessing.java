package todoApplication.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target()
@Retention(RetentionPolicy.RUNTIME)
@interface IllegalExceptionProcessing {
}
