package com.stupidbeauty.builtinftp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Utils {
    public static final String SHEL_EXECUTE_ERROR = "SHEL_EXECUTE_ERROR";

    public static String shellExec(String cmdCommand) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final Process process = Runtime.getRuntime().exec(cmdCommand);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (Exception e) {
            return SHEL_EXECUTE_ERROR;
        }
        return stringBuilder.toString();
    }
}
