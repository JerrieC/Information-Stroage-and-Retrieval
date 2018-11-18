package Classes;

/**
 * This is for INFSCI 2140 in 2018
 */
public class WordNormalizer {
  public char[] lowercase(char[] chars) {
    // Transform the word uppercase characters into lowercase.
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] <= 'Z' && chars[i] >= 'A') chars[i] += 32;
    }
    return chars;
  }

  public String stem(char[] chars) {
    // Return the stemmed word with Stemmer in Classes package.
    String str = "";
    Stemmer stemmer = new Stemmer();
    stemmer.add(chars, chars.length);
    stemmer.stem();
    str = stemmer.toString();
    return str;
  }
}
