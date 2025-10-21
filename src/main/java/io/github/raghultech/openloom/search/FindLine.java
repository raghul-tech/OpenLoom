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

package io.github.raghultech.openloom.search;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Match;
import io.github.raghultech.openloom.model.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Provides advanced utilities for searching specific text patterns within files.
 * <p>
 * Supports both case-sensitive and case-insensitive text searches with optional
 * line range restrictions. Each result contains rich positional metadata — including
 * the line number, character start and end indices, and the full line text —
 * enabling easy integration with syntax highlighters, editors, and search viewers.
 * </p>
 *
 * <pre>
 * Example usage:
 * OpenLoom loom = new OpenLoom();
 * List&lt;Match&gt; results = loom.findLines(Paths.get("file.txt"), "hello", StandardCharsets.UTF_8, false);
 * for (Match match : results) {
 *     System.out.println("Found at line " + match.getLineNumber() +
 *         " [" + match.getStartIndex() + "," + match.getEndIndex() + "]");
 * }
 * </pre>
 */
public class FindLine {

	  /**
     * Searches the entire file for all occurrences of the specified text.
     *
     * @param file          the path to the file to search
     * @param searchText    the target text to find
     * @param charset       the character encoding of the file
     * @param caseSensitive if {@code true}, performs a case-sensitive search;
     *                      otherwise, ignores case differences
     * @return a list of {@link Match} objects representing all occurrences found
     * @throws OpenLoomFileException if the file cannot be read or validation fails
     */
	protected List<Match> findLine(Path file, String searchText, Charset charset, boolean caseSensitive) {
	    return findLineInRange(file, searchText, charset, 1, Integer.MAX_VALUE, caseSensitive);
	}

    /**
     * Searches the entire file for occurrences of the specified text using
     * case-insensitive matching by default.
     *
     * @param file        the path to the file to search
     * @param searchText  the target text to find
     * @param charset     the character encoding of the file
     * @return a list of {@link Match} objects representing all matches found
     * @throws OpenLoomFileException if the file cannot be read or validation fails
     */
	protected List<Match> findLine(Path file, String searchText, Charset charset) {
	    return findLineInRange(file, searchText, charset, 1, Integer.MAX_VALUE, false);
	}

  
    /**
     * Searches for the specified text within a defined line range.
     * <p>
     * This method allows efficient partial searching within large files
     * by limiting reads to specific lines.
     * </p>
     *
     * @param file          the path to the file to search
     * @param searchText    the target text to find
     * @param charset       the character encoding of the file
     * @param startLine     the first line number to include (1-based)
     * @param endLine       the last line number to include (inclusive)
     * @param caseSensitive if {@code true}, performs a case-sensitive search;
     *                      otherwise, ignores case differences
     * @return a list of {@link Match} objects found within the specified line range
     * @throws OpenLoomFileException if invalid parameters are provided
     *                               or an I/O error occurs
     */
    protected List<Match> findLineInRange(Path file, String searchText, Charset charset,
                                          int startLine, int endLine, boolean caseSensitive) {
        Validate.validate(file.toFile(), searchText);

        if (startLine < 1 || endLine < startLine) {
            throw new OpenLoomFileException("Invalid line range: " + startLine + " - " + endLine);
        }

        List<Match> matches = new ArrayList<>();

        Pattern pattern = caseSensitive
                ? Pattern.compile(Pattern.quote(searchText))
                : Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);

        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (lineNumber > endLine) break; // stop reading once out of range
                if (lineNumber >= startLine) {
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        matches.add(new Match(lineNumber, matcher.start(), matcher.end(), line));
                    }
                }
                lineNumber++;
            }

        } catch (IOException e) {
            throw new OpenLoomFileException("Error reading file: " + file, e);
        }

        return matches;
    }

 
    /**
     * Searches for the specified text within a defined line range using
     * case-insensitive matching by default.
     *
     * @param file       the path to the file to search
     * @param searchText the target text to find
     * @param charset    the character encoding of the file
     * @param startLine  the first line number to include (1-based)
     * @param endLine    the last line number to include (inclusive)
     * @return a list of {@link Match} objects found within the specified range
     * @throws OpenLoomFileException if invalid parameters are provided
     *                               or an I/O error occurs
     */
    protected List<Match> findLineInRange(Path file, String searchText, Charset charset,
                                          int startLine, int endLine) {
        return findLineInRange(file, searchText, charset, startLine, endLine, false);
    }
}

