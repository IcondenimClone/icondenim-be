package com.store.backend.order.enums;

public enum OrderStatus {
  WAITING, // Chờ xác nhận
  PENDING, // Chờ xử lý
  PROCESSING, // Đang xử lý
  SHIPPING, // Đang vận chuyển
  DELIVERED, // Đã giao hàng
  CANCELED // Đã hủy
}
