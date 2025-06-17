package com.raghultech.openloom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.raghultech.openloom.exception.OpenLoomFileException;
import com.raghultech.openloom.exception.OpenLoomMemoryException;


public class OpenLoom {
	 private Charset charset;

	 
	 public OpenLoom() {
	      //  this(StandardCharsets.UTF_8); // Default charset
		 this.charset = StandardCharsets.UTF_8;
	    }

	    public OpenLoom(Charset charset) {
	        this.charset = charset;
	    }
	    
	    public Charset getCharset() { return charset; }
	    public void setCharset(Charset charset) { this.charset = charset; }

	
	 public   StringBuilder read(File file) {
		    return getContent(file);
		}
	 
	 public   StringBuilder read(Path path) {
		 return getContent(path.toFile());
		}
	 
	 public CompletableFuture<StringBuilder> readAsync(File file) {
		    return CompletableFuture.supplyAsync(() -> read(file));
		}

	 public CompletableFuture<StringBuilder> readAsync(Path path) {
		    return CompletableFuture.supplyAsync(() -> read(path));
		}
	 public CompletableFuture<Void> streamLinesAsync(File file, Consumer<String> lineConsumer) {
		    return CompletableFuture.runAsync(() -> streamLines(file, lineConsumer));
		}

	 public CompletableFuture<Void> streamLinesAsync(Path path, Consumer<String> lineConsumer) {
		    return CompletableFuture.runAsync(() -> streamLines(path, lineConsumer));
		}

	 
	 public void streamLines(File file, Consumer<String> lineConsumer) {
 	    if (file == null || !file.exists()) {
 	        throw new OpenLoomFileException("File not found or invalid: " + file);
 	    }

 	    try (BufferedReader reader = new BufferedReader(
 	            new InputStreamReader(new FileInputStream(file), charset))) {

 	        String line;
 	        while ((line = reader.readLine()) != null) {
 	            lineConsumer.accept(line);
 	        }

 	    } catch (IOException e) {
 	        throw new OpenLoomFileException("Failed to stream lines from file: " + file, e);
 	    }
 	}
  
  public void streamLines(Path path, Consumer<String> lineConsumer) {
 	    streamLines(path.toFile(), lineConsumer);
 	}

	 
	
	 private    StringBuilder getContent(File openFile) {
         if (openFile == null || !openFile.exists()) {
         	  throw new OpenLoomFileException("File not found or invalid: " + openFile);
         }
     double fileSizeInMB = openFile.length() / (1024.0 * 1024.0); // Convert bytes to MB
   
                 if (fileSizeInMB < 20.0) {  
                  return   loadSmallFile(openFile);
                 }  else if (fileSizeInMB < 60.0) {  
               return  	 loadLargeFile(openFile); 	  
                 }else if (fileSizeInMB <90.0 ){
                 return	loadVeryLargeFile(openFile);
                 	
                 }else {
                 return	loadBigFile(openFile);
                 }  
     }
     

     
     private   StringBuilder loadSmallFile(File file) throws OpenLoomMemoryException {
         try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(new FileInputStream(file), charset))) {

             // Use an initial capacity if you expect the file to be large-ish
             StringBuilder content = new StringBuilder((int) file.length());

             String line;
             while ((line = reader.readLine()) != null) {
                 content.append(line).append("\n");
             }

             return content;

         } catch (IOException e) {
         	 throw new  OpenLoomFileException("Failed to read content from file: " + file, e);
         }
     }

 
     private   StringBuilder loadLargeFile(File file) throws OpenLoomMemoryException {
    //     Charset charset = StandardCharsets.UTF_8;
         CharsetDecoder decoder = charset.newDecoder();
         StringBuilder content = new StringBuilder((int) Math.min(file.length(), Integer.MAX_VALUE)); // Safe initial capacity

         try (RandomAccessFile raf = new RandomAccessFile(file, "r");
              FileChannel fileChannel = raf.getChannel()) {

             ByteBuffer buffer = ByteBuffer.allocate(65536); // 64KB buffer
             while (fileChannel.read(buffer) > 0) {
                 buffer.flip();
                 CharBuffer charBuffer = decoder.decode(buffer);
                 content.append(charBuffer);
                 buffer.clear();
             }

         } catch (IOException e) {
         	 throw new  OpenLoomFileException("Failed to read content from file: " + file, e);
         }

         return content;
     }


     private   StringBuilder loadVeryLargeFile(File file) throws  OpenLoomMemoryException {
         final int BUFFER_SIZE = 32 * 1024; // 32KB
         StringBuilder content = new StringBuilder((int) Math.min(file.length(), Integer.MAX_VALUE));

         try (FileInputStream fis = new FileInputStream(file);
              InputStreamReader isr = new InputStreamReader(fis, charset);
              BufferedReader reader = new BufferedReader(isr, BUFFER_SIZE)) {

             char[] buffer = new char[BUFFER_SIZE];
             int charsRead;

             while ((charsRead = reader.read(buffer)) != -1) {
                 content.append(buffer, 0, charsRead);
             }

         } catch (IOException e) {
         	 throw new  OpenLoomFileException("Failed to read content from file: " + file, e);
         }

         return content;
     }


     private   StringBuilder loadBigFile(File file) throws  OpenLoomMemoryException {
         try (RandomAccessFile raf = new RandomAccessFile(file, "r");
              FileChannel fileChannel = raf.getChannel()) {

             long fileSize = fileChannel.size();
             long maxLoadSize = Math.min(fileSize, Integer.MAX_VALUE); // Avoid overflow

             MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, maxLoadSize);
             byte[] byteArray = new byte[(int) maxLoadSize];
             buffer.get(byteArray);

             String content = new String(byteArray, charset);

             return new StringBuilder(content);

         } catch (IOException e) {
         	 throw new  OpenLoomFileException("Failed to read content from file: " + file, e);
         }
     }
     
    


	
}
