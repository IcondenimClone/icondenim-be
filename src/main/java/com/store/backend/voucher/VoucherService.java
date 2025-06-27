package com.store.backend.voucher;

import com.store.backend.voucher.request.CreateVoucherRequest;
import com.store.backend.voucher.response.VoucherResponse;

public interface VoucherService {
  void createVoucher(CreateVoucherRequest request);
}
