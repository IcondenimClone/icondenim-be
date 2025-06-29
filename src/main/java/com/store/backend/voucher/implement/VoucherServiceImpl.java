package com.store.backend.voucher.implement;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.store.backend.exception.BadRequestException;
import com.store.backend.voucher.VoucherEntity;
import com.store.backend.voucher.VoucherRepository;
import com.store.backend.voucher.VoucherService;
import com.store.backend.voucher.request.CreateVoucherRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherServiceImpl implements VoucherService {
  VoucherRepository voucherRepository;

  @Override
  public VoucherEntity createVoucher(CreateVoucherRequest request) {
    if (request.getDiscountPercent() == null && request.getDiscountAmount() == null) {
      throw new BadRequestException("Yêu cầu nhập 1 trong 2 phương thức giảm giá");
    }
    if (request.getDiscountAmount() != null && request.getDiscountPercent() != null) {
      throw new BadRequestException("Chỉ được chọn 1 trong 2 phương thức giảm giá");
    }

    LocalDate now = LocalDate.now();
    if (!request.getStartAt().isBefore(request.getEndAt())) {
      throw new BadRequestException("Ngày bắt đầu voucher phải nhỏ hơn ngày kết thúc");
    }
    if (request.getStartAt().isBefore(now)) {
      throw new BadRequestException("Ngày bắt đầu voucher phải từ hôm nay trở đi");
    }

    if (voucherRepository.existsByCodeIgnoreCase(request.getCode())) {
      throw new BadRequestException("Code đã tồn tại");
    }

    VoucherEntity newVoucher = VoucherEntity.builder().code(request.getCode().toUpperCase())
        .description(request.getDescription())
        .discountPercent(request.getDiscountPercent()).discountAmount(request.getDiscountAmount())
        .minimumOrderAmount(request.getMinimumOrderAmount()).maximumDiscount(request.getMaximumDiscount())
        .quantity(request.getQuantity()).startAt(request.getStartAt()).endAt(request.getEndAt()).type(request.getType())
        .build();
    newVoucher.initUsed();
    newVoucher.setStock();
    return voucherRepository.save(newVoucher);
  }
}
