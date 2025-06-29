package com.store.backend.voucher;

import com.store.backend.voucher.request.CreateVoucherRequest;

public interface VoucherService {
  VoucherEntity createVoucher(CreateVoucherRequest request);
}
