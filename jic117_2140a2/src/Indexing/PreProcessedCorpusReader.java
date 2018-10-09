package Indexing;
import Classes.Path;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PreProcessedCorpusReader {
	private File file;
	private BufferedReader inputStream;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		file = new File(Path.ResultHM1+type);
		inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("File was not found.");
		}
	}

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		Map<String, String> map = new HashMap<>();
		StringBuffer content = new StringBuffer();
		String line = inputStream.readLine();
		if(line==null) {
			inputStream.close();
			return null;
		} else {
			map.put("DOCNO", line);
			map.put("CONTENT", inputStream.readLine());
			return map;
		}
	}
//	public static void main(String[] args) throws IOException{
//		PreProcessedCorpusReader p = new PreProcessedCorpusReader("trectext");
//		Map<String, String> pp = p.NextDocument();
//		System.out.println(pp.get("DOCNO"));
//		System.out.println(pp.get("CONTENT"));
//	}
}
