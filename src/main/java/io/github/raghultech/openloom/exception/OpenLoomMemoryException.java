/*
 * Copyright 2025 Raghul-tech
 * https://github.com/raghul-tech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.raghultech.openloom.exception;

@SuppressWarnings("serial")
public class OpenLoomMemoryException extends RuntimeException {
    public OpenLoomMemoryException(String message, Throwable cause) {
        super(message, cause);
    }
    public  OpenLoomMemoryException() {
    super("The file is too large to fit in memory. ");	
    }
    public OpenLoomMemoryException(String message) {
        super(message);
    }
}
