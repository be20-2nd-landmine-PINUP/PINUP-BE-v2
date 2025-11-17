package pinup.backend.recommendation.query.dto;

import java.time.LocalDate;

public class SeasonUtil {

    public static String getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();

        if (month == 12 || month == 1 || month == 2) {
            return "겨울";
        } else if (month >= 3 && month <= 5) {
            return "봄";
        } else if (month >= 6 && month <= 8) {
            return "여름";
        } else {
            return "가을";
        }
    }
}
