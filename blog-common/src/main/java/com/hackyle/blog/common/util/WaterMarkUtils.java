package com.hackyle.blog.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片水印工具
 */
public class WaterMarkUtils {

    // 水印透明度
    private static final float alpha = 0.4f;

    /**
     * 水印文字字体，size=25为字体的行高，单位px
     */
    private static final Font font = new Font("Microsoft Yahei UI", Font.BOLD, 25);
    // 水印文字颜色
    private static final Color color = Color.red;


    /**
     * 给图片加图片水印，水印坐落于右下侧，距离最右边距有40px，距离最下边距有40px
     * @param markPic 图片水印
     * @param sourceImgStream 待添加水印的图片
     * @param targetImgStream 图片输出流
     * @param imgType 图片文件格式，例如png、jpg
     * @throws IOException -
     */
    public static void markByPic(InputStream markPic, InputStream sourceImgStream, OutputStream targetImgStream, String imgType) throws IOException {
        //入参检查
        if(imgType == null || "".equals(imgType.trim())) {
            throw new IllegalArgumentException("The img type can't be null: imgType=" +imgType);
        }
        if(sourceImgStream == null || markPic == null) {
            throw new IllegalArgumentException("The image stream can't be null!");
        }

        //图片读入
        BufferedImage sourceImage = ImageIO.read(sourceImgStream);
        int sourceImageWidth = sourceImage.getWidth();
        int sourceImageHeight = sourceImage.getHeight();

        //图片太小，就不要添加水印了
        if(sourceImageHeight < 350 || sourceImageWidth < 650) {
            ImageIO.write(sourceImage, imgType, targetImgStream);
        }

        //读取水印图片
        BufferedImage markImage = ImageIO.read(markPic);
        int markWidth = markImage.getWidth();
        int markHeight = markImage.getHeight();

        //获取画笔
        Graphics2D graph = sourceImage.createGraphics();
        //设置对线段的锯齿状边缘处理
        graph.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graph.drawImage(sourceImage.getScaledInstance(sourceImageWidth, sourceImageHeight, Image.SCALE_SMOOTH), 0, 0, null);

        //设置水印文字颜色
        graph.setColor(color);
        //设置水印文字Font
        graph.setFont(font);
        //设置水印文字透明度
        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

        //文本水印，水印所在坐标
        int widthLocation = sourceImageWidth - 40 - markWidth; //水印距离最右边有40px，另外一个的是水印图片的宽度
        int heightLocation = sourceImageHeight - 40 - markHeight; //水印距离最下边有40px，另外一个是水印图片的长度
        graph.drawImage(markImage, widthLocation, heightLocation, null);

        //关闭画笔
        graph.dispose();

        //图片写出
        ImageIO.write(sourceImage, imgType, targetImgStream);
    }

    /**
     * 给图片加文本水印，水印坐落于右下侧，距离最右边距有40px，距离最下边距有40px
     * @param text 文本水印
     * @param sourceImgStream 待添加水印的图片
     * @param targetImgStream 图片输出流
     * @param imgType 图片文件格式，例如png、jpg
     * @throws IOException -
     */
    public static void markByText(String text, InputStream sourceImgStream, OutputStream targetImgStream, String imgType) throws IOException {
        //入参检查
        if(text == null || text.trim().isEmpty() || imgType == null || imgType.trim().isEmpty()) {
            throw new IllegalArgumentException("The parameters of image water mark can't be null: text=" +text+ ", imgType=" +imgType);
        }
        if(sourceImgStream == null) {
            throw new IllegalArgumentException("The image source stream can't be null!");
        }

        //图片读入
        BufferedImage sourceImage = ImageIO.read(sourceImgStream);
        int sourceImageWidth = sourceImage.getWidth();
        int sourceImageHeight = sourceImage.getHeight();

        //图片太小，就不要添加水印了
        if(sourceImageHeight < 350 || sourceImageWidth < 650) {
            ImageIO.write(sourceImage, imgType, targetImgStream);
        }

        //获取画笔
        Graphics2D graph = sourceImage.createGraphics();
        //设置对线段的锯齿状边缘处理
        graph.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graph.drawImage(sourceImage.getScaledInstance(sourceImageWidth, sourceImageHeight, Image.SCALE_SMOOTH), 0, 0, null);

        //设置水印文字颜色
        graph.setColor(color);
        //设置水印文字Font
        graph.setFont(font);
        //设置水印文字透明度
        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

        //文本水印，水印所在坐标
        int widthLocation = sourceImageWidth - 40 - getWatermarkLength(text, graph); //水印距离最右边有40px，另外的是字体的长度
        int heightLocation = sourceImageHeight - 40; //水印距离最下边有40px
        graph.drawString(text, widthLocation, heightLocation);

        //关闭画笔
        graph.dispose();

        //图片写出
        ImageIO.write(sourceImage, imgType, targetImgStream);
    }

    /**
     * 获取水印文字的长度
     */
    private static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

}
