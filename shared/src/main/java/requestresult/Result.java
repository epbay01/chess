package requestresult;

import com.google.gson.Gson;

public abstract class Result {
    // for polymorphism (especially with error results and regular)

    public static String toJson(Result result) {
        Gson gson = new Gson();
        return gson.toJson(result);
    }
}
