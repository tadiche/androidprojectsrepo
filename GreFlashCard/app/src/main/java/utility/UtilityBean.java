package utility;

import com.tadiche.greflashcard.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class UtilityBean implements IUtilityBean{

    private List<Word> wordList = new ArrayList<>();
    private Stack<Word> leftSwipeStack = new Stack<>();
    private Stack<Word> rightSwipeStack = new Stack<>();

    @Override
    public List<Word> getWordList() {
        return wordList;
    }

    @Override
    public void addToWordList(Word word) {
        wordList.add(word);
    }

    @Override
    public void addWordToStack(Word word, String movement) {
        switch (movement){
            case "left":
                //add to left stack;
                getLeftSwipeStack().push(word);
                break;
            case "right":
                //add to right stack
                getRightSwipeStack().push(word);
                break;

            default:{break;}
        }
    }

    @Override
    public Word getWordFromStack(String movement) {
        Word word;
        switch (movement){
            case "left":
                //add to left stack;
                if (!getLeftSwipeStack().isEmpty()) {
                    word = getLeftSwipeStack().pop();
                }else {word = null;}
                break;
            case "right":
                //add to right stack
                if (!getRightSwipeStack().isEmpty()) {
                    word = getRightSwipeStack().pop();
                }else {word = null;}
                break;

            default:{ word = null;break;}
        }
        return word;
    }

    /*public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
    }*/

    public Stack<Word> getLeftSwipeStack() {
        return leftSwipeStack;
    }

    public void setLeftSwipeStack(Stack<Word> leftSwipeStack) {
        this.leftSwipeStack = leftSwipeStack;
    }

    public Stack<Word> getRightSwipeStack() {
        return rightSwipeStack;
    }

    public void setRightSwipeStack(Stack<Word> rightSwipeStack) {
        this.rightSwipeStack = rightSwipeStack;
    }
}
