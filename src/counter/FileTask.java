package counter;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import org.apache.commons.io.IOUtils;

import java.io.File;

public class FileTask extends Task<Integer> {

    File textFile;
    String result = "";
    String name = "";

    FileTask(File file) {
        this.textFile = file;
        this.name = file.getName();
    }

    @Override
    protected Integer call() throws Exception {

        @SuppressWarnings("deprecation")
        String fileContents = IOUtils.toString(textFile.toURI());

        if (fileContents == null || fileContents.isEmpty()) {
            System.out.println("empty");
        }

        String[] words = new String[0];

        assert fileContents != null;
        words = fileContents.split("\\s+");
        result = String.valueOf(words.length);


        return words.length;
    }

    public String getResult() {
        return this.result;
    }

    public String getName() {
        return this.name;
    }
}
