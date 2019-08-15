package com.madalin.wisetraveller.model.enums;

public enum FacebookProfilePictureType {
    Small,
    Normal,
    Album,
    Large,
    Square;

    public String asFacebookParameter() {
        return toString().toLowerCase();
    }
}
