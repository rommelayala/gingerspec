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
package com.privalia.qa.ATests;


import com.privalia.qa.cucumber.testng.CucumberRunner;
import com.privalia.qa.utils.BaseGTest;
import cucumber.api.CucumberOptions;
import org.testng.annotations.Test;

@CucumberOptions(format = "json:target/cucumber.json", features = {"src/test/resources/features/backgroundTag1.feature",
                             "src/test/resources/features/backgroundTag2.feature",
                             "src/test/resources/features/backgroundTag3.feature"},
        glue = "classpath:com/privalia/qa/specs/*")
public class BackgroundTagIT extends BaseGTest {
    @Test
    public void backgroundTagIt() throws Exception {
        new CucumberRunner(this.getClass()).runCukes();
    }
}
