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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Match;
import io.github.raghultech.openloom.model.Validate;

/**
 * Provides utilities for performing advanced regular expression (regex) searches in text files.
 * <p>
 * This class supports both full-file and line-range-based regex searches, with optional
 * case-insensitive matching. Each result is encapsulated in a {@link Match} object,
 * containing detailed information such as the line number, start and end positions,
 * and the full line text.
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * File file = new File("example.txt");
 * 
 * openloom finder = new openloom();
 * List&lt;Match&gt; matches = finder.find().findLineRegex(file, "\\bhello\\b", true);
 * 
 * for (Match match : matches) {
 *     System.out.printf("Found match at line %d [%d,%d]%n",
 *         match.getLineNumber(), match.getStartIndex(), match.getEndIndex());
 * }
 * </pre>
 *
 * @see io.github.raghultech.openloom.model.Match
 * @see io.github.raghultech.openloom.model.Validate
 * @see io.github.raghultech.openloom.exception.OpenLoomFileException
 */
public class FindLineRegex {


    /**
     * Searches the entire file for matches of the given regular expression.
     *
     * @param file             the file to search
     * @param regex            the regular expression to match
     * @param caseInsensitive  {@code true} to perform a case-insensitive search; {@code false} for case-sensitive
     * @param charset          the charset used to read the file
     * @return a list of {@link Match} objects representing all regex matches found
     * @throws OpenLoomFileException if an I/O error occurs during reading or validation fails
     */
    protected List<Match> findLineRegex(File file, String regex, boolean caseInsensitive, Charset charset) {
        return findLineRegexInternal(file, regex, caseInsensitive, charset);
    }
    
    
    /**
     * Searches the entire file for matches of the given regular expression using
     * case-sensitive matching by default.
     *
     * @param file     the file to search
     * @param regex    the regular expression to match
     * @param charset  the charset used to read the file
     * @return a list of {@link Match} objects representing all regex matches found
     * @throws OpenLoomFileException if an I/O error occurs during reading or validation fails
     */
    protected List<Match> findLineRegex(File file, String regex, Charset charset) {
        return findLineRegexInternal(file, regex, false, charset);
    }

    /**
     * Searches for regex matches only within a specific line range (inclusive).
     * <p>
     * This method is optimized for large files, allowing partial search by restricting
     * the processing to a specific section of the file.
     * </p>
     *
     * @param file             the file to search
     * @param regex            the regular expression to match
     * @param startLine        the starting line number (1-based)
     * @param endLine          the ending line number (inclusive)
     * @param caseInsensitive  {@code true} to perform a case-insensitive search; {@code false} for case-sensitive
     * @param charset          the charset used to read the file
     * @return a list of {@link Match} objects found within the specified line range
     * @throws OpenLoomFileException if the range or file is invalid, or an I/O error occurs
     */
    protected List<Match> findLineRegexInRange(File file, String regex, int startLine, int endLine,
                                               boolean caseInsensitive, Charset charset) {
        return findLineRegexInRangeInternal(file, regex, startLine, endLine, caseInsensitive, charset);
    }
    
    
    /**
     * Searches for regex matches only within a specific line range (inclusive),
     * using case-sensitive matching by default.
     *
     * @param file        the file to search
     * @param regex       the regular expression to match
     * @param startLine   the starting line number (1-based)
     * @param endLine     the ending line number (inclusive)
     * @param charset     the charset used to read the file
     * @return a list of {@link Match} objects found within the specified line range
     * @throws OpenLoomFileException if invalid parameters or I/O errors occur
     */
    protected List<Match> findLineRegexInRange(File file, String regex, int startLine, int endLine,
    											Charset charset) {
                return findLineRegexInRangeInternal(file, regex, startLine, endLine, false, charset);
       }

    // 🔹 Private helpers

    private List<Match> findLineRegexInternal(File file, String regex, boolean caseInsensitive, Charset charset) {
        return findLineRegexInRangeInternal(file, regex, 1, Integer.MAX_VALUE, caseInsensitive, charset);
    }

    private List<Match> findLineRegexInRangeInternal(File file, String regex, int startLine, int endLine,
                                                     boolean caseInsensitive, Charset charset) {
        Validate.validate(file, regex);

        if (startLine < 1 || endLine < startLine) {
            throw new IllegalArgumentException("Invalid start/end line numbers: start=" + startLine + ", end=" + endLine);
        }

        List<Match> matches = new ArrayList<>();
        int flags = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
        Pattern pattern = Pattern.compile(regex, flags);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            String line;
            int currentLine = 1;

            while ((line = reader.readLine()) != null) {
                if (currentLine > endLine) break;

                if (currentLine >= startLine) {
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        matches.add(new Match(
                                currentLine,
                                matcher.start(),
                                matcher.end(),
                                line
                        ));
                    }
                }
                currentLine++;
            }

        } catch (IOException e) {
            throw new OpenLoomFileException("Error searching file with regex: " + file, e);
        }

        return matches;
    }
}

