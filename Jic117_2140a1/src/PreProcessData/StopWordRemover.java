package PreProcessData;

import Classes.*;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StopWordRemover {
  // Essential private methods or variables can be added.
  private BufferedReader inputStream;
  private String line;
  private Map<Character, HashSet<String>> dict;

  // YOU SHOULD IMPLEMENT THIS METHOD.
  public StopWordRemover() {
    // Load and store the stop words from the fileinputstream with appropriate data structure.
    // NT: address of stopword.txt is Path.StopwordDir
    try {
      inputStream = new BufferedReader(new FileReader(new File(Path.StopwordDir)));
    } catch (FileNotFoundException e) {
      System.out.println("File was not Found!");
    }
    try {
      dict = new HashMap<>();
      line = "";
      char key = ' ';
      while (line != null) {
        if (line.length() == 1) {
          key = line.toCharArray()[0];
          dict.put(key, new HashSet<String>());
          dict.get(key).add(line);
        } else if (line.length() > 0 && line.toCharArray()[0] == key) {
          dict.get(key).add(line);
        }
        line = inputStream.readLine();
      }
      inputStream.close();
    } catch (IOException e) {
    }
  }

  // YOU SHOULD IMPLEMENT THIS METHOD.
  public boolean isStopword(char[] word) {
    // Return true if the input word is a stopword, or false if not.
    char title = word[0];
    String wordString = String.valueOf(word);
//	    return set.contains(wordString);
    if (dict.get(title) != null) return dict.get(title).contains(wordString);
    return false;
  }
//	public static void main(String[] args) {
//		StopWordRemover stopWordRemover = new StopWordRemover();
//	    System.out.println(stopWordRemover);
//	    char[] able = {'b', 'b', 'l', 'e'};
//        char[] all = {'a', 'l', 'l'};
//	    System.out.println(stopWordRemover.isStopword(able));
//		System.out.println(stopWordRemover.dict.size());
//	}
}
