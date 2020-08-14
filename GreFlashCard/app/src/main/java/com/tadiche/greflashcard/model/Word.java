package com.tadiche.greflashcard.model;

public class Word {
    String _id;
    String wordstr;
    String type;
    String meaning;

    /**
     * @return the _id
     */

    public String get_id() {
        return _id;
    }

    /**
     * @param _id the _id to set
     */
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * @return the wordstr
     */
    public String getWordstr() {
        return wordstr;
    }

    /**
     * @param wordstr the wordstr to set
     */
    public void setWordstr(String wordstr) {
        this.wordstr = wordstr;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the meaning
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * @param meaning the meaning to set
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    /**
     * @param _id
     * @param wordstr
     * @param type
     * @param meaning
     */
    public Word(String _id, String wordstr, String type, String meaning) {
        super();
        this._id = _id;
        this.wordstr = wordstr;
        this.type = type;
        this.meaning = meaning;
    }

    public Word() {
    }
}
