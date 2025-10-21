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


package io.github.raghultech.openloom.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class Utils {

  /*  protected static Path getTempPath(Path target) {
        Path parent = target.getParent();
        if (parent == null) {
            parent = Paths.get(System.getProperty("java.io.tmpdir"));
        }
        String tmpName = target.getFileName() + "." + UUID.randomUUID() + ".tmp";
        return parent.resolve(tmpName);
    }*/
	
	 public static Path createTemp(File file, String operationName) {
		 String suffix = operationName != null ? "." + operationName + ".tmp" : ".tmp";
	        try {
	            return Files.createTempFile(file.getParentFile().toPath(), file.getName(), suffix);
	        } catch (IOException e) {
	            return Paths.get(file.getParent(), file.getName() + "." + UUID.randomUUID() + suffix);
	        }
	    }
	
    
    public static Path resolveFinalTarget(Path source, Path target) {
        if (Files.isDirectory(target)) {
            return target.resolve(source.getFileName());
        } 
        String targetStr = target.toString();
        if (targetStr.endsWith("/") || targetStr.endsWith("\\")) {
            return target.resolve(source.getFileName());
        }
        
        return target;
    }
	
}

