package com.farmstory.requestdto.product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdateAdminProductReqDto {
    private Long prodIdx;
    private int quantity;
}
