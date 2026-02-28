package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "vladsheshko" + timestamp + "@example.com";
    }

    public static Map<String, String> getRegistrationData(){
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123");
        data.put("username", "learnqaV");
        data.put("firstName", "learnqaV");
        data.put("lastName", "learnqaV");

        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }
    
    public static Map<String, String> getRegistrationDataWithoutField(String missingField) {
        Map<String, String> data = new HashMap<>();
        String[] fields = {"email", "password", "username", "firstName", "lastName"};

        for (String field : fields) {
            if (!field.equals(missingField)) {
                switch (field) {
                    case "email": data.put("email", getRandomEmail()); break;
                    case "password": data.put("password", "123"); break;
                    default: data.put(field, "TestUser"); break;
                }
            }
        }
        return data;
    }
}
