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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.github.raghultech.openloom.config.FileIOConfig;
import io.github.raghultech.openloom.model.Match;

/**
 * The {@code SearchManager} provides a unified interface for searching,
 * replacing, inserting, modifying, and deleting text and lines in files.
 * <p>
 * This manager is part of the OpenLoom {@code search()} subsystem and acts
 * as a high-level API that delegates operations to internal managers such as
 * {@link ReplaceText}, {@link ReplaceLine}, {@link InsertLine}, {@link ModifyLine},
 * {@link DeleteLine}, {@link FindLine}, and {@link FindLineRegex}.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *     <li>Find lines by keyword (case-sensitive or insensitive).</li>
 *     <li>Find lines using regular expressions, including range-limited searches.</li>
 *     <li>Replace text in a specific line or all lines (normal or safe atomic modes).</li>
 *     <li>Insert, modify, or delete lines individually or in bulk (normal or safe atomic modes).</li>
 *     <li>Supports both {@link File} and {@link Path} inputs with configurable {@link Charset}.</li>
 * </ul>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
 * SearchManager search = loom.search();
 *
 * // Find lines containing a keyword
 * List<Match> results = search.findLine(file, "TODO");
 *
 * // Replace a word in a specific line
 * search.replaceText(file, 5, "oldWord", "newWord");
 *
 * // Insert a new line at a specific position
 * search.insertLine(file, 3, "New line content");
 *
 * // Delete multiple lines safely
 * search.deleteLinesSafe(file, List.of(2, 5, 7));
 * }</pre>
 *
 * <p><b>Safe methods</b> perform atomic operations using temporary files to
 * ensure that the original file remains unmodified in case of errors.</p>
 *
 * @see ReplaceText
 * @see ReplaceLine
 * @see InsertLine
 * @see ModifyLine
 * @see DeleteLine
 * @see FindLine
 * @see FindLineRegex
 * @see io.github.raghultech.openloom.OpenLoom#search()
 */
public class SearchManager {
	
	private final FileIOConfig config;
	
	 private final FindLine findLine = new FindLine();
	 private final FindLineRegex Lineregex = new FindLineRegex();
	 private final ReplaceLine replace = new ReplaceLine();
	 private final ReplaceText replaceText = new ReplaceText();
	 private final InsertLine insert = new InsertLine();
	 private final ModifyLine modify = new ModifyLine();
	 private final DeleteLine delete = new DeleteLine();

	 
	 public SearchManager() {
		 this.config = new FileIOConfig(); 
	 }
	 
		public SearchManager(FileIOConfig config) {
			this.config = config != null ? config : new FileIOConfig();
		}
		
	 
	 public SearchManager(Charset charset) {
		 this.config = new FileIOConfig();
	        this.config.setCharset(charset);
	 }
	
	  public Charset getCharset() { return config.getCharset(); }
	    public void setCharset(Charset charset) {	config.setCharset(charset);}
	
	    
	    public SearchManager(String charset) {
	    	 this.config = new FileIOConfig();
		        this.config.setCharset(charset);
	    }
	    
	    public String getCharsetName() { return config.getCharsetName(); }
	    public void setCharset(String charset) {	config.setCharset(charset);}
	
	// =============================
	// 🔍 SEARCH OPERATIONS 
	// =============================

		
		public List<Match> findLine(Path file, String keyword) {
		    return findLine.findLine(file, keyword, config.getCharset()); // Both number+content
		}
		public List<Match> findLine(File file, String keyword) {
		    return findLine.findLine(file.toPath(), keyword, config.getCharset()); // Both number+content
		}
		public List<Match> findLine(Path file, String keyword,boolean caseSensitive ) {
		    return findLine.findLine(file, keyword, config.getCharset(), caseSensitive); // Both number+content
		}
		public List<Match> findLine(File file, String keyword, boolean caseSensitive) {
		    return findLine.findLine(file.toPath(), keyword, config.getCharset(),caseSensitive); // Both number+content
		}
	    
	 // =============================
		// 🔍 SEARCH OPERATIONS IN RANGE
		// =============================

			
			public List<Match> findLineInRange(Path file, String keyword, int startLine, int endLine) {
			    return findLine.findLineInRange(file, keyword, config.getCharset(), startLine, endLine); // Both number+content
			}
			public List<Match> findLineInRange(File file, String keyword, int startLine, int endLine) {
			    return findLine.findLineInRange(file.toPath(), keyword, config.getCharset(),  startLine,  endLine); // Both number+content
			}
			public List<Match> findLineInRange(Path file, String keyword, int startLine, int endLine,boolean caseSensitive ) {
			    return findLine.findLineInRange(file, keyword, config.getCharset(),  startLine,  endLine, caseSensitive); // Both number+content
			}
			public List<Match> findLineInRange(File file, String keyword,  int startLine, int endLine,boolean caseSensitive) {
			    return findLine.findLineInRange(file.toPath(), keyword, config.getCharset(),  startLine, endLine,caseSensitive); // Both number+content
			}
	    
	
	
	// =============================
	// 🔍 REGEX SEARCH OPERATIONS (numbers/content/both)
	// =============================

	// --- Entire file ---
	public List<Match> findLineRegex(File file, String regex, boolean caseInsensitive) {
	    return Lineregex.findLineRegex(file, regex, caseInsensitive, config.getCharset()); // Both number+content
	}
     public List<Match> findLineRegex(Path path, String regex, boolean caseInsensitive) {	    
		    return Lineregex.findLineRegex(path.toFile(), regex, caseInsensitive, config.getCharset());
		}
 	public List<Match> findLineRegex(File file, String regex) {
	    return Lineregex.findLineRegex(file, regex, config.getCharset()); // Both number+content
	}
     public List<Match> findLineRegex(Path path, String regex) {	    
		    return Lineregex.findLineRegex(path.toFile(), regex, config.getCharset());
		}


	// =============================
	// 🔍 REGEX SEARCH IN RANGE (numbers/content/both)
	// =============================

	public List<Match> findLineRegexInRange(File file, String regex, int startLine, int endLine, boolean caseInsensitive) {    
	    return Lineregex.findLineRegexInRange(file, regex, startLine, endLine, caseInsensitive, config.getCharset()); // Both
	}
	public List<Match> findLineRegexInRange(Path path, String regex, int startLine, int endLine, boolean caseInsensitive) {
	    return Lineregex.findLineRegexInRange(path.toFile(), regex, startLine, endLine, caseInsensitive,config.getCharset());
	}
	public List<Match> findLineRegexInRange(File file, String regex, int startLine, int endLine) {    
	    return Lineregex.findLineRegexInRange(file, regex, startLine, endLine, config.getCharset()); // Both
	}
	public List<Match> findLineRegexInRange(Path path, String regex, int startLine, int endLine) {
	    return Lineregex.findLineRegexInRange(path.toFile(), regex, startLine, endLine,config.getCharset());
	}
	
	
	/**
	 * this is replace sync
	 * @param file
	 * @param line
	 * @param content
	 */
	public void replaceLine(File file, int line, String content) { replace.replaceLine(file, line, content, config.getCharset()); }
	public void replaceLineSafe(File file, int line, String content) { replace.replaceLineSafe(file, line, content, config.getCharset()); }
	public void replaceLines(File file,  Map<Integer, String> replacements) { replace.replaceLines(file, replacements, config.getCharset()); }
	public void replaceLinesSafe(File file,  Map<Integer, String> replacements) { replace.replaceLinesSafe(file, replacements, config.getCharset()); }
	
	public void replaceLine(Path path, int line, String content) { replace.replaceLine(path.toFile(), line, content, config.getCharset()); }
	public void replaceLineSafe(Path path, int line, String content) { replace.replaceLineSafe(path.toFile(), line, content, config.getCharset()); }
	public void replaceLines(Path path,  Map<Integer, String> replacements) { replace.replaceLines(path.toFile(), replacements, config.getCharset()); }
	public void replaceLinesSafe(Path path,  Map<Integer, String> replacements) { replace.replaceLinesSafe(path.toFile(), replacements, config.getCharset()); }
	
	/**
	 * Insert sync
	 * @param file
	 * @param line
	 * @param content
	 */
	
	public void insertLine(File file, int line, String content) { insert.insertLine(file, line, content, config.getCharset()); }
	public void insertLineSafe(File file, int line, String content) { insert.insertLineSafe(file, line, content, config.getCharset()); }
	public void insertLines(File file,  Map<Integer, String> inserts) { insert.insertLines(file, inserts, config.getCharset()); }
	public void insertLinesSafe(File file,  Map<Integer, String> inserts) { insert.insertLinesSafe(file, inserts, config.getCharset()); }
	
	public void insertLine(Path path, int line, String content) { insert.insertLine(path.toFile(), line, content, config.getCharset()); }
	public void insertLineSafe(Path path, int line, String content) { insert.insertLineSafe(path.toFile(), line, content, config.getCharset()); }
	public void insertLines(Path path,  Map<Integer, String> inserts) { insert.insertLines(path.toFile(), inserts, config.getCharset()); }
	public void insertLinesSafe(Path path,  Map<Integer, String> inserts) { insert.insertLinesSafe(path.toFile(), inserts, config.getCharset()); }
	
	/**
	 * this is modify sync 
	 * @param file
	 * @param line
	 * @param modifier
	 */
	
	public void modifyLine(File file, int line, Function<String, String> modifier) { modify.modifyLine(file, line, modifier, config.getCharset()); }
	public void modifyLineSafe(File file, int line, Function<String, String> modifier) { modify.modifyLineSafe(file, line, modifier, config.getCharset()); }
	public void modifyLines(File file,  Map<Integer, Function<String, String>> modifiers) { modify.modifyLines(file, modifiers, config.getCharset()); }
	public void modifyLinesSafe(File file,  Map<Integer, Function<String, String>> modifiers) { modify.modifyLinesSafe(file,  modifiers, config.getCharset()); }
    
	public void modifyLine(Path path, int line, Function<String, String> modifier) { modify.modifyLine(path.toFile(), line, modifier, config.getCharset()); }
	public void modifyLineSafe(Path path, int line, Function<String, String> modifier) { modify.modifyLineSafe(path.toFile(), line, modifier, config.getCharset()); }
	public void modifyLines(Path path,  Map<Integer, Function<String, String>> modifiers) { modify.modifyLines(path.toFile(), modifiers, config.getCharset()); }
	public void modifyLinesSafe(Path path,  Map<Integer, Function<String, String>> modifiers) { modify.modifyLinesSafe(path.toFile(),  modifiers, config.getCharset()); }
    

	/**
	 * delete sync
	 * @param file
	 * @param line
	 */
	
	
	public void deleteLine(File file, int line) { delete.deleteLine(file, line, config.getCharset()); }
	public void deleteLineSafe(File file, int line) { delete.deleteLineSafe(file, line, config.getCharset()); }
	public void deleteLines(File file, Collection<Integer> line) { delete.deleteLines(file, line, config.getCharset()); }
	public void deleteLinesSafe(File file, Collection<Integer> line) { delete.deleteLinesSafe(file, line, config.getCharset()); }
	
	
	public void deleteLine(Path path, int line) { delete.deleteLine(path.toFile(), line, config.getCharset()); }
	public void deleteLineSafe(Path path, int line) { delete.deleteLineSafe(path.toFile(), line, config.getCharset()); }
	public void deleteLines(Path path, Collection<Integer> line) { delete.deleteLines(path.toFile(), line, config.getCharset()); }
	public void deleteLinesSafe(Path path, Collection<Integer> line) { delete.deleteLinesSafe(path.toFile(), line, config.getCharset()); }
	

	
	/**
	 * replace text 
	 */
	public void replaceText(File file,int line,  String target, String content) { replaceText.replaceText(file,line, target, content, config.getCharset()); }
	public void replaceTextSafe(File file,int line,  String target, String content) { replaceText.replaceTextSafe(file,line, target, content, config.getCharset()); }
	public void replaceText(Path path, int line, String target, String content) { replaceText.replaceText(path.toFile(),line, target, content, config.getCharset()); }
	public void replaceTextSafe(Path path,int line,  String target, String content) { replaceText.replaceTextSafe(path.toFile(),line, target, content, config.getCharset()); }
	
	public void replaceTextAll(File file,  String target, String content) { replaceText.replaceTextAll(file, target, content, config.getCharset()); }
	public void replaceTextAllSafe(File file,  String target, String content) { replaceText.replaceTextAllSafe(file, target, content, config.getCharset()); }
	public void replaceTextAll(Path path,  String target, String content) { replaceText.replaceTextAll(path.toFile(), target, content, config.getCharset()); }
	public void replaceTextAllSafe(Path path,  String target, String content) { replaceText.replaceTextAllSafe(path.toFile(), target, content, config.getCharset()); }

	
	
	
}

