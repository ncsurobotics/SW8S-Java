
import cv2
import numpy as np

from time import time
total_time_func = 0
PRINT_TIME = False
def time_func(func):
    def wrapper(*args, **kwargs):
        start_time = time()
        result = func(*args, **kwargs)
        end_time = time()
        global total_time_func 
        total_time_func += end_time-start_time
        if (PRINT_TIME):
            print(f"Time taken for {func.__name__}: {end_time-start_time}")
        return result
    return wrapper

class ImagePrep:
    KMEANSFILTER = [3,  # num of clusters
                4,  # num of iterations
                (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10, 1.0), # criteria
                cv2.KMEANS_PP_CENTERS]  # flag

    def __init__(self, slice_size = 10, kmeans_filter = KMEANSFILTER):
        self.slice_size = slice_size
        self.k, self.iter_num, self.criteria, self.flag = kmeans_filter

    @time_func
    def slice(self, image):
        arr_size = tuple(int(element / self.slice_size) for element in image.shape)
        col_array = np.array_split(image, arr_size[0], axis=0)
        img_array = []
        for col in col_array:
            img_array.append(np.array_split(col,arr_size[1],axis=1))
        return img_array

    @time_func
    def combineRow(self, imgs):
        combined_img = imgs[0]
        for img in imgs[1:]:
            combined_img = np.concatenate((combined_img,img),axis=1)
        return combined_img

    @time_func
    def combineCol(self, imgs):
        combined_img = imgs[0]
        for img in imgs[1:]:
            combined_img = np.concatenate((combined_img,img),axis=0)
        return combined_img

    @time_func
    def reduce_image_color(self, image, ncluster = None):
        img_kmean = image.reshape(-1,3)
        img_kmean = np.float32(img_kmean)
        if ncluster is not None:
            ret,label,center = cv2.kmeans(img_kmean,ncluster,None,self.criteria,self.iter_num,self.flag)
        else:
            ret,label,center = cv2.kmeans(img_kmean,self.k,None,self.criteria,self.iter_num,self.flag)
        center = np.uint8(center)
        res = center[label.flatten()]
        res2 = res.reshape((image.shape))
        return res2, center

class Path_Properties:
    #PATH_PROPERTY_THRESHOLDS = np.array([10, 20, np.pi/6, 0.02, 10, 10, 10, 10, 10, 10]) # difference in [path_color, background_color, path_theta, size, location_x_bot, location_y_bot, location_x,top, location_y_top]
    PATH_PROPERTY_THRESHOLDS = np.array([100, 200, 100, 2, 1000, 1000, 1000, 1000, 1000, 1000]) # no threshold
    PROPERTY_TAGS = ["path_color", "background_color", "bot2top_theta", "path_size", "path_x", "path_y", "x_bot", "y_bot", "x_top", "y_top"]
    def __init__(self, path_properties = None):
        self.confidence = 0.5

        if path_properties is not None:
            self.properties = path_properties
            return
            
        self.path_color, self.background_color = None, None
        self.path_theta, self.size = None, None
        self.x, self.y, self.x_bot, self.y_bot, self.x_top, self.y_top = None, None, None, None, None, None

        self.properties = np.array([self.path_color, self.background_color, self.path_theta, self.size, self.x, self.y, self.x_bot, self.y_bot, self.x_top, self.y_top])

    # pass in a numpy array of properties
    def compareAndUpdate(self, path_properties):
        # within threshold
        if None in self.properties:
            self.properties = path_properties
            return True
        if self.withinThreshold(path_properties, self.properties, self.PATH_PROPERTY_THRESHOLDS):
            self.properties = path_properties
            if self.confidence < 1: 
                self.confidence += 0.01
            return True
        # outside of threshold
        if self.confidence > 0: 
            self.confidence -= 0.01
        return False

    def withinThreshold(self, val1, val2, thres):
        #print(abs(np.subtract(val1, val2)))
        return (abs(np.subtract(val1, val2)) < thres).all() # return True only when all properties are under the threshold

SCALING_FACTOR = 0.5

NOISE_PROPORTION = 0.01 # threshold % of the image as path (less means it is noise)
FORWARD_DEFAULT = [0,-1] # image up is forward

# thresholds
PATH_COLOR_LOW_THRES, PATH_COLOR_UP_THRES = 60, 85
PATH_WIDTH_LOW_THRES, PATH_WIDTH_UP_THRES = 70, 400      # between means it is path
NUM_OF_COLORS = 4   # depends on the situation (4 or higher if there are random stuff and opposite colors of tiles)

WAIT_KEY = 0 # 0 to wait key press
### 
#   Functions
###
# input: binary image
# output: mean: center of the coordinates, 
#         pca_vector: [[PC2_x, PC1_x], [PC2_y, PC1_y]]
@time_func
def Path_PCA(image):                       # definition method
    pca_vector = []
    #image = cv2.resize(image,IMAGE_SIZE)
    coords_data = np.array(cv2.findNonZero(image)).T.reshape((2,-1))            # 2 x n matrix of coords [[x1,x2,...],[y1,y2,...]]
    mean = np.mean(coords_data,axis=1,keepdims=True)                         # center of coords
    cov_mat = np.cov(coords_data - mean, ddof = 1)              # find covariance
    pca_val, pca_vector = np.linalg.eig(cov_mat)                # find eigen vectors (also PCA first and second component)
    return mean, pca_vector, pca_val

# changes the value above the line in an image
# input: image_mask, initial_coord[x,y], slope[x,y], value= 0,1 (for binary masking)
@time_func
def set_mask(image_mask, initial_coord, slope, value):
    # line_y = mx+b
    # b = line_y - mx
    m = slope[1] / slope[0]
    b = initial_coord[1] - m * initial_coord[0]
    for x in range(image_mask.shape[1]):
        # compute y
        line_y = int(round(m*x + b))
        # bound y within image height
        if line_y > image_mask.shape[0]: line_y = image_mask.shape[0]
        if line_y <= 0: line_y = 1
        # change value of under the line (top of the image)
        image_mask[0:line_y, x] = value 
    return image_mask

# just for finding the place to draw the circle
def compute_location(pca_cent, pca_dir, scale = 10):
    return (int(pca_cent[0] + scale * pca_dir[0]),
            int(pca_cent[1] + scale * pca_dir[1]))

# compute angle between two vectors
# arccos((unit_a dot unit_b))
@time_func
def compute_angle(v_1, v_2):
    unit_v_1 = v_1 / np.linalg.norm(v_1)
    unit_v_2 = v_2 / np.linalg.norm(v_2)
    return np.arccos(np.dot(unit_v_1,unit_v_2))
@time_func
def compute_slope(p_1, p_2):
    return (p_2[0] - p_1[0]), (p_2[1] - p_1[1])


if __name__ == '__main__':
    path_dirs = ['/home/lixin/Projects/GitHubProjects/PathProject/Data/path_left_up_down.mp4',
                '/home/lixin/Projects/GitHubProjects/PathProject/Data/path_mid_left_right.mp4',
                '/home/lixin/Projects/GitHubProjects/PathProject/Data/path_mid_up_down.mp4',
                '/home/lixin/Projects/GitHubProjects/PathProject/Data/path_top_left_right.mp4',
                '/home/lixin/Projects/GitHubProjects/PathProject/Data/path_rotate.mp4',
                '/home/xing/TesterCodes/OpenCV/PathProject/Data/path_rotate.mp4',
                '/home/xing/TesterCodes/OpenCV/PathProject/Data/maryland-pool-img_SFmnGKCf.mp4',
                '/home/xing/TesterCodes/OpenCV/PathProject/Data/path_left_up_down.mp4',
                '/home/xing/TesterCodes/OpenCV/PathProject/Data/maryland-pool-img-4_bPA7wbk1.mp4',
                '/home/xing/TesterCodes/OpenCV/PathProject/Data/auto_path.avi',
                '/home/xing/TesterCodes/OpenCV/PathProject/Data/manual_path_edited.mp4']
    path_file_select = 10
    
    test_prep = ImagePrep(slice_size = 25)
    path_object = Path_Properties()
    out_video = cv2.VideoWriter("path_output.avi",cv2.VideoWriter_fourcc('M','J','P','G'), 10, (320,240))

    cap = cv2.VideoCapture(path_dirs[path_file_select])
    ####
    #   Filter Image to Binary Image
    ####
    current_frame = 0
    print(cap.isOpened())
    while cap.isOpened():
        ret, frame = cap.read()
        
        width = int(frame.shape[1] * SCALING_FACTOR)
        height = int(frame.shape[0] * SCALING_FACTOR)
        
        frame = cv2.resize(frame, (width, height))
        frame = cv2.medianBlur(frame,5)

        test_slice_imgs = test_prep.slice(frame)
        test_kmeans = test_slice_imgs.copy()
        comb_row = [i for i in range(len(test_slice_imgs))]
        for i,row in enumerate(test_slice_imgs):
            for j,block in enumerate(row):
                test_kmeans[i][j], _ = test_prep.reduce_image_color(block, 2)
            comb_row[i] = (test_prep.combineRow(test_kmeans[i]))
        combined_filter = test_prep.combineCol(comb_row)

        #combined_filter[:,:,1:3] = 0    # only blue channel is relevant (clear GR in BGR)

        filter_final, colors = test_prep.reduce_image_color(combined_filter,NUM_OF_COLORS)  # reduce colors (background (black & white tiles) and path)


        ####
        #   Find Path Directions
        ####
        gray = cv2.cvtColor(filter_final, cv2.COLOR_BGR2GRAY) 

        # adaptively find the path color
        gray_colors, gray_counts = np.unique(gray.flatten(),return_counts=True)                 # find the colors and counts of each color
        print(gray_colors,gray_counts)
        
        if len(gray_colors) < 2:
            print("no path in image")
            continue
        current_frame += 1

        img_size = sum(gray_counts)
        background_color = gray_colors[np.argsort(gray_counts)[-1]]             # most common color
        gray_counts[gray_counts < img_size * NOISE_PROPORTION] = img_size       # mark noise color (takse too little of the image)
        out_of_thresh = 0
        for i,color in enumerate(gray_colors):                                  # mark white and black tiles
            if color < PATH_COLOR_LOW_THRES or color > PATH_COLOR_UP_THRES:
                gray_counts[i] = img_size
                out_of_thresh += 1
        
        path_color = gray_colors[np.argsort(gray_counts)[0]]    # find the least common color
        if out_of_thresh == NUM_OF_COLORS:
            closest_color_i = np.argmin(np.abs(gray_colors - (PATH_COLOR_LOW_THRES + PATH_COLOR_UP_THRES)/2))
            path_color = gray_colors[closest_color_i]
        path_size = gray_counts[np.argsort(gray_counts)[0]]
        print(gray_colors, gray_counts, path_color, background_color)
        # simple threshold, use the least frequent color (which should not be the background)
        thres = np.uint8(np.where(gray == path_color, 255, 0)) # produce binary image for the path color found
        #thres = cv2.erode(thres,(5,5), iterations=2)
        #thres = cv2.medianBlur(thres,3)                      # remove the edges

        #cv2.imshow('thres',thres)

        input_shape = thres.shape
        # compute Principle Components
        # to find line between two paths
        center1, pca_vector_1, pca_val = Path_PCA(thres)

        # display the overall PC
        path_hori_cent, path_vert_cent = int(center1[0][0]), int(center1[1][0])
        #pca1_loc = compute_location(center1[:,0],pca_vector_1[:,1])
        #pca2_loc = compute_location(center1[:,0],pca_vector_1[:,0])
        #cv2.circle(gray,(hori_cent, vert_cent),radius=3,color=(0))
        #cv2.circle(gray,pca1_loc,radius=3,color=(255))
        #cv2.circle(gray,pca2_loc,radius=3,color=(255))
        path_dir_first_pass = pca_vector_1[:,np.argmax(pca_val)]
        path_variance_first_pass = pca_vector_1[:,np.argmin(pca_val)]
        if path_dir_first_pass[1] > 0:
            path_dir_first_pass = -path_dir_first_pass
        print(path_dir_first_pass)
        print(compute_angle(FORWARD_DEFAULT, path_dir_first_pass))
        slice_dir = np.argmin(pca_val)  # slice by the minimum variance
        # Create the masks to separate two paths
        mask_one = np.ones(input_shape, dtype="uint8")                                   # generate mask
        mask_one = set_mask(mask_one, center1[:,0], pca_vector_1[:,slice_dir], 0)       # set above 0
        mask_two = np.zeros(input_shape, dtype="uint8")                                  # generate mask
        mask_two = set_mask(mask_two, center1[:,0], pca_vector_1[:,slice_dir], 1)       # set above 1
        # generate the two path segments
        bottom_path = cv2.bitwise_and(thres,thres,mask=mask_one)
        top_path = cv2.bitwise_and(thres,thres,mask=mask_two)
        overall_path = cv2.bitwise_or(bottom_path,top_path)
        display_image_left = test_prep.combineCol([filter_final,cv2.cvtColor(gray,cv2.COLOR_GRAY2BGR)])
        display_image_middle = test_prep.combineCol([cv2.cvtColor(top_path,cv2.COLOR_GRAY2BGR), cv2.cvtColor(bottom_path,cv2.COLOR_GRAY2BGR)])
        
        #cv2.imshow('mask2_path',top_path)

        # Compute Principle Components for both path segments (center point(mean), direction vector(eigvec), variance vector(eigval))
        path_center1, path_direction1, pca_val1 = Path_PCA(bottom_path)
        path_center2, path_direction2, pca_val2 = Path_PCA(top_path)
        # select highest variance for each(eigenvalue)
        bot_dir = path_direction1[:,np.argmax(pca_val1)]
        top_dir = path_direction2[:,np.argmax(pca_val2)]
        # center of two segments (start location)
        bot_hori_cent, bot_vert_cent = int(path_center1[:,0][0]), int(path_center1[:,0][1])
        top_hori_cent, top_vert_cent = int(path_center2[:,0][0]), int(path_center2[:,0][1])
        # compute end location of two segment directions
        bot_pca_1 = compute_location(path_center1.T[0],bot_dir, scale = 20)
        top_pca_1 = compute_location(path_center2.T[0],top_dir, scale = 20)
        path_direction = compute_slope((bot_hori_cent, bot_vert_cent),(top_hori_cent, top_vert_cent))
        # find angle of bottom direction and top direction with respect to up
        bot_angle = compute_angle(FORWARD_DEFAULT, bot_dir)
        top_angle = compute_angle(FORWARD_DEFAULT, top_dir)
        path_angle = compute_angle(FORWARD_DEFAULT,path_direction)
        print("+x is right, -y is up")
        print(f"bot_dir[x,y]: {bot_dir}, top_dir[x,y]: {top_dir}, bot_angle(rad): {bot_angle}, top_angle: {top_angle}, path_angle: {path_angle}")

        #cv2.putText(frame,'bot',bot_pca_1,fontFace=cv2.FONT_HERSHEY_PLAIN,fontScale=1,color=(255,255,255))
        #cv2.putText(frame,'top',top_pca_1,fontFace=cv2.FONT_HERSHEY_PLAIN,fontScale=1,color=(255,255,255))
        #cv2.arrowedLine(frame,(bot_hori_cent, bot_vert_cent),bot_pca_1,
        #                color=(0,0,0),thickness=2,tipLength=0.5)
        #cv2.arrowedLine(frame,(top_hori_cent, top_vert_cent),top_pca_1,
        #                color=(0,0,0),thickness=2,tipLength=0.5)


        #rotated_bot_up = ndimage.rotate(frame,bot_angle*180/np.pi) # rotate the image so the bot is vertical
        #rotated_top_up = ndimage.rotate(frame,top_angle*180/np.pi) # rotate the image so the top is vertical
        #rotated_path = ndimage.rotate(frame,float(path_angle)*180/np.pi)   
        #cv2.imshow('rotated', rotated_path)
        cv2.waitKey(WAIT_KEY)
        first_pass_end_location = compute_location((path_hori_cent, path_vert_cent), path_dir_first_pass,scale= np.max(pca_val)/np.sum(pca_val)*30)
        first_pass_variance_end_location = compute_location((path_hori_cent, path_vert_cent), path_variance_first_pass, scale = np.min(pca_val)/np.sum(pca_val)*30)
        cv2.arrowedLine(frame,(path_hori_cent, path_vert_cent),first_pass_end_location,
                        color=(0,0,255),thickness=1,tipLength=0.2)
        cv2.arrowedLine(frame,(path_hori_cent, path_vert_cent),first_pass_variance_end_location,
                        color=(0,255,0),thickness=1,tipLength=0.2)
        

        #    [path_color, background_color, theta, size, path_location, bot_location, top_location]
        current_path_properties = [path_color, background_color, path_angle, path_size/img_size, path_hori_cent, path_vert_cent, bot_hori_cent, bot_vert_cent, top_hori_cent, top_vert_cent]
        #print(current_path_properties)
        path_update = path_object.compareAndUpdate(current_path_properties)
        print(list(zip(path_object.PROPERTY_TAGS, path_object.properties)))

        # information on display
        pca_variance_thres = np.min(pca_val)
        if pca_variance_thres < PATH_COLOR_LOW_THRES or pca_variance_thres > PATH_WIDTH_UP_THRES or path_color < PATH_COLOR_LOW_THRES or path_color > PATH_COLOR_UP_THRES:
            cv2.putText(frame, "no path", (0,20), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(0,0,255))
            cv2.putText(frame, "color: {diff}    width: {var:.2f}".format(diff = path_color, var = pca_variance_thres), (0,40), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(0,0,255))
        else:
            cv2.putText(frame, "found path:", (0,20), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(0,255,0))
            cv2.putText(frame, "color: {diff}    width: {var:.2f}".format(diff = path_color, var = pca_variance_thres), (0,40), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(0,255,0))
            cv2.arrowedLine(frame,(bot_hori_cent, bot_vert_cent),(top_hori_cent, top_vert_cent),
                        color=(255,255,255),thickness=2,tipLength=0.2)

        # track bottom x
        if (path_object.properties[path_object.PROPERTY_TAGS.index("path_x")] < width/2):
            print("move left")
            cv2.putText(frame, "move left (x offset): {loc}".format(loc = path_object.properties[path_object.PROPERTY_TAGS.index("path_x")] - width/2),
                        (0,60), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(255,255,255))
        else:
            print("move right")
            cv2.putText(frame, "move right(x offset): {loc}".format(loc = path_object.properties[path_object.PROPERTY_TAGS.index("path_x")] - width/2),
                        (0,60), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(255,255,255))

        # top x - bottom x
        turn_direction = path_object.properties[path_object.PROPERTY_TAGS.index("x_top")] - path_object.properties[path_object.PROPERTY_TAGS.index("x_bot")]

        if (turn_direction > 0):
            print("rotate right")
            cv2.putText(frame, "rotate right(turn rad): {theta:.2f}".format(theta = path_object.properties[path_object.PROPERTY_TAGS.index("bot2top_theta")]),
                        (0,80), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(255,255,255))
        else:
            print("rotate left")
            cv2.putText(frame, "rotate left(turn rad): {theta:.2f}".format(theta = -path_object.properties[path_object.PROPERTY_TAGS.index("bot2top_theta")]),
                        (0,80), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(255,255,255))
        
        cv2.putText(frame, "frame: {cur_frame}".format(cur_frame = current_frame), (0,100), cv2.FONT_HERSHEY_PLAIN, fontScale=1, color=(255,255,255))
        display_image_right = test_prep.combineCol([cv2.cvtColor(overall_path, cv2.COLOR_GRAY2BGR), frame])
        display_image_overall = test_prep.combineRow([display_image_left, display_image_middle, display_image_right])
        cv2.imshow('final', display_image_overall)
        out_video.write(frame)
    cap.release()
    out_video.release()
    cv2.destroyAllWindows()
    cv2.waitKey(1)