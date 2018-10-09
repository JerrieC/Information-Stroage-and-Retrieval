package PreProcessData;

import Classes.*;

/**
 * This is for INFSCI 2140 in 2018
 */
public class WordNormalizer {
  // Essential private methods or variables can be added.

  // YOU MUST IMPLEMENT THIS METHOD.
  public char[] lowercase(char[] chars) {
    // Transform the word uppercase characters into lowercase.
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] <= 'Z' && chars[i] >= 'A') chars[i] += 32;
    }
    return chars;
  }

  // YOU MUST IMPLEMENT THIS METHOD.
  public String stem(char[] chars) {
    // Return the stemmed word with Stemmer in Classes package.
    String str = "";
    Stemmer stemmer = new Stemmer();
    stemmer.add(chars, chars.length);
    stemmer.stem();
    str = stemmer.toString();
    return str;
  }
//	public static void main(String[] args) {
//        String ssss = "Hello";
//        WordNormalizer wordNormalizer = new WordNormalizer();
//        System.out.println(Arrays.toString(wordNormalizer.lowercase(ssss.toCharArray())));
//    }

}
