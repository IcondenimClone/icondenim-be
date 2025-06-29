package com.store.backend.voucher;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.store.backend.voucher.enums.VoucherType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "vouchers", uniqueConstraints = @UniqueConstraint(columnNames = "code", name = "vouchers_code_key"))
public class VoucherEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @Column(nullable = false, length = 50)
  private String code;

  @Column(nullable = false)
  private String description;

  @Column
  private Integer discountPercent;

  @Column
  private BigDecimal discountAmount;

  @Column
  private BigDecimal minimumOrderAmount;

  @Column
  private BigDecimal maximumDiscount;

  @Column
  private Integer quantity;

  @Column
  private Integer used;

  @Column
  private Integer stock;

  @Column(nullable = false)
  private LocalDate startAt;

  @Column(nullable = false)
  private LocalDate endAt;

  @Column(nullable = false)
  @Builder.Default
  private boolean active = true;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private VoucherType type;

  public void initUsed() {
    if (this.quantity != null) {
      this.used = 0;
    }
  }

  public void setStock() {
    if (this.quantity != null && this.used != null) {
      this.stock = this.quantity - this.used;
    }
  }

  public boolean isValid() {
    LocalDate now = LocalDate.now();
    boolean validStock = (this.stock == null) || this.stock > 0;
    return this.active && validStock && !now.isBefore(startAt) && !now.isAfter(endAt);
  }
}
