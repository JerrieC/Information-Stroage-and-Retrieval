package PreProcessData;

/**
 * This is for INFSCI 2140 in 2018
 * <p>
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
  // Essential private methods or variables can be added.
  private int start;
  private int size;
  private char[] texts;

  // YOU MUST IMPLEMENT THIS METHOD.
  public WordTokenizer(char[] texts) {
    // Tokenize the input texts.
    start = 0;
    this.texts = texts;
    size = texts.length;
  }

  // YOU MUST IMPLEMENT THIS METHOD.
  public char[] nextWord() {
    // Return the next word in the document.
    // Return null, if it is the end of the document.
    while (start < size && !isAlphabeticOrNumber(texts[start])) {
      start++;
    }
    if (start < size) {
      int wordLength = 0;
      for (int i = start; i < size && isAlphabeticOrNumber(texts[i]); i++) {
        wordLength++;
      }
      char[] nextWord = new char[wordLength];
      for (int i = 0; i < wordLength; i++) {
        nextWord[i] = texts[start + i];
      }
      start += wordLength;
      return nextWord;
    }
    return null;
  }

  private boolean isAlphabeticOrNumber(char c) {
    return Character.isAlphabetic(c) || Character.isDigit(c);
  }

//  	public static void main(String[] args) {
//	    String string = "word is 233 ";
//	    char[] texts = string.toCharArray();
//	    WordTokenizer wordTokenizer = new WordTokenizer(texts);
//	    char[] firstWord = wordTokenizer.nextWord();
//	    System.out.println(firstWord[3]);
//	    System.out.println(firstWord.length);
//	    char[] secondWord = wordTokenizer.nextWord();
//	    System.out.println(secondWord.length);
//	    char[] secondWord3 = wordTokenizer.nextWord();
//	    System.out.println(secondWord3);
//	}
}
