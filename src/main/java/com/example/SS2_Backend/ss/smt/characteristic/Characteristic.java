package com.example.SS2_Backend.ss.smt.characteristic;

import com.example.SS2_Backend.ss.smt.requirement.Requirement;

/**
 * @author Tiến Thành
 * Như tên gọi, lưu dữ liệu về Property & Requirement của một trong số những characteristics
 * trên một individual
 * Có nhiều loại Characteristic nên viết thành interface
 */
public interface Characteristic {

    /**
     * Lấy loại để tiện cho việc tính toán
     * Mỗi loại đại diện cho một kiểu dữ liệu về Characteristic
     * @return loại
     */
    int getType();

    /**
     * Lấy giá trị thực của Characteristic
     *
     * @return giá trị
     */
    Object getProperty();

    /**
     * Lấy trọng số của Characteristic
     *
     * @return Double
     */
    Double getWeight();

    /**
     * Lấy yêu cầu của Characteristic
     *
     * @return Yêu cầu
     */
    <T extends Requirement> T getRequirement();
}
