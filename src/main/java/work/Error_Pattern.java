package work;

import java.io.File;
import java.io.IOException;

public class Error_Pattern {
    String[] flags;
    String[] files_names;
    boolean error;
    Error_Pattern(String[] str) throws IOException {
        error = false;
        files_names = new String[str.length];
        flags = new String[2];
        int size_files_names = 0;
        for (int i = 0; (i< str.length) && (!error); i++){
            if (str[i].substring(0, 1).equals("-")){
                int number_parameters = 0;
                String[] parameters = {"-a", "-d", "-s", "-i"};
                for (; (number_parameters < parameters.length) && (!str[i].equals(parameters[number_parameters])); number_parameters++){}
                if (number_parameters != parameters.length){
                    if (flags[0] == null){
                        flags[0] = str[i];
                    } else {
                        if (!flags[0].equals(str[i])){
                            if (number_parameters % 2 == 0){
                                number_parameters++;
                            } else {
                                number_parameters--;
                            }
                            if (!flags[0].equals(parameters[number_parameters])){
                                flags[1] = str[i];
                            } else {
                                error = true;
                                System.out.println("Error:: Opposite parameters");
                            }
                        } else {
                            error = true;
                            System.out.println("Error:: Two identical parameters");
                        }
                    }
                } else {
                    error = true;
                    System.out.println("Error:: There is no such parameter { " + str[i] + " }");
                }
            } else {
                File file = new File(str[i]);
                if ((file.exists() && !file.isDirectory()) || (size_files_names == 0)) {
                    files_names[size_files_names] = str[i];
                    size_files_names++;
                }
                else {
                    System.out.println("Error:: The file in on the patch { " + str[i] + " } does not exist");
                }
            }
        }
    }
}