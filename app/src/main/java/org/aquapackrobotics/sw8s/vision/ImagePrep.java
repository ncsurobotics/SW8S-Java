package org.aquapackrobotics.sw8s.vision;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.aquapackrobotics.sw8s.vision.IntPair;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

/**
 * Base for all image processing modules
 * - holds input image and its properties
 * - preprocessing tasks
 * - slice image to blocks
 * - kmeans (by blocks)
 * - PCA of a binary image
 * - more
 * 
 * @author Xingjian Li
 *
 */
public class ImagePrep {
    private int IMG_WIDTH = 200;
    private int IMG_HEIGHT = 200;
    private final double SCALE;
    private boolean resized = false;
    private int id_width, id_height = 0;
    private int max_width_id, max_height_id = 0;
    private int block_width, block_height = 0;
    private Size image_size;

    public Mat inputImg = new Mat();
    public Mat processImg = new Mat();
    public Mat resultImg = new Mat();
    public Mat block = new Mat();
    public Rect ROI = new Rect();

    public ImagePrep(double scale) {
        this.SCALE = scale;
    }

    /**
     * Default constructor
     */
    public ImagePrep() {
        this.SCALE = 0.5;
    }

    public void set_input_to_result() {
        this.inputImg = this.resultImg;
    }

    /**
     * auto resizes inputImg to IMG_WIDTH and IMG_HEIGHT
     * 
     * @param frame inputImg
     */
    public void setFrame(Mat frame) {
        setFrame(frame, true);
    }

    /**
     * optional resize, set the inputImg
     * 
     * @param frame  inputImg
     * @param resize boolean
     */
    public void setFrame(Mat frame, boolean resize) {
        // do not resize the image
        if (!resize) {
            this.IMG_HEIGHT = frame.height();
            this.IMG_WIDTH = frame.width();
        }
        try {
            if (!resized) {
                this.IMG_HEIGHT = (int) (frame.height() * SCALE);
                this.IMG_WIDTH = (int) (frame.width() * SCALE);
                resized = true;
            }
            Imgproc.resize(frame, this.inputImg, new Size(this.IMG_WIDTH, this.IMG_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.image_size = this.inputImg.size();
    }

    private boolean checkBounds() {
        if (max_width_id < id_width || max_height_id < id_height) {
            return false;
        }
        return true;
    }

    private void set_block() {
        if (checkBounds()) {
            this.ROI = new Rect(block_width * id_width, block_height * id_height, block_width, block_height);
            this.block = new Mat(this.inputImg, this.ROI).clone();
        } else {
            System.out.println("ImagePrep:set_block(): Block out of bounds");
        }
    }

    /**
     * Configures the coordinates of the top left corner of each block using number
     * of desired blocks
     * 
     * @param num_x number of blocks in the horizontal direction
     * @param num_y number of blocks in the vertical direction
     */
    public void sliceNumber(int num_x, int num_y) {
        this.block_width = (int) this.image_size.width / num_x;
        this.block_height = (int) this.image_size.height / num_y;
        this.max_width_id = num_x - 1;
        this.max_height_id = num_y - 1;
        this.IMG_WIDTH = num_x * this.block_width;
        this.IMG_HEIGHT = num_y * this.block_height;
        setFrame(this.inputImg);
    }

    /**
     * Configures the coordinates of the top left corner of each block using size of
     * each block
     * 
     * @param block_width
     * @param block_height
     */
    public void sliceSize(int block_width, int block_height) {

        this.block_width = block_width;
        this.block_height = block_height;
        this.max_width_id = (int) (this.image_size.width / this.block_width - 1);
        this.max_height_id = (int) (this.image_size.height / this.block_height - 1);
        this.IMG_WIDTH = (this.max_width_id + 1) * this.block_width;
        this.IMG_HEIGHT = (this.max_height_id + 1) * this.block_height;

        setFrame(this.inputImg);
    }

    /**
     * https://forum.opencv.org/t/opencv-kmeans-java/285/3
     * changes the resultImg to kmeans of the block
     * 
     * @param n_clusters num of colors
     * @param img        input image
     */
    public Mat kmeans(int n_clusters, Mat img) {
        Mat data = img.reshape(1, (int) img.total());
        Mat data_32f = new Mat();
        data.convertTo(data_32f, CvType.CV_32F);
        Mat bestLabels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        int attempts = 3;
        int flags = Core.KMEANS_PP_CENTERS;
        Mat center = new Mat();

        Core.kmeans(data_32f, n_clusters, bestLabels, criteria, attempts, flags, center);
        Mat draw = new Mat((int) img.total(), 1, CvType.CV_32FC3);
        Mat colors = center.reshape(3, n_clusters);

        for (int i = 0; i < n_clusters; i++) {
            Mat mask = new Mat();
            Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
            Mat col = colors.row(i);
            double d[] = col.get(0, 0);
            draw.setTo(new Scalar(d[0], d[1], d[2]), mask);
        }

        draw = draw.reshape(3, img.rows());
        draw.convertTo(draw, CvType.CV_8U);
        return draw;
    }

    /**
     * perform local kmeans block by block
     * 
     * @param intermediate_k num of colors in each block
     * @param global_k       num of colors in entire image, <=0 for off
     */
    public void localKmeans(int intermediate_k, int global_k) {
        this.inputImg.copyTo(this.processImg);
        for (int row = 0; row <= this.max_height_id; row++) {
            this.id_height = row;
            for (int col = 0; col <= this.max_width_id; col++) {
                this.id_width = col;
                set_block();
                kmeans(intermediate_k, this.block).copyTo(this.processImg.submat(this.ROI));

            }
        }
        if (global_k > 0) {
            this.resultImg = kmeans(global_k, this.processImg);
        } else {
            this.resultImg = this.processImg;
        }
    }

    /**
     * perform local kmeans block by block
     * 
     * @param intermediate_k num of colors in each block
     * @param global_k       num of colors in entire image, <=0 for off
     */
    public void localKmeansParallel(int intermediate_k, int global_k) {
        System.out.println("PARALLLEL PROCESS: 0 -> " + this.max_height_id);
        IntStream.range(0, this.max_height_id).forEach(row -> {
            System.out.println("LOOP HIT: " + row);
            id_height = row;
            IntStream.range(0, this.max_width_id).forEach(col -> {
                id_width = col;
            });
            for (int col = 0; col <= this.max_width_id; col++) {
                id_width = col;
                set_block();
                kmeans(intermediate_k, this.block).copyTo(this.processImg.submat(this.ROI));
            }
        });
        if (global_k > 0) {
            this.resultImg = kmeans(global_k, this.processImg);
        } else {
            this.resultImg = this.processImg;
        }
    }

    /**
     * calculate the mean, pca vectors and values from matrix of points
     * 
     * @param image_on_points Mat of points
     * @return [mean. pca_vector, pca_value]
     */
    public List<Mat> binaryPCA(MatOfPoint image_on_points) {
        Mat mean = new Mat();
        Mat eigenvectors = new Mat();
        Mat eigenvalues = new Mat();

        List<Point> pts = image_on_points.toList();
        Mat image_data = new Mat(pts.size(), 2, CvType.CV_64F);
        double[] dataPtsData = new double[(int) (image_data.total() * image_data.channels())];
        for (int i = 0; i < image_data.rows(); i++) {
            dataPtsData[i * image_data.cols()] = pts.get(i).x;
            dataPtsData[i * image_data.cols() + 1] = pts.get(i).y;
        }
        image_data.put(0, 0, dataPtsData);

        Core.PCACompute2(image_data, mean, eigenvectors, eigenvalues);
        List<Mat> ret = new ArrayList<>();
        ret.add(mean);
        ret.add(eigenvectors);
        ret.add(eigenvalues);
        return ret;
    }

    /**
     * receive a binary image and return the coordinates of high pixels (value 1)
     * 
     * @param binary_image
     * @return
     */
    protected MatOfPoint cvtBinaryToPoints(Mat binary_image) {
        List<Point> on_points = new ArrayList<>();
        for (int h = 0; h < binary_image.height(); h++) {
            for (int w = 0; w < binary_image.width(); w++) {
                if ((int) binary_image.get(h, w)[0] == 255) {
                    on_points.add(new Point(w, h));
                }
            }
        }
        MatOfPoint ret = new MatOfPoint();
        ret.fromList(on_points);
        return ret;
    }

    /**
     * obtain an arraylist of unique colors
     * 
     * @param gray_image
     * @return arraylist
     */
    protected List<Integer> uniqueColor(Mat gray_image) {
        List<Integer> unique_colors = new ArrayList<>();
        for (int h = 0; h < gray_image.height(); h++) {
            for (int w = 0; w < gray_image.width(); w++) {
                double[] color = gray_image.get(h, w);
                if (!unique_colors.contains((int) color[0])) {
                    unique_colors.add((int) color[0]);
                }
            }
        }
        return unique_colors;
    }

    /**
     * obtain an arraylist of unique colors
     * 
     * @param yuv_image input image
     * @return arraylist
     */
    protected Set<IntPair> uniqueColor_YUV(Mat yuv_image) {
        Set<IntPair> unique_colors = new HashSet<>();
        for (int h = 0; h < yuv_image.height(); h++) {
            for (int w = 0; w < yuv_image.width(); w++) {
                double[] color = yuv_image.get(h, w);
                IntPair color_set = new IntPair((int) color[1], (int) color[2]);
                unique_colors.add(color_set);
            }
        }
        return unique_colors;

    }
    protected Set<IntPair> uniqueColor_YUV_Wall(Mat yuv_image) {
        Set<org.aquapackrobotics.sw8s.vision.IntPair> unique_colors = new HashSet<>();
        for (int h = 0; h < yuv_image.height(); h++) {
            for (int w = 0; w < yuv_image.width(); w++) {
                double[] color = yuv_image.get(h, w);
                IntPair color_set = new IntPair((int) color[0], (int) color[0]);
                unique_colors.add(color_set);
            }
        }
        return unique_colors;

    }
    public void debug() {
        System.out.println("done processing\n");
    }
}
