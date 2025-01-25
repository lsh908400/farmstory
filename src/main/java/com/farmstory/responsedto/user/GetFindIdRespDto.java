package com.farmstory.responsedto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetFindIdRespDto {
    private String userName;
    private String userId;
    private String userEmail;
    private String userCreateAt;
}
