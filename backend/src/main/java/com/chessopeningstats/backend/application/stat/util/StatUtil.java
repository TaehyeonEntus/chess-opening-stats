package com.chessopeningstats.backend.application.stat.util;

import com.chessopeningstats.backend.application.stat.dto.Stat;
import com.chessopeningstats.backend.domain.GamePlayerResult;

import java.util.List;

public class StatUtil {
    public static Stat mapToStat(List<Object[]> rows) {
        long win = 0;
        long draw = 0;
        long lose = 0;

        for (Object[] row : rows) {
            long count = (Long) row[1];
            GamePlayerResult result = (GamePlayerResult) row[0];

            switch (result) {
                case WIN -> win = count;
                case DRAW -> draw = count;
                case LOSE -> lose = count;
            }
        }

        return new Stat(win, draw, lose);
    }
}
