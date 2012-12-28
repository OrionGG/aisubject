using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using HandwrittenDigitsRecognition.Properties;
using Encog.Neural.Networks;
using Encog.Neural.Networks.Layers;
using Encog.Engine.Network.Activation;
using Encog.ML.Data;
using Encog.ML.Train;
using System.Diagnostics;
using Encog.ML.Data.Basic;
using Encog.Neural.Networks.Training.Propagation.Back;
using Encog.Util;
using System.Collections;
using System.IO;

namespace HandwrittenDigitsRecognition
{
    class HandwrittenDigitsRecongnition
    {
        private const int MAX_POINT_VALUE = 255;
        private const int BASE = 2;
        private const int iMaxIterationNoImprove = 10;
        private const double dMinImproving = 0.01;


        #region Training

        public static BasicNetwork TrainNetwork(BasicNetwork oBasicNetwork)
        {
            int iRowCount = 0;
            int.TryParse(Settings.Default.ROW_COUNT, out iRowCount);
            Console.WriteLine("Rows Count: " + iRowCount);

            int iInputCount = 0;
            int.TryParse(Settings.Default.INPUT_COUNT, out iInputCount);
            Console.WriteLine("Input Count: " + iInputCount);

            int iOutputCount = 0;
            int.TryParse(Settings.Default.OUTPUT_COUNT, out iOutputCount);
            Console.WriteLine("Output Count: " + iOutputCount);


            List<byte[,]> lTrainImage = NISTImage.Read(Settings.Default.TrainNISTImageFile);
            List<int> lTrainLabel = NISTLabel.Read(Settings.Default.TrainNISTLabeleFile);

            // initialize input and output values
            double[][] input = Generate(lTrainImage, iRowCount, iInputCount);
            double[][] output = Generate(lTrainLabel, iRowCount, iOutputCount);

            double dLearnError = 0.0;
            double.TryParse(Settings.Default.LEARN_ERROR.Replace(".", ","), out dLearnError);
            Console.WriteLine("Learn Error: " + dLearnError);

            double dMomentum = 0.0;
            double.TryParse(Settings.Default.MOMENTUM.Replace(".", ","), out dMomentum);
            Console.WriteLine("Momentum: " + dMomentum);

            long time1 = HandWrittenDigitsEncog(input, output, ref oBasicNetwork, dLearnError, dMomentum);
            Console.WriteLine("Time(seconds):" + time1/1000);

            return oBasicNetwork;
        }


        public static long HandWrittenDigitsEncog(double[][] input, double[][] output, ref BasicNetwork network, double dLearnError, double dMomentum)
        {
            if (network == null)
            {
                network = CreateNetwork(input, output, network);
            }

            IMLDataSet trainingSet = new BasicMLDataSet(input, output);

            //// train the neural network
            IMLTrain train = new Backpropagation(network, trainingSet, dLearnError, dMomentum);

            var sw = StartTraining(train);

            return sw.ElapsedMilliseconds;
        }

        private static Stopwatch StartTraining(IMLTrain train)
        {
            var sw = new Stopwatch();
            sw.Start();

            int iMinErrorIteration = 0;
            double dMinError = 1.0;

            double error1 = 1.0;
            int iIterationNoImprove = 0;

            //// run epoch of learning procedure
            int iIterations = 0;
            int.TryParse(Settings.Default.ITERATIONS, out iIterations);
            Console.WriteLine("Iterations: " + iIterations);

            int iExitIteration = iIterations;

            bool bImproving = true;
            for (int i = 0; i < iIterations && bImproving; i++)
            {
                train.Iteration();
                
                RefreshMinError(train, ref iMinErrorIteration, ref dMinError, i);


                double error2 = train.Error;
                double improve = (error1 - error2) / error1;

                bImproving = CheckImproving(train, i, improve, ref error1, ref iIterationNoImprove, ref iExitIteration);


                Console.WriteLine("Error: " + train.Error + "; Improve: " + improve + "; Iteration: " + i + "; Time: " + sw.ElapsedMilliseconds);

            }

            Console.WriteLine("Min Error Iteration:" + iMinErrorIteration + "; Error: " + dMinError + "; Iteration Exit: " + iExitIteration);

            sw.Stop();
            return sw;
        }

        private static bool CheckImproving(IMLTrain train, int i, double improve, ref double error1, ref int iIterationNoImprove, ref int iExitIteration)
        {
            bool bImproving = true;
            if (improve < dMinImproving)
            {
                iIterationNoImprove++;
            }
            else
            {
                iIterationNoImprove = 0;
            }

            if (iMaxIterationNoImprove < iIterationNoImprove && improve < 0)
            {
                iExitIteration = i;
                bImproving = false;
            }


            return bImproving;
        }

        private static void RefreshMinError(IMLTrain train, ref int iMinErrorIteration, ref double dMinError, int i)
        {
            if (train.Error < dMinError)
            {
                iMinErrorIteration = i;
                dMinError = train.Error;
            }
        }


        private static BasicNetwork CreateNetwork(double[][] input, double[][] output, BasicNetwork network)
        {
            network = new BasicNetwork();
            network.AddLayer(new BasicLayer(null, true,
                                        input[0].Length));

            string sHiddenLayerNeurons = Settings.Default.HiddenLayerNeurons;

            string[] aHiddenLayerNeurons = sHiddenLayerNeurons.Split(';');

            for (int i = 0; i < aHiddenLayerNeurons.Length; i++)
            {
                string sHiddenLayerNeuron = aHiddenLayerNeurons[i];
                int iHiddenLayerNeuron = 0;
                int.TryParse(sHiddenLayerNeuron, out iHiddenLayerNeuron);
                if (iHiddenLayerNeuron != 0)
                {
                    network.AddLayer(new BasicLayer(new ActivationSigmoid(), true,
                                                    iHiddenLayerNeuron));
                }
            }
            network.AddLayer(new BasicLayer(new ActivationSigmoid(), false,
                                            output[0].Length));
            network.Structure.FinalizeStructure();
            network.Reset();
            return network;
        }


        #endregion

        #region Testing


        public static bool Test(BasicNetwork network)
        {
            bool bResult = false;
            int iInputCount = 0;
            int.TryParse(Settings.Default.INPUT_COUNT, out iInputCount);

            int iOutputCount = 0;
            int.TryParse(Settings.Default.OUTPUT_COUNT, out iOutputCount);

            List<byte[,]> lTestImage = NISTImage.Read(Settings.Default.TestNISTImageFile);
            List<int> lTestLabel = NISTLabel.Read(Settings.Default.TestNISTLabeleFile);

            // initialize input and output values
            double[][] inputTest = Generate(lTestImage, lTestImage.Count, iInputCount);
            double[][] outputTest = Generate(lTestLabel, lTestLabel.Count, iOutputCount);

            TestBPROP(inputTest, outputTest, network);

            bResult = true;
            return bResult;
        }

        private static void TestBPROP(double[][] input, double[][] output, BasicNetwork network)
        {
            IMLDataSet trainingData = new BasicMLDataSet(input, output);

            IMLTrain bprop = new Backpropagation(network, trainingData, 0.7, 0.9);
            TestTraining(bprop, 0.01);
        }

        private static void TestTraining(IMLTrain train, double requiredImprove)
        {

            Console.WriteLine("Running test ...");
            train.Iteration();
            double dError = train.Error;

            Console.WriteLine("Error : " + dError);
        }


        #endregion

        #region GenerationIO

        private static double[][] Generate(List<byte[,]> lTrainImage, int iRow, int iInputCount)
        {
            int iRows = iRow;
            double[][] aResult = new double[iRows][];

            int iImageDimension =  0;
            int.TryParse(Settings.Default.MAX_iMAGE_DIMENSION, out iImageDimension);
            Console.WriteLine("Image Dimension: " + iImageDimension);


            for (int i = 0; i < iRows && i < lTrainImage.Count; i++)
            {

                int iWIDTH_RESIZE = 1;
                int iHEIGHT_RESIZE = 1;

                try
                {
                    iWIDTH_RESIZE = (int)Math.Sqrt(Math.Pow(iImageDimension, 2) / iInputCount);
                    iHEIGHT_RESIZE = (int)Math.Sqrt(Math.Pow(iImageDimension, 2) / iInputCount);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Error trying to divide image: " + ex.Message);
                }
                    
                int iCols = lTrainImage[i].Length / (iWIDTH_RESIZE * iHEIGHT_RESIZE);
                aResult[i] = new double[iCols];

                DivideImages(lTrainImage, aResult, i, iWIDTH_RESIZE, iHEIGHT_RESIZE);

            }

            return aResult;
        }



        private static void DivideImages(List<byte[,]> lTrainImage, double[][] aResult, int i, int iWIDTH_RESIZE, int iHEIGHT_RESIZE)
        {
            byte[,] aByte = lTrainImage[i];
            int l = 0;

            for (int j = 0; j < aByte.GetLength(0) / iHEIGHT_RESIZE; j++)
            {
                for (int k = 0; k < aByte.GetLength(1) / iWIDTH_RESIZE; k++)
                {
                    double dValue = 0;

                    int iInitialjj = j * iHEIGHT_RESIZE;
                    for (int jj = 0; jj < iHEIGHT_RESIZE; jj++)
                    {
                        int iInitialkk = k * iWIDTH_RESIZE;
                        for (int kk = 0; kk < iWIDTH_RESIZE; kk++)
                        {
                            double dPower = Math.Pow(BASE, (kk + (iWIDTH_RESIZE * jj)));
                            dValue += (double)aByte[iInitialjj + jj, iInitialkk + kk] * dPower;
                        }
                    }

                    double dMaxPowerValue = Math.Pow(BASE, (iHEIGHT_RESIZE * iWIDTH_RESIZE)) - 1;
                    dValue = dValue / (dMaxPowerValue * MAX_POINT_VALUE);
                    aResult[i][l] = dValue;
                    l++;
                }
            }
        }

        private static double[][] Generate(List<int> lTrainLabel, int iRow, int iOutputCount)
        {
            int iRows = iRow;
            double[][] aResult = new double[iRows][];
            for (int i = 0; i < iRows && i < lTrainLabel.Count; i++)
            {
                int iCols = iOutputCount;
                aResult[i] = new double[iCols];

                int iOutput = lTrainLabel[i];
                aResult[i][iOutput] = 1.0;

            }

            return aResult;
        }


        #endregion


        #region CSVCreaion

        public static bool WriteToCSV(double[][] aInput, double[][] aOutput, string sOutputFileName)
        {

            bool bResult = CreateFileIfNotExists(sOutputFileName);

            if (bResult)
            {
                bResult = WriteInputInCSV(aInput, aOutput, sOutputFileName, bResult);
            }

            return bResult;
        }

        private static bool WriteInputInCSV(double[][] aInput, double[][] aOutput, string sOutputFileName, bool bResult)
        {
            using (System.IO.StreamWriter file = new System.IO.StreamWriter(sOutputFileName))
            {
                int iLength = 0;
                if (aInput.Length > 0 && aOutput.Length > 0 && aInput.Length == aOutput.Length)
                {
                    iLength = aInput.Length;
                }
                else
                {
                    bResult = false;
                }

                for (int i = 0; i < iLength; i++)
                {
                    double[] aInputValues = aInput[i];
                    double[] aOutputValues = aOutput[i];

                    for (int j = 0; j < aInputValues.Length; j++)
                    {
                        double dValue = aInputValues[j];
                        file.Write(dValue);
                        file.Write(";");
                    }

                    for (int j = 0; j < aOutputValues.Length; j++)
                    {
                        double dValue = aOutputValues[j];
                        file.Write(dValue);
                        file.Write(";");
                    }
                    file.WriteLine("");
                }
            }
            return bResult;
        }

        private static bool CreateFileIfNotExists(string sOutputFileName)
        {
            bool bResult = true;

            if (!File.Exists(sOutputFileName))
            {
                try
                {
                    using (File.Create(sOutputFileName))
                    {
                    }
                }
                catch (Exception)
                {
                    bResult = false;
                }
            }
            else
            {
                File.Delete(sOutputFileName);
            }

            return bResult;
        }


        #endregion

        #region SaveFiles
        public static BasicNetwork ReadNetworkFromXML()
        {
            BasicNetwork network = null;
            string sXMLPersistBasicNetwork = GetXmlBasicNetworkFile();

            PersistBasicNetwork oPersistBasicNetwork = new PersistBasicNetwork();

            if (File.Exists(sXMLPersistBasicNetwork))
            {
                using (FileStream fs = File.OpenRead(sXMLPersistBasicNetwork))
                {
                    network = (BasicNetwork)oPersistBasicNetwork.Read(fs);
                }
            }
            return network;

        }


        public static bool SaveNetworkInXML(BasicNetwork network)
        {
            bool bResult = false;
            string sXMLPersistBasicNetwork = GetXmlBasicNetworkFile();

            using (FileStream fs = File.Create(sXMLPersistBasicNetwork))
            {

                PersistBasicNetwork oPersistBasicNetwork = new PersistBasicNetwork();
                oPersistBasicNetwork.Save(fs, network);
                bResult = true;
            }
            return bResult;
        }

        private static string GetXmlBasicNetworkFile()
        {
            string sXMLPersistBasicNetwork = Settings.Default.XMLPersistBasicNetwork.Trim();
            if (string.IsNullOrEmpty(Settings.Default.XMLPersistBasicNetwork.Trim()))
            {
                sXMLPersistBasicNetwork = Directory.GetCurrentDirectory();
                sXMLPersistBasicNetwork = Path.Combine(sXMLPersistBasicNetwork, "XMLPersistBasicNetwork.xml");
            }


            return sXMLPersistBasicNetwork;
        }


        private static bool SaveToCSV(double[][] input, double[][] output)
        {
            bool bResult = false;
            if (!String.IsNullOrEmpty(Settings.Default.TrainCSVFile.Trim()))
            {
                WriteToCSV(input, output, Settings.Default.TrainCSVFile);
                bResult = true;
            }
            return bResult;
        }
        #endregion
    }
}
