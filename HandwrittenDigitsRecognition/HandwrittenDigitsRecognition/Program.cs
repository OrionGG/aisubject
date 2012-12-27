using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace HandwrittenDigitsRecognition
{
    static class Program
    {
        private const string TRAIN = "/train";
        private const string SAVETRAIN = "/savetrain";
        private const string TEST = "/test";
     

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            bool bExecution = true;
            switch (args.Length)
            {
                case 1:
                    switch (args[0].ToLower())
	                {
                        case TRAIN:
                            HandwrittenDigitsRecongnition.TrainNetwork();
                            break;
                        case TEST:
                            HandwrittenDigitsRecongnition.Test(HandwrittenDigitsRecongnition.ReadNetworkFromXML());
                            break;
		                default:
                            bExecution = false;
                            break;
	                }                    
                    break;
                case 2: 
                    if (args[0] == TRAIN && args[1] == TEST)
                    {
                        HandwrittenDigitsRecongnition.SaveNetworkInXML(HandwrittenDigitsRecongnition.TrainNetwork());
                    }
                    else
                    {
                        bExecution = false;
                    }
                    break;
                case 3:
                    if (args[0] == TRAIN && args[1] == TEST && args[2] == TEST)
                    {
                        HandwrittenDigitsRecongnition.SaveNetworkInXML(HandwrittenDigitsRecongnition.TrainNetwork());
                        HandwrittenDigitsRecongnition.Test(HandwrittenDigitsRecongnition.ReadNetworkFromXML());
                    }
                    else
                    {
                        bExecution = false;
                    }
                    break;
                default:
                    bExecution = false;
                    break;
            }

            if (!(args.Length > 0 && bExecution))
            {
                Console.WriteLine("Error: some options are needed");
                Console.WriteLine("/train       : train a network configured in the config file");
                Console.WriteLine("/savetrain   : save the train in a xml located in XMLPersistBasicNetwork param of the config file");
                Console.WriteLine("/test        : test the XMLPersistBasicNetwork saved network");

            }
        }


    }
}
