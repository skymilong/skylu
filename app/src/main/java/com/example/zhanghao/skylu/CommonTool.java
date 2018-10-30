package com.example.zhanghao.skylu;

import java.util.UUID;

public class CommonTool {
    public static final String CID="428f5ff441fa75e263e4a58d41be5b0a";

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");


    }
}
