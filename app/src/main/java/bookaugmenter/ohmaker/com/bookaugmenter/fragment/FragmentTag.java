package bookaugmenter.ohmaker.com.bookaugmenter.fragment;

/**
 * Fragment名
 */
public enum FragmentTag {
    MAIN("main"),
    READ_BOOK("read_book"),
    CREATE_TAG("create_tag");

    private String mName;

    FragmentTag(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }
}
