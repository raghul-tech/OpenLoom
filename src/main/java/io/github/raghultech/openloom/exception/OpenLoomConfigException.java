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


/**
 * Exception thrown for configuration-related errors in OpenLoom.
 * 
 * <p>This exception indicates problems with library configuration,
 * such as invalid character sets, illegal parameter values, or
 * unsupported settings.</p>
 */
@SuppressWarnings("serial")
public class OpenLoomConfigException extends RuntimeException {
    
    /**
     * Constructs a new configuration exception with the specified detail message.
     *
     * @param message the detail message explaining the configuration error
     */
    public OpenLoomConfigException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new configuration exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the configuration error
     * @param cause the underlying cause of this exception
     */
    public OpenLoomConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
