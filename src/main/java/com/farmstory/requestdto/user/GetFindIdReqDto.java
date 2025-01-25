package com.farmstory.requestdto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFindIdReqDto {
    private String userName;
    private String userEmail;

}
