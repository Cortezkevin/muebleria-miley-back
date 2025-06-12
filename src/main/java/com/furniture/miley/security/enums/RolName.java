package com.furniture.miley.security.enums;

public enum RolName {
    ROLE_ADMIN, ROLE_TRANSPORT, ROLE_WAREHOUSE, ROLE_USER;
    public String getCapitalizedName() {
        String[] words = this.name().split("_");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            capitalized.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return capitalized.toString().trim();
    }
}
