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


package io.github.raghultech.openloom.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import io.github.raghultech.openloom.exception.OpenLoomConfigException;

/**
 * Configuration class for file I/O operations in OpenLoom.
 * 
 * <p>This class manages character encoding settings for reading and writing files.
 * It provides both type-safe {@link Charset} and flexible string-based charset configuration
 * with comprehensive validation.</p>
 * 
 * <p><b>Usage Examples:</b></p>
 * <pre>
 * {@code
 * // Type-safe approach (recommended)
 * config.setCharset(StandardCharsets.UTF_8);
 * 
 * // String-based approach (for configuration files)
 * config.setCharset("ISO-8859-1");
 * 
 * // Get current charset
 * Charset current = config.getCharset();
 * String charsetName = config.getCharsetName();
 * }
 * </pre>
 * 
 * @author Raghul-tech
 * @version 1.0.0
 */
public class FileIOConfig {
    
    /**
     * Default character encoding: UTF-8.
     * UTF-8 is recommended for its compatibility with Unicode and widespread support.
     */
    private Charset charset = StandardCharsets.UTF_8;
    
      
    public void setCharset(Charset charset) {
        validateCharsetObject(charset);
        this.charset = charset;
    }
   
    public Charset getCharset() {
        return charset;
    }
    
    public void setCharset(String charsetName) {
        validateCharsetName(charsetName);
        
        try {
            Charset resolvedCharset = Charset.forName(charsetName.trim());
            setCharset(resolvedCharset); // Reuse primary validation
        } catch (UnsupportedCharsetException e) {
            throw new OpenLoomConfigException(
                createUnsupportedCharsetMessage(charsetName), e);
        }
    }
    
    public String getCharsetName() {
        return charset.name();
    }
    
    private void validateCharsetObject(Charset charset) {
        if (charset == null) {
            throw new OpenLoomConfigException(
                "Charset cannot be null. " +
                "Use StandardCharsets.UTF_8 for default encoding.");
        }
        
        // Verify the charset is actually available on this system
        if (!Charset.availableCharsets().containsKey(charset.name())) {
            throw new OpenLoomConfigException(
                "Charset '" + charset.name() + "' is not available on this system. " +
                "Available charsets: " + getAvailableCharsetNames());
        }
    }
    
    /**
     * Validates a charset name string before processing.
     * 
     * @param charsetName the charset name to validate
     * @throws OpenLoomConfigException if validation fails
     */
    private void validateCharsetName(String charsetName) {
        if (charsetName == null) {
            throw new OpenLoomConfigException(
                "Charset name cannot be null. " +
                "Use 'UTF-8' for default encoding.");
        }
        
        String trimmedName = charsetName.trim();
        if (trimmedName.isEmpty()) {
            throw new OpenLoomConfigException(
                "Charset name cannot be empty or whitespace. " +
                "Use 'UTF-8' for default encoding.");
        }
        
        // Basic sanity check for charset name format
        if (!trimmedName.matches("[a-zA-Z0-9\\-_.]+")) {
            throw new OpenLoomConfigException(
                "Invalid charset name format: '" + charsetName + "'. " +
                "Charset names must contain only letters, numbers, hyphens, and underscores.");
        }
    }
    
    // ========================================================================
    // UTILITY METHODS
    // ========================================================================
    
    /**
     * Creates a user-friendly error message for unsupported charsets.
     */
    private String createUnsupportedCharsetMessage(String requestedCharset) {
        return String.format(
            "Unsupported charset: '%s'.%n" +
            "Available charsets on this system:%n" +
            "- %s%n" +
            "Recommended charsets: UTF-8, ISO-8859-1, US-ASCII",
            requestedCharset,
            String.join(", ", getAvailableCharsetNames())
        );
    }
    
    /**
     * Returns a list of available charset names on the current system.
     */
    private String getAvailableCharsetNames() {
        return String.join(", ", Charset.availableCharsets().keySet());
    }
    
    /**
     * Returns a string representation of the configuration.
     * 
     * @return a string containing the current charset setting
     */
    @Override
    public String toString() {
        return  charset.name();
    }
}
