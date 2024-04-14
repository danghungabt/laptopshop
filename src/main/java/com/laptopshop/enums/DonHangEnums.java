package com.laptopshop.enums;

public enum DonHangEnums {
    DANG_CHO_GIAO	("dang-cho-giao");
    private String typeName;

    DonHangEnums(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
