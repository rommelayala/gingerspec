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

package com.privalia.qa.utils;

import org.openqa.selenium.WebElement;

import java.util.List;

public final class PreviousWebElements {

    private List<WebElement> previousWebElements;

    public PreviousWebElements(List<WebElement> previousWebElements) {
        this.previousWebElements = previousWebElements;
    }

    public List<WebElement> getPreviousWebElements() {
        return previousWebElements;
    }

    public void setPreviousWebElements(List<WebElement> previousWebElements) {
        this.previousWebElements = previousWebElements;
    }

}