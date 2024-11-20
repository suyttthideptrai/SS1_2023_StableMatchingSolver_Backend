package com.example.SS2_Backend.ss.smt.characteristic;

/**
 * @author Tiến Thành
 * Như tên gọi, lưu dữ liệu về Property & Requirement của một trong số những characteristics
 * trên một individual
 * Có nhiều loại Characteristic nên viết thành interface
 */
@Deprecated
public interface Characteristic {

    /**
     * Lấy loại để tiện cho việc tính toán
     * Mỗi loại đại diện cho một kiểu dữ liệu về Characteristic
     * @return loại
     */
    int getType();

    /**
     * Lấy giá trị thực của Property
     * @return giá trị
     */
    int getPropertyValue();

}
