package Classes;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StopWordRemover {
  private BufferedReader inputStream;
  private String line;
  private Map<Character, HashSet<String>> dict;

  public StopWordRemover() {
    // Load and store the stop words from the fileinputstream with appropriate data structure.
    // NT: address of stopword.txt is Path.StopwordDir
    try {
      inputStream = new BufferedReader(new FileReader(new File(Path
              .StopwordDir)));
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

  public boolean isStopword(char[] word) {
    // Return true if the input word is a stopword, or false if not.
    char title = word[0];
    String wordString = String.valueOf(word);
    if (dict.get(title) != null) return dict.get(title).contains(wordString);
    return false;
  }
}
