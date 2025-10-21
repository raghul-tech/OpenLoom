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

/**
 * Represents a text match with its position in the file.
 */

public  class Match {
    private final int lineNumber;
    private final int startIndex;
    private final int endIndex;
    private final String lineContent;

    public Match(int lineNumber, int startIndex, int endIndex, String lineContent) {
        this.lineNumber = lineNumber;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.lineContent = lineContent;
    }

    public int getLineNumber() { return lineNumber; }
    public int getStartIndex() { return startIndex; }
    public int getEndIndex() { return endIndex; }
    public String getLineContent() { return lineContent; }

    @Override
    public String toString() {
        return lineNumber + ", start=" + startIndex +
                ", end=" + endIndex + ", text='" + lineContent ;
    }
}

