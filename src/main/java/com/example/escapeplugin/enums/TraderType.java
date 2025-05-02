package com.example.escapeplugin.enums;

public enum TraderType 
{
    COOK(""), 
    GUNMAKER(""),
    TOOLMAKER(""),
    MAGICIAN(""),
    TRAPPER(""),
    MYSTERIOUS("");

    private String name;

    TraderType(String name)
    {
        this.name = name;
    };
    
    public String getName() { return name; }
}
