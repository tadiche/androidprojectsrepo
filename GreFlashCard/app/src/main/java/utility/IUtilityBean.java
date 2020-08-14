package utility;

import com.tadiche.greflashcard.model.Word;

import java.util.List;

public interface IUtilityBean {
    List<Word> getWordList();
    void addToWordList(Word word);
    void addWordToStack(Word word,String movement);
    Word getWordFromStack(String movement);
}
