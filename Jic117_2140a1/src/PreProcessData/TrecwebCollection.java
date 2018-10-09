package PreProcessData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	// Essential private methods or variables can be added.
	private File trecFile;
	private static final String TEXT_END = "</DOC>";
	private static final String TEXT_NUM_START = "<DOCNO>";
	private static final String TEXT_CONTENT_START = "</DOCHDR>";
	private BufferedReader inputStream;

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public TrecwebCollection() throws IOException {
		// 1. Open the file in Path.DataWebDir.
		// 2. Make preparation for function nextDocument().
		// NT: you cannot load the whole corpus into memory!!
		trecFile = new File(Path.DataWebDir);
		inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(trecFile));
		} catch (FileNotFoundException e) {
			System.out.println("File was not Found!");
		}
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public Map<String, Object> nextDocument() throws IOException {
		// 1. When called, this API processes one document from corpus, and returns its doc number and content.
		// 2. When no document left, return null, and close the file.
		// 3. the HTML tags should be removed in document content.
		Map<String, Object> map = new HashMap<String, Object>();
		String number = null;
		StringBuffer content = new StringBuffer();
		String line = "";
		line = inputStream.readLine();
		// when no document left, return null
		while (line != null && (!line.equals(TEXT_END) || number == null)) {
			line = inputStream.readLine();
			if (line == null) {
				break;
			}
			if (line.length() > 7 && line.substring(0, 7).equals(TEXT_NUM_START)) {
				number = line.substring(7, line.length() - 8).replaceAll(" ", ""); // delete all spaces
			} else if (line.equals(TEXT_CONTENT_START)) { // read content
				// jump over content without numbers
				while (!line.equals(TEXT_END)) {
//					System.out.println(line);
					content.append(line + " ");
					line = inputStream.readLine();
				}
			}
		}
		if (line != null) {
			content = removeTags(content);
			map.put(number, content.toString().toCharArray());
			return map;
		}
		inputStream.close();
		return null;
	}

	public static StringBuffer removeTags(StringBuffer content) {
		StringBuffer cleanContent = new StringBuffer();
		cleanContent = cleanContent.append(content.toString().replaceAll("\\<.*?>",
						""));
//			}

//		}
		return cleanContent;
	}
//	test main
//    public static void main(String[] args) throws IOException {
//      DocumentCollection corpus = new TrecwebCollection();
//      String string = String.valueOf(corpus.nextDocument().get("00001"));
//      corpus.nextDocument();
//      String string2 = String.valueOf(corpus.nextDocument().get("lists-000-0000000"));
//      System.out.println("haha"+string);
//}
}
