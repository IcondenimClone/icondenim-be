package com.store.backend.voucher.mapper;

import org.mapstruct.Mapper;

import com.store.backend.voucher.VoucherEntity;
import com.store.backend.voucher.response.VoucherResponse;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
  VoucherResponse entityToResponse(VoucherEntity voucher);
}
