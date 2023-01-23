package work;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Merge {
    work.Error_Pattern ind;
    int size_temp_file = 0;
    Path temp_file;
    Merge(String[] str) throws IOException {
        ind = new work.Error_Pattern(str);
        if (!ind.error){
            merge_file();
        }
    }
    private void merge_file() throws IOException {
        try {
            File output_file = new File(ind.files_names[0]);
            if (output_file.createNewFile()){
                temp_file = Files.createTempFile("temp-", ".txt");
                for (int i = 1; i < ind.number_files; i++){
                    FileReader fr = new FileReader(ind.files_names[i]);
                    BufferedReader reader = new BufferedReader(fr);
                    String line = reader.readLine();
                    while (line != null) {
                        line += "\n";
                        Files.write(temp_file, line.getBytes(), StandardOpenOption.APPEND);
                        line = reader.readLine();
                        size_temp_file++;
                    }
                }
                if(!search_error_file()){
                    System.out.println("Warning:: Some data was lost while reading");
                }
                merge_sort( 0, size_temp_file-1);
                write_output_file(output_file);
                temp_file.toFile().deleteOnExit();
            } else {
                System.out.println("Error:: Creating output file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean search_error_file() throws IOException {
        boolean output = true;
        int size_temp_file = 0;
        Path temp_file_New = Files.createTempFile("temp-", ".txt");
        FileReader fr = new FileReader(temp_file.toFile());
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        if (ind.flags[0].equals("-i")){
            while (line != null) {
                if (line.matches("[-+]?\\d+")){
                    line += "\n";
                    Files.write(temp_file_New, line.getBytes(),StandardOpenOption.APPEND);
                    size_temp_file++;
                } else {
                    output = false;
                }
                line = reader.readLine();
            }
        } else {
            while (line != null) {
                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(line);
                if ((!matcher.find()) && (line.length()>0)){
                    line += "\n";
                    Files.write(temp_file_New, line.getBytes(),StandardOpenOption.APPEND);
                    size_temp_file++;
                } else {
                    output = false;
                }
                line = reader.readLine();
            }
        }
        this.size_temp_file = size_temp_file;
        temp_file.toFile().deleteOnExit();
        temp_file = temp_file_New;
        return output;
    }

    private void merge_sort( int start, int size) throws IOException {
        if (start < size) {
            int m = (start+size)/2;
            merge_sort( start, m);
            merge_sort( m+1, size);
            merge( start, m, size);
        }
    }

    private void merge( int l, int m, int r) throws IOException {
        Path temp_file_Left = Files.createTempFile("temp-", ".txt");
        Path temp_file_Right = Files.createTempFile("temp-", ".txt");

        int n1 = m - l + 1;
        int n2 = r - m;
        write_temp_file(temp_file_Left,l, n1);
        write_temp_file(temp_file_Right,m+1, n2);

        Path temp_file_New = Files.createTempFile("temp-", ".txt");
        FileReader[] fr = new FileReader[3];
        fr[0] = new FileReader(temp_file.toFile());
        fr[1] = new FileReader(temp_file_Left.toFile());
        fr[2] = new FileReader(temp_file_Right.toFile());
        BufferedReader[] reader = new BufferedReader[3];
        String[] line = new String[3];
        for (int i=0; i<3; i++){
            reader[i] = new BufferedReader(fr[i]);
            line[i] = reader[i].readLine();
        }

        int i = 0, j = 0;
        int k = l;
        for( int get = 0; get < k; get++){
            line[0] += "\n";
            Files.write(temp_file_New, line[0].getBytes(), StandardOpenOption.APPEND);
            line[0] = reader[0].readLine();
        }
        while (i < n1 && j < n2) {
            if (left_right_comparison(line[1],line[2])) {
                line[1] += "\n";
                Files.write(temp_file_New, line[1].getBytes(), StandardOpenOption.APPEND);
                line[1] = reader[1].readLine();
                i++;
            } else {
                line[2] += "\n";
                Files.write(temp_file_New, line[2].getBytes(), StandardOpenOption.APPEND);
                line[2] = reader[2].readLine();
                j++;
            }
            line[0] = reader[0].readLine();
            k++;
        }
        while (i < n1) {
            line[1] += "\n";
            Files.write(temp_file_New, line[1].getBytes(), StandardOpenOption.APPEND);
            line[1] = reader[1].readLine();
            i++;
            line[0] = reader[0].readLine();
            k++;
        }

        while (j < n2) {
            line[2] += "\n";
            Files.write(temp_file_New, line[2].getBytes(), StandardOpenOption.APPEND);
            line[2] = reader[2].readLine();
            j++;
            line[0] = reader[0].readLine();
            k++;
        }

        while (line[0] != null){
            line[0] += "\n";
            Files.write(temp_file_New, line[0].getBytes(), StandardOpenOption.APPEND);
            line[0] = reader[0].readLine();
        }
        temp_file.toFile().deleteOnExit();
        temp_file_Left.toFile().deleteOnExit();
        temp_file_Right.toFile().deleteOnExit();
        temp_file = temp_file_New;
    }

    private boolean left_right_comparison(String line1, String line2){
        boolean output = false;
        switch (ind.flags[0]){
            case("-s"):{
                if ((ind.flags[1] != null) && ind.flags[1].compareTo("-d") == 0){
                    output = 0 <= line1.compareTo(line2);
                } else {
                    output = 0 >= line1.compareTo(line2);
                }
                break;
            }
            case("-i"):{
                if ((ind.flags[1] != null) && ind.flags[1].compareTo("-d") == 0){
                    output = Integer.parseInt(line1) >= Integer.parseInt(line2);
                } else {
                    output = Integer.parseInt(line1) <= Integer.parseInt(line2);
                }
                break;
            }
        }
        return output;
    }
    private void write_temp_file (Path temp_file_write, int get, int size) throws IOException {
        FileReader fr = new FileReader(temp_file.toFile());
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        for (int i = 0; i<get; i++){
            line = reader.readLine();
        }
        for (int i=0; i<size; ++i){
            line += "\n";
            Files.write(temp_file_write, line.getBytes(), StandardOpenOption.APPEND);
            line = reader.readLine();
        }
    }

    private void write_output_file(File output_file) throws IOException{
        FileReader fr = new FileReader(temp_file.toFile());
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        String line2 = line;
        while(line != null){
            line2 = line;
            line = reader.readLine();
            if (line != null ){
                line2 += "\n";
            }
            Files.write(output_file.toPath(), line2.getBytes(), StandardOpenOption.APPEND);
        }
    }
}
