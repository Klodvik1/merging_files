package work;

import java.io.File;
import java.io.IOException;

public class Error_Pattern {
    String[] flags;
    String[] files_names;
    int number_files;
    boolean error;
    Error_Pattern(String[] str) throws IOException {
        error = false;
        files_names = new String[str.length];
        flags = new String[2];
        int size_files_names = 0;
        boolean obligatory_parameters = false;
        for (int i = 0; (i< str.length) && (!error); i++){
            if (str[i].substring(0, 1).equals("-")){
                int number_parameters = 0;
                String[] parameters = {"-a", "-d", "-s", "-i"};
                for (; (number_parameters < parameters.length) && (!str[i].equals(parameters[number_parameters])); number_parameters++){}
                if (number_parameters != parameters.length){
                    if (flags[0] == null){
                        flags[0] = str[i];
                        if (number_parameters == 2 || number_parameters == 3){
                            obligatory_parameters = true;
                        }
                    } else if (flags[1] == null){
                        if (!flags[0].equals(str[i])){
                            if (number_parameters == 2 || number_parameters == 3){
                                obligatory_parameters = true;
                            }
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
                    } else {
                        error = true;
                        System.out.println("Error:: More than 2 parameters");
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
                    System.out.println("Warning:: The file in on the patch { " + str[i] + " } does not exist");
                }
            }
        }
        if (!error){
            if (size_files_names < 2) {
                error = true;
                System.out.println("Error:: Less than 2 files have been entered");
            }
            if (!obligatory_parameters){
                error = true;
                System.out.println("Error:: Need parameter { -s } or { -i }");
            }

            if ((flags[1] != null) && (0>flags[0].compareTo(flags[1]))){
                String temp = flags[0];
                flags[0] = flags[1];
                flags[1] = temp;
            }
        }
        number_files = size_files_names;
    }
}