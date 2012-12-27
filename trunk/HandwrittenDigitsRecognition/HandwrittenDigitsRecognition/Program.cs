using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace HandwrittenDigitsRecognition
{
    static class Program
    {
        private enum ArgsOptions
        {
            train,
            saveTrain,
            test
        }
     

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            switch (args.Length)
            {
                case 1:

                    break;
                default:
                    break;
            }
            if (args.Length > 0)
            {
                HandwrittenDigitsRecongnition.Calcule();
                Console.Read();
            }
            else
            {
                Console.WriteLine("Some options are needed:");
                Console.WriteLine("/train       : train a network configured in the config file");
                Console.WriteLine("/savetrain   : save the train in a xml located where XMLPersistBasicNetwork param of the config file");
                Console.WriteLine("/test        : test the XMLPersistBasicNetwork saved network");

            }
        }


    }
}
