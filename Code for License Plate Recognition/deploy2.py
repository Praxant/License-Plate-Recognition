from flask import Flask,request,jsonify
import os
from sklearn.externals import joblib
from PIL import Image
import pickle
import string
import base64
from io import BytesIO

import numpy as np
from skimage.transform import resize
from skimage import measure
from skimage.measure import regionprops
import matplotlib.patches as patches
import matplotlib.pyplot as plt


from skimage.io import imread
from skimage.filters import threshold_otsu
import matplotlib.pyplot as plt
from skimage.transform import resize  
from flask import Flask,jsonify,request


from skimage import measure
from skimage.measure import regionprops
import matplotlib.pyplot as plt
import matplotlib.patches as patches


app = Flask(__name__)

def resize_if_necessary(image_to_resize):        
        height, width = image_to_resize.shape
        ratio = float(width) / height
        # if the image is too big, resize
        if width > 600:
            width = 600
            height = round(width / ratio)
            return resize(image_to_resize, (height, width))

        return image_to_resize

def validate_plate(candidates,license_plate_exact):    
        
        for each_candidate in candidates:
            height, width = each_candidate.shape
            each_candidate = inverted_threshold(each_candidate)           
            highest_average = 0
            total_white_pixels = 0
            for column in range(width):
                total_white_pixels += sum(each_candidate[:, column])
            
            average = float(total_white_pixels) / width
            if average >= highest_average:
                license_plate_exact = each_candidate
                highest_average = average

        return license_plate_exact



def inverted_threshold(grayscale_image):        
        threshold_value = threshold_otsu(grayscale_image)
        return grayscale_image > threshold_value



@app.route("/hello",methods=['POST'])
def hello():
		    # load the model
		print("Loading model")
		content=request.get_json()
		im=Image.open(BytesIO(base64.b64decode(content["image"])))
		im.save("image1.jpg")
		car_image = imread("image1.jpg", as_gray=True)		
		# it should be a 2 dimensional array
		print(car_image.shape)
		car_image=resize_if_necessary(car_image)		

		gray_car_image = car_image * 255
		#fig, (ax1, ax2) = plt.subplots(1, 2)
		#ax1.imshow(gray_car_image, cmap="gray")
		threshold_value = threshold_otsu(gray_car_image)
		binary_car_image = gray_car_image > threshold_value
		# print(binary_car_image)
		#ax2.imshow(binary_car_image, cmap="gray")
		# ax2.imshow(gray_car_image, cmap="gray")
		#plt.show()

		# CCA (finding connected regions) of binary image		
		# this gets all the connected regions and groups them together
		label_image = measure.label(binary_car_image)
		# getting the maximum width, height and minimum width and height that a license plate can be
		plate_dimensions = (0.03*label_image.shape[0], 0.08*label_image.shape[0], 0.15*label_image.shape[1], 0.3*label_image.shape[1])
		plate_dimensions2 = (0.08*label_image.shape[0], 0.2*label_image.shape[0], 0.15*label_image.shape[1], 0.4*label_image.shape[1])
		min_height, max_height, min_width, max_width = plate_dimensions
		plate_objects_cordinates = []
		plate_like_objects = []
		acc_licence = []
		#fig, (ax1) = plt.subplots(1)
		#ax1.imshow(gray_car_image, cmap="gray")
		flag =0
		# regionprops creates a list of properties of all the labelled regions
		for region in regionprops(label_image):		    
		    if region.area < 100:
		        #if the region is so small then it's likely not a license plate
		        continue
		        # the bounding box coordinates
		    min_row, min_col, max_row, max_col = region.bbox    

		    region_height = max_row - min_row
		    region_width = max_col - min_col     

		    # ensuring that the region identified satisfies the condition of a typical license plate
		    if region_height >= min_height and region_height <= max_height and region_width >= min_width and region_width <= max_width and region_width > region_height:
		        flag = 1
		        plate_like_objects.append(binary_car_image[min_row:max_row,
		                                  min_col:max_col])
		        plate_objects_cordinates.append((min_row, min_col,
		                                         max_row, max_col))
		        rectBorder = patches.Rectangle((min_col, min_row), max_col - min_col, max_row - min_row, edgecolor="red",
		                                       linewidth=2, fill=False)
		        #ax1.add_patch(rectBorder)
		        # let's draw a red rectangle over those regions
		if(flag == 1):
		    # print(plate_like_objects[0])
		    #plt.show()
		    print(" ")

		if(flag==0):
		    min_height, max_height, min_width, max_width = plate_dimensions2
		    plate_objects_cordinates = []
		    plate_like_objects = []
		    #fig, (ax1) = plt.subplots(1)
		    #ax1.imshow(gray_car_image, cmap="gray")
		    # regionprops creates a list of properties of all the labelled regions
		    for region in regionprops(label_image):
		        if region.area < 50:
		            #if the region is so small then it's likely not a license plate
		            continue
		            # the bounding box coordinates
		        min_row, min_col, max_row, max_col = region.bbox
		        
		        region_height = max_row - min_row
		        region_width = max_col - min_col         

		        # ensuring that the region identified satisfies the condition of a typical license plate
		        if region_height >= min_height and region_height <= max_height and region_width >= min_width and region_width <= max_width and region_width > region_height:
		            # print("hello")
		            plate_like_objects.append(binary_car_image[min_row:max_row,
		                                      min_col:max_col])
		            plate_objects_cordinates.append((min_row, min_col,
		                                             max_row, max_col))
		            rectBorder = patches.Rectangle((min_col, min_row), max_col - min_col, max_row - min_row, edgecolor="red",
		                                           linewidth=2, fill=False)
		            #ax1.add_patch(rectBorder)
		            # let's draw a red rectangle over those regions
		    # print(plate_like_objects[0])
		    #plt.show()
		number_of_candidates=len(plate_like_objects)
		print(number_of_candidates)
		if number_of_candidates == 0:
		    print("Licence plate could not be located")
		if number_of_candidates == 1:
		    acc_licence = inverted_threshold(plate_like_objects[0])
		else:
		    acc_licence = validate_plate(plate_like_objects,acc_licence)


		license_plate = np.invert(acc_licence)

		labelled_plate = measure.label(license_plate)

		#fig, ax1 = plt.subplots(1)
		#ax1.imshow(license_plate, cmap="gray")		
		character_dimensions = (0.35*license_plate.shape[0], 0.60*license_plate.shape[0], 0.05*license_plate.shape[1], 0.15*license_plate.shape[1])
		min_height, max_height, min_width, max_width = character_dimensions

		characters = []
		counter=0
		column_list = []
		for regions in regionprops(labelled_plate):
		    y0, x0, y1, x1 = regions.bbox
		    region_height = y1 - y0
		    region_width = x1 - x0

		    if region_height > min_height and region_height < max_height and region_width > min_width and region_width < max_width:
		        roi = license_plate[y0:y1, x0:x1]

		        # draw a red bordered rectangle over the character.
		        rect_border = patches.Rectangle((x0, y0), x1 - x0, y1 - y0, edgecolor="red",
		                                       linewidth=2, fill=False)
		        #ax1.add_patch(rect_border)

		        # resize the characters to 20X20 and then append each character into the characters list
		        resized_char = resize(roi, (20, 20))
		        characters.append(resized_char)

		        # this is just to keep track of the arrangement of the characters
		        column_list.append(x0)
		# print(characters)
		#plt.show()



		filename = './finalized_model.sav' 
		model = pickle.load(open(filename, 'rb'))

		print('Model loaded. Predicting characters of number plate')
		classification_result = []
		for each_character in characters:
		    # converts it to a 1D array
		    each_character = each_character.reshape(1, -1);
		    result = model.predict(each_character)
		    classification_result.append(result)

		#print('Classification result')
		#print(classification_result)

		plate_string = ''
		for eachPredict in classification_result:
		    plate_string += eachPredict[0]

		#print('Predicted license plate')
		#print(plate_string)

		# it's possible the characters are wrongly arranged
		# since that's a possibility, the column_list will be
		# used to sort the letters in the right order

		column_list_copy = column_list[:]
		column_list.sort()
		rightplate_string = ''
		for each in column_list:
		    rightplate_string += plate_string[column_list_copy.index(each)]
		print('License plate')
		print(rightplate_string)
		return rightplate_string


if __name__ == '__main__':
    app.run(host='0.0.0.0')