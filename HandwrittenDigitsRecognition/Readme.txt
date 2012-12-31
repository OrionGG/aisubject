Folders:
doc: Manual of the practise.
HandwrittenDigitsRecognition: code of the practise.
HandwrittenDigitsRecognition.sln: Visual Studio solution.
Release.zip: Bins for the execution.

IMPORTANT: place the NIST files in the DataFiles Folder of the release.

for running the application:
HandwrittenDigitsRecognition.exe options

options (can be concatenated):
/train       : train a network configured in the config file
/savetrain   : save the train in a xml located in XMLPersistBasicNetwork param of the config file
/test        : test the XMLPersistBasicNetwork saved network

HandwrittenDigitsRecognition.exe.config Params:

TrainNISTImageFile: trainig file with the images.
TrainNISTLabeleFile: training file with the results.
TestNISTImageFile: test file with the images.
TestNISTLabeleFile: test files with the results.
HiddenLayerNeurons: hidden layers and neurons. Example <value>20;30</value> there would be two hidden layers, the first one with 20 neurons and the second one with 30.
ROW_COUNT: number of training cases. If one does not want to use every case in the training file.
INPUT_COUNT: entries of the neural network.
OUTPUT_COUNT: output of the neural network.
ITERATIONS; training iterations.
TrainCSVFile: file for saving the training data (not implemented inthis version).
TestCSVFile: file for saving the test data (not implemented inthis version).
XMLPersistBasicNetwork: file for saving and getting a neural network saved. If does not exist the nertwork will not be neither saved or retrieved,
MAX_iMAGE_DIMENSION: dimension of the images in the loaded files. Data neded to create regiond of the image.
LEARN_ERROR: Lerning error for the network.
MOMENTUM: Momentum for the network.
