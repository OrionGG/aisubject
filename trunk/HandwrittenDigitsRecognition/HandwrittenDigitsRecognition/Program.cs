using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using HandwrittenDigitsRecognition.Properties;
using Encog.Neural.Networks;
using Encog.Neural.Networks.Layers;
using Encog.Engine.Network.Activation;
using Encog.ML.Data;
using Encog.ML.Train;
using System.Diagnostics;
using Encog.ML.Data.Basic;
using Encog.Neural.Networks.Training.Propagation.Back;
using System.Text;
using Encog.Util;
using System.Collections;
using System.IO;

namespace HandwrittenDigitsRecognition
{
    static class Program
    {

        public const int ROW_COUNT = 6000;
        public const int INPUT_COUNT = 784;
        public const int OUTPUT_COUNT = 10;
        public const int ITERATIONS = 10;
        public const int MAX_POINT_VALUE = 255;

        public static long HandWrittenDigitsEncog(double[][] input, double[][] output)
        {
            var network = new BasicNetwork();
            network.AddLayer(new BasicLayer(null, true,
                                            INPUT_COUNT));
            
            string sHiddenLayerNeurons = Settings.Default.HiddenLayerNeurons;
            
            string[] aHiddenLayerNeurons = sHiddenLayerNeurons.Split(';');

            for (int i = 0; i < aHiddenLayerNeurons.Length; i++)
            {
                string sHiddenLayerNeuron=  aHiddenLayerNeurons[i];
                int iHiddenLayerNeuron = 0;
                int.TryParse(sHiddenLayerNeuron, out iHiddenLayerNeuron);
                if (iHiddenLayerNeuron != 0)
                {
                    network.AddLayer(new BasicLayer(new ActivationSigmoid(), true,
                                                    iHiddenLayerNeuron));
                }
            }
            network.AddLayer(new BasicLayer(new ActivationSigmoid(), false,
                                           OUTPUT_COUNT));
            network.Structure.FinalizeStructure();
            network.Reset();

            IMLDataSet trainingSet = new BasicMLDataSet(input, output);

            //// train the neural network
            IMLTrain train = new Backpropagation(network, trainingSet, 0.7, 0.7);

            var sw = new Stopwatch();
            sw.Start();
            //// run epoch of learning procedure
            for (int i = 0; i < ITERATIONS; i++)
            {
                train.Iteration();
            }
            sw.Stop();

            return sw.ElapsedMilliseconds;
        }

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            List<byte[,]> lTrainImage = NISTImage.Read(Settings.Default.TrainNISTImageFile);
            List<int> lTrainLabel = NISTLabel.Read(Settings.Default.TrainNISTLabeleFile);

            // initialize input and output values
            double[][] input = Generate(lTrainImage, ROW_COUNT, INPUT_COUNT);             
            double[][] output = Generate(lTrainLabel, ROW_COUNT, OUTPUT_COUNT);

            WriteToCSV(input, output, @"D:\Master Vision Artificial\AI\Projects\HandWrittenDigitsTraining.csv");
           


            for (int i = 0; i < 10; i++)
            {
                long time1 = HandWrittenDigitsEncog(input, output);
                var line = new StringBuilder();
                line.Append(@"Regular: ");
                line.Append(Format.FormatInteger((int)time1));

                Console.WriteLine(line.ToString());
            }

            int a=0;
        }

        private static double[][] Generate(List<byte[,]> lTrainImage, int iRow, int iInputCount)
        {
            int iRows = iRow;
            double[][] aResult = new double[iRows][];

            for (int i = 0; i < iRows && i < lTrainImage.Count; i++)
            {
                int iCols = lTrainImage[i].Length;
                aResult[i] = new double[iCols];

                int j = 0;
                foreach (byte oByte in lTrainImage[i])
                {
                    double dValue = (double)oByte / MAX_POINT_VALUE;
                    aResult[i][j] = dValue;
                    j++;
                }

            }

            return aResult;
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

        public static bool WriteToCSV(double[][] aInput, double[][] aOutput, string sOutputFileName)
        {
            if (!File.Exists(sOutputFileName))
            {
                try
                {
                    using (File.Create(sOutputFileName))
                    {
                    }
                }
                catch (Exception ex)
                {
                    return false;
                }
            }
            else
            {
                File.Delete(sOutputFileName);
            }



            using (System.IO.StreamWriter file = new System.IO.StreamWriter(sOutputFileName))
            {
                int iLength = 0;
                if (aInput.Length > 0 && aOutput.Length > 0 && aInput.Length == aOutput.Length)
                {
                    iLength = aInput.Length;
                }
                else
                {
                    return false;
                }

                for (int i = 0; i < iLength; i++)
                {
                    double[] aInputValues = aInput[i];
                    double[] aOutputValues = aOutput[i];

                    for (int j = 0; j < INPUT_COUNT; j++)
                    {
                        double dValue = aInputValues[j];
                        file.Write(dValue);
                        file.Write(";");
                    }

                    for (int j = 0; j < OUTPUT_COUNT; j++)
                    {
                        double dValue = aOutputValues[j];
                        file.Write(dValue);
                        file.Write(";");
                    }
                    file.WriteLine("");
                }
            }
            return true;
        }
    }
}
