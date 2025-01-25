package com.farmstory.requestdto.user;

import com.farmstory.entity.user.UserScheduleEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PostScheduleReqDto {
    private Long scheduleIdx;

    private Long userIdx;

    private int year;

    private int month;

    private int date;

    private String color;

    private String text;

    private String bgcolor;

    public UserScheduleEntity toEntity(Long userIdxx) {

            return UserScheduleEntity.builder()
                    .userIdx(userIdxx)
                    .bgcolor(bgcolor)
                    .color(color)
                    .text(text)
                    .year(year)
                    .month(month)
                    .date(date)
                    .build();
    }
}
