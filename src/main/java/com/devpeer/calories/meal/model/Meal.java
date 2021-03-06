package com.devpeer.calories.meal.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meal {

    public static final String ID_FIELD_NAME = "id";
    public static final String USER_ID_FIELD_NAME = "userId";

    @Id
    private String id;

    @Indexed
    private String userId;

    /**
     * UTC timezone
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;

    private String text;

    private Integer calories;

    /**
     * Extra boolean field set to true, if the total for that day is less than expected number of calories per day,
     * otherwise false.
     */
    @BsonIgnore
    private Boolean isTotalForTheDayOk;
}
