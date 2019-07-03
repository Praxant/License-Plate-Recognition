from flask import Flask,request,jsonify
import os
from sklearn.externals import joblib
from PIL import Image
import pickle
import string
import base64
from io import BytesIO
app = Flask(__name__)

@app.route("/hello",methods=['POST'])
def hello():
		    # load the model
		print("Loading model")
		content=request.get_json()
		im=Image.open(BytesIO(base64.b64decode(content["image"])))
		im.save("image.jpg")
		import SegmentCharacters
		filename = './finalized_model.sav' 
		model = pickle.load(open(filename, 'rb'))

		print('Model loaded. Predicting characters of number plate')
		classification_result = []
		for each_character in SegmentCharacters.characters:
		    # converts it to a 1D array
		    each_character = each_character.reshape(1, -1);
		    result = model.predict(each_character)
		    classification_result.append(result)

		print('Classification result')
		print(classification_result)

		plate_string = ''
		for eachPredict in classification_result:
		    plate_string += eachPredict[0]

		print('Predicted license plate')
		print(plate_string)

		# it's possible the characters are wrongly arranged
		# since that's a possibility, the column_list will be
		# used to sort the letters in the right order

		column_list_copy = SegmentCharacters.column_list[:]
		SegmentCharacters.column_list.sort()
		rightplate_string = ''
		for each in SegmentCharacters.column_list:
		    rightplate_string += plate_string[column_list_copy.index(each)]
		print('License plate')
		print(rightplate_string)
		return rightplate_string


if __name__ == '__main__':
    app.run(host='0.0.0.0')