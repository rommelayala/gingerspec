/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.privalia.qa.aspects;

import com.privalia.qa.assertions.SeleniumAssert;
import com.privalia.qa.specs.BaseGSpec;
import com.privalia.qa.specs.CommonG;
import com.privalia.qa.specs.ThenGSpec;
import com.privalia.qa.utils.PreviousWebElements;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;

@Aspect
public class SeleniumAspect extends BaseGSpec {

    private WebDriver webdriver;

    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getCanonicalName());

    @Pointcut("call(* com.privalia.qa.assertions.SeleniumAssert.*(..))"
            + " || call(* org.openqa.selenium.*.click(..))"
            + " || call(* org.openqa.selenium.*.findElement(..))"
            + " || call(* org.openqa.selenium.support.ui.FluentWait.*(..))") //To detect exceptions thrown by FluentWait
    protected void exceptionCallPointcut() {
    }


    /**
     * If an exception has thrown by selenium, this methods save a screen
     * capture.
     *
     * @param pjp ProceedingJoinPoint
     * @return Object object
     * @throws Throwable exception
     */
    @Around(value = "exceptionCallPointcut()")
    public Object aroundExceptionCalls(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal = null;
        try {
            retVal = pjp.proceed();
            return retVal;
        } catch (Throwable ex) {
            WebDriver driver = null;
            if (ex instanceof WebDriverException) {
                logger.info("Got a selenium exception");

                /**
                 * This captures extracts the driver instance when a
                 * TimeoutException is thrown by FluentWait
                 */
                if (ex instanceof TimeoutException) {
                    if (pjp.getThis() instanceof FluentWait) {
                        FluentWait fw = (FluentWait) pjp.getThis();
                        Field f = fw.getClass().getDeclaredField("input");
                        f.setAccessible(true);
                        driver = (WebDriver) f.get(fw);
                    } else {
                        throw ex;
                    }

                } else {
                    if (!(pjp.getThis() instanceof WebDriver)) {
                        throw ex;
                    }
                    driver = (WebDriver) pjp.getThis();
                }
            } else if ((pjp.getTarget() instanceof SeleniumAssert)
                    && (ex instanceof AssertionError)) {
                logger.info("Got a SeleniumAssert response");
                SeleniumAssert as = (SeleniumAssert) pjp.getTarget();
                Class<?> c = as.getClass().getSuperclass();
                Field actual = c.getDeclaredField("actual");
                actual.setAccessible(true);
                Object realActual = actual.get(as);

                if (realActual instanceof WebDriver) {
                    driver = (WebDriver) actual.get(as);
                } else if (realActual instanceof ArrayList) {
                    if (((ArrayList) realActual).get(0) instanceof RemoteWebElement) {
                        driver = ((RemoteWebElement) ((ArrayList) realActual).get(0)).getWrappedDriver();
                    }
                } else if ((realActual instanceof PreviousWebElements) ||
                        (realActual instanceof Boolean) ||
                        (realActual instanceof String) ||
                        (realActual == null)) {
                    driver = ((CommonG) ((SeleniumAssert) pjp.getTarget()).getCommonspec()).getDriver();
                } else if (realActual instanceof RemoteWebElement) {
                    driver = ((RemoteWebElement) actual.get(as)).getWrappedDriver();
                }
            }
            if (driver != null) {
                logger.info("Trying to capture screenshots...");
                String suffix = "assert";
                CommonG common = null;
                if ((pjp.getThis() instanceof ThenGSpec) && (((ThenGSpec) pjp.getThis()).getCommonSpec() != null)) {
                    common = ((ThenGSpec) pjp.getThis()).getCommonSpec();
                } else if ((pjp.getTarget() instanceof SeleniumAssert) && ((SeleniumAssert) pjp.getTarget()).getCommonspec() != null) {
                    common = ((CommonG) ((SeleniumAssert) pjp.getTarget()).getCommonspec());
                } else {
                    common = new CommonG();
                    common.setDriver((RemoteWebDriver) driver);
                    suffix = "exception";
                }
                common.captureEvidence(driver, "framehtmlSource", suffix);
                common.captureEvidence(driver, "htmlSource", suffix);
                common.captureEvidence(driver, "screenCapture", suffix);
                logger.info("Screenshots are available at target/executions");
            } else {
                logger.info("Got no Selenium driver to capture a screen");
            }
            throw ex;
        }
    }
}