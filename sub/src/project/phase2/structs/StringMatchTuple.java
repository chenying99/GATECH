package project.phase2.structs;

public class StringMatchTuple {

    public String string;
    public String fileName = "";
    public int line = 0, startIndex = 0, endIndex = 0;

    public StringMatchTuple() {
        //default
    }

    public StringMatchTuple(String s) {
        string = s;
    }

    public StringMatchTuple(StringMatchTuple s) {
        set(s);
    }

    public void set(StringMatchTuple s) {
        string = s.string;
        fileName = s.fileName;
        line = s.line;
        startIndex = s.startIndex;
        endIndex = s.endIndex;
    }

    public String toString() {
        // "cba"<"file1.txt", 40, 50>}
        return "\"" + string + "\" <\"" + fileName + "\", " + line + ", " + startIndex + ", " + endIndex + ">";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof StringMatchTuple)) {
            return false;
        }

        return string.equals(((StringMatchTuple) o).string);
    }
}