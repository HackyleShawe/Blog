package com.hackyle.blog.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class WaterMarkUtilsTest {
    @Test
    public void testMarkByPic() throws IOException {
        //原始图片
        String source = "C:\\Users\\KYLE\\Desktop\\aa.png";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));

        //水印图片
        BufferedInputStream markBis = new BufferedInputStream(new FileInputStream("C:\\Users\\KYLE\\Desktop\\mark.png"));

        //打上水印的输出图片
        String target = "C:\\Users\\KYLE\\Desktop\\" +System.currentTimeMillis()+ ".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

        WaterMarkUtils.markByPic(markBis, bis, bos, "png");

    }

    @Test
    public void testMarkByText() throws Exception {
        String source = "C:\\Users\\KYLE\\Desktop\\aa.png";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));

        String text = "HACKYLE";

        String target = "C:\\Users\\KYLE\\Desktop\\" +System.currentTimeMillis()+ ".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

        WaterMarkUtils.markByText(text, bis, bos, "png");
    }
}
