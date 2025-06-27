package com.wuplish;

import com.wuplish.main.thisismainbro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args)  {
        try {
            thisismainbro.main();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
