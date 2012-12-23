using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace HandwrittenDigitsRecognition
{
    class NISTLabel
    {
        public static List<int> Read(string sFileName)
        {
            List<int> lResutl = new List<int>();
            //Fragmento de código que lee las etiquetas correspondientes a cada imagen

            try
            {
                BinaryReader oBinaryReader = new BinaryReader(File.OpenRead(sFileName));
                int vacio1 = oBinaryReader.ReadInt32();
                int vacio2 = oBinaryReader.ReadInt32();
                int tipo = oBinaryReader.ReadInt32();  //Valdrá 8
                int dim = oBinaryReader.ReadInt32();   //Valdrá 1
                int numEtiquetas = oBinaryReader.ReadInt32() << 24 | oBinaryReader.ReadInt32() << 16 | oBinaryReader.ReadInt32() << 8 | oBinaryReader.ReadInt32();   //Valdrá 60000

                //Este bucle lee todas las etiquetas
                for (int c = 0; c < numEtiquetas; c++)
                {
                    int etiqueta = oBinaryReader.ReadInt32();
                    //Aqui ya tenemos la etiqueta de la imagen c-esima leída
                    lResutl.Add(etiqueta);

                }
            }
            catch (IOException ex)
            {
            }

            return lResutl;
        }
    }
}
