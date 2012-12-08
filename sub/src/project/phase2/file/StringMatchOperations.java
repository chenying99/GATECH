package project.phase2.file;

import project.phase2.structs.StringMatchList;
import project.phase2.structs.StringMatchTuple;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatchOperations {

    private StringMatchOperations() {
    }

    /**
     * Print a list, all-pretty like.
     *
     * @param stringMatchList
     */
    public static void print(final StringMatchList stringMatchList) {
        System.out.println(stringMatchList.toString());
    }

    /**
     * Find the string in a given file
     *
     * @param file
     * @param string
     * @return results tuple
     */
    public static StringMatchTuple find(final File file, final String string) {
        try {
            return findInFile(file, string);
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    /**
     * replace a given location with a new string.
     *
     * @param file
     * @param line
     * @param startIndex
     * @param endIndex
     * @param replaceWith
     */
    public static void replace(final File file, int line, int startIndex, int endIndex, String replaceWith) {
        try {
            FileEditor.replaceAtSubstring(file, line, startIndex, endIndex, replaceWith);
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    /**
     * Replace a tuple with another string.
     *
     * @param tuple
     * @param replaceWith
     */
    public static void replace(final StringMatchTuple tuple, final String replaceWith) {
        replace(new File(tuple.fileName), tuple.line, tuple.startIndex, tuple.endIndex, replaceWith);
    }

    /**
     * Keep replacing until you just can't replace no more!
     *
     * @param file
     * @param match
     * @param replaceWith
     */
    public static void recursiveReplace(final File file, final String match, final String replaceWith) {
        try {
            boolean remaining = true;
            while (remaining) {
                StringMatchTuple tuple = findInFile(file, match);
                if (tuple.found()) {
                    FileEditor.replaceAtSubstring(new File(tuple.fileName), tuple.line, tuple.startIndex, tuple.endIndex, replaceWith);
                } else {
                    remaining = false;
                }
            }
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    private static StringMatchTuple findInFile(final File file, final String string) throws IOException {
        StringMatchTuple t = new StringMatchTuple(string);
        t.fileName = file.getPath();
        findInFile(t);
        return t;
    }

    private static void findInFile(final StringMatchTuple stringMatchTuple) throws IOException {
        List<String> lines = FileEditor.readEntireFileIntoLines(new File(stringMatchTuple.fileName));
        for (int line = 0; line < lines.size(); line++) {
            Pattern pattern = Pattern.compile(stringMatchTuple.string);
            Matcher matcher = pattern.matcher(lines.get(line));
            if (matcher.find()) {
                stringMatchTuple.startIndex = matcher.start();
                stringMatchTuple.endIndex = matcher.end();
                stringMatchTuple.line = 1 + line;
                break;
            }
        }
    }
}
