package com.farmstory.responsedto.board;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetMainBoardsRespDto {
    private String boardTitle;
    private Long boardIdx;
    private String boardCreateAt;
    private String boardSection;
    private String boardType;
}
