package com.farmstory.requestdto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DeleteProductReqDto {
    private List<String> prodIdx;
}
